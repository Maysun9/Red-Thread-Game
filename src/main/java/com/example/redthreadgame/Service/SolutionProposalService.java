package com.example.redthreadgame.Service;
import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.SolutionProposalIn;
import com.example.redthreadgame.DTO.OUT.SolutionProposalOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Enums.SolutionProposalStatusType;
import com.example.redthreadgame.Model.CaseSolution;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Model.SessionPlayer;
import com.example.redthreadgame.Model.SolutionProposal;
import com.example.redthreadgame.Model.Suspect;
import com.example.redthreadgame.Repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolutionProposalService {

    private final SolutionProposalRepository solutionProposalRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final SuspectRepository suspectRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final CaseSolutionRepository caseSolutionRepository;
    private final HintService hintService;
    private final OpenAiService openAiService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;

    public List<SolutionProposalOut> getProposalsByGameSession(Integer gameSessionId) {
        List<SolutionProposalOut> proposals = new ArrayList<>();

        for (SolutionProposal s : solutionProposalRepository.findAllByGameSessionId(gameSessionId)) {
            proposals.add(modelMapper.map(s, SolutionProposalOut.class));
        }

        return proposals;
    }

    public void submitProposal(Integer gameSessionId, Integer playerId, Integer suspectId, SolutionProposalIn dto) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        Suspect suspect = suspectRepository.findById(suspectId)
                .orElseThrow(() -> new ApiException("Suspect not found"));

        checkCanPlay(gameSession, player);
        if (!suspect.getSuspectCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Suspect does not belong to this game session case");

        SolutionProposal proposal = modelMapper.map(dto, SolutionProposal.class);
        proposal.setStatus(SolutionProposalStatusType.PENDING);
        proposal.setAcceptCount(0);
        proposal.setRejectCount(0);
        proposal.setGameSession(gameSession);
        proposal.setPlayer(player);
        proposal.setSuspect(suspect);

        solutionProposalRepository.save(proposal);
    }// Creates a pending solution proposal after validating the session, player, and accused suspect.

    public String evaluateProposal(Integer proposalId) {
        SolutionProposal proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        GameSession gameSession = proposal.getGameSession();
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        Integer requiredVotes = (int) Math.ceil(gameSession.getPlayersCount() / 2.0);
        if (proposal.getAcceptCount() < requiredVotes)
            throw new ApiException("Proposal does not have majority accepted votes");

        CaseSolution solution = caseSolutionRepository.findCaseSolutionById(gameSession.getSessionCase().getId());
        if (solution == null)
            throw new ApiException("Case solution not found");

        List<SessionPlayer> joinedPlayers = new ArrayList<>();
        for (SessionPlayer s : sessionPlayerRepository.findAllByGameSessionId(gameSession.getId())) {
            if (s.getStatus() == SessionPlayerStatus.JOINED) {
                joinedPlayers.add(s);
            }
        }

        if (joinedPlayers.isEmpty())
            throw new ApiException("No joined players found in this game session");

        String analysisResult = openAiService.evaluateSolution(
                proposal.getReason(),
                proposal.getSuspect().getName(),
                proposal.getSuspect().getAge(),
                solution.getJustification()
        );

        boolean isCorrect;
        try {
            JsonNode analysisJson = new ObjectMapper().readTree(analysisResult);
            isCorrect = analysisJson.path("isCorrect").asBoolean();
        } catch (Exception e) {
            throw new ApiException("Failed to parse AI response");
        }

        if (!isCorrect) {
            proposal.setStatus(SolutionProposalStatusType.WRONG);
            gameSession.setScore(0);
            gameSession.setStatus(GameSessionStatusType.LOST);
            gameSession.setEndedAt(LocalDateTime.now());
            gameSessionRepository.save(gameSession);
            solutionProposalRepository.save(proposal);
            notifyPlayersWrongSolution(gameSession, proposal, joinedPlayers);
            return analysisResult;
        }

        Integer totalScore = hintService.calculateTotalScore(gameSession.getId());
        Integer playerScore = totalScore / joinedPlayers.size();

        proposal.setStatus(SolutionProposalStatusType.CORRECT);
        gameSession.setScore(totalScore);
        gameSession.setStatus(GameSessionStatusType.WON);
        gameSession.setEndedAt(LocalDateTime.now());

        for (SessionPlayer s : joinedPlayers) {
            Player player = s.getPlayer();
            player.setScore(player.getScore() + playerScore);
            playerRepository.save(player);
        }

        gameSessionRepository.save(gameSession);
        solutionProposalRepository.save(proposal);
        notifyPlayersCorrectSolution(gameSession, proposal, totalScore, playerScore, joinedPlayers);
        return analysisResult;
    }

    private void checkCanPlay(GameSession gameSession, Player player) {
        if (gameSession.getStatus() != GameSessionStatusType.IN_PROGRESS)
            throw new ApiException("Game session is not in progress");

        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(gameSession, player);
        if (sessionPlayer == null || sessionPlayer.getStatus() != SessionPlayerStatus.JOINED)
            throw new ApiException("Player is not joined in this game session");
    }

    private void notifyPlayersCorrectSolution(GameSession gameSession, SolutionProposal proposal, Integer totalScore, Integer playerScore, List<SessionPlayer> joinedPlayers) {
        for (SessionPlayer s : joinedPlayers) {
            Player player = s.getPlayer();

            emailService.send(
                    player.getEmail(),
                    "🏆 Case Closed Successfully!",
                    "🎉 Congratulations Detective " + player.getName() + "! 🎉\n\n" +
                            "The case has been solved successfully! 🕵️\n\n" +
                            "📂 Case: " + gameSession.getSessionCase().getTitle() + "\n" +
                            "😈 Culprit Identified: " + proposal.getSuspect().getName() + "\n" +
                            "⭐ Team Score: " + totalScore + "\n" +
                            "🏅 Your Score: " + playerScore + "\n" +
                            "⏰ Investigation Closed: " + gameSession.getEndedAt() + "\n\n" +
                            "🔍 Excellent detective work!\n" +
                            "🧩 Every clue mattered.\n" +
                            "🎯 Justice has been served.\n\n" +
                            "See you in the next mystery! 🚨\n\n" +
                            "🧵 Red Thread Team"
            );
        }
    }// Sends the win result and each player's earned score to all joined players.

    private void notifyPlayersWrongSolution(GameSession gameSession, SolutionProposal proposal, List<SessionPlayer> joinedPlayers) {
        for (SessionPlayer s : joinedPlayers) {
            Player player = s.getPlayer();

            emailService.send(
                    player.getEmail(),
                    "🚨 Investigation Failed",
                    "Detective " + player.getName() + ",\n\n" +
                            "❌ Case Status: FAILED\n\n" +
                            "📂 Case File: " + gameSession.getSessionCase().getTitle() + "\n" +
                            "🎭 Suspect Accused: " + proposal.getSuspect().getName() + "\n" +
                            "🏆 Team Score: 0\n" +
                            "⭐ Your Score: 0\n\n" +
                            "The evidence didn't lead to the correct suspect.\n" +
                            "😈 The real culprit has escaped justice.\n\n" +
                            "🧠 Review the clues more carefully next time.\n" +
                            "🔍 Every detail matters in an investigation.\n\n" +
                            "Better luck on your next case, Detective! 🕵️‍♀️\n\n" +
                            "🧵 Red Thread Investigation Department"
            );
        }
    }// Sends the loss result to all joined players when the proposed solution is incorrect.

    public SolutionProposalOut getActiveProposalBySession(Integer gameSessionId) {
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        for (SolutionProposal s : solutionProposalRepository.findAllByGameSessionId(gameSessionId)) {
            if (s.getStatus() == SolutionProposalStatusType.PENDING) {
                return modelMapper.map(s, SolutionProposalOut.class);
            }
        }

        throw new ApiException("No active proposal found");
    }// Find the current pending proposal that players still need to vote on

    public SolutionProposalOut getLastProposalResultBySession(Integer gameSessionId) {
        gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        List<SolutionProposal> proposals = solutionProposalRepository.findAllByGameSessionId(gameSessionId);

        if (proposals.isEmpty())
            throw new ApiException("No proposals found");

        proposals.sort((p1, p2) -> p2.getId().compareTo(p1.getId()));

        return modelMapper.map(proposals.get(0), SolutionProposalOut.class);
    }// Return the latest proposal result for reviewing the final or most recent team accusation
}

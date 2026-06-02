package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.SolutionProposalIn;
import com.example.redthreadgame.DTO.OUT.SolutionProposalOut;
import com.example.redthreadgame.Model.SolutionProposalModel;
import com.example.redthreadgame.Repository.SolutionProposalRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolutionProposalService {

    private final SolutionProposalRepository solutionProposalRepository;
//    private final GameSessionRepository gameSessionRepository;
//    private final PlayerRepository playerRepository;
//    private final SuspectRepository suspectRepository;
    private final ModelMapper modelMapper;

//    public List<SolutionProposalOut> getProposalsByGameSession(Integer gameSessionId) {
//        List<SolutionProposalOut> proposals = new ArrayList<>();
//
//        for (SolutionProposalModel s : solutionProposalRepository.findAllByGameSessionId(gameSessionId)) {
//            proposals.add(modelMapper.map(s, SolutionProposalOut.class));
//        }
//
//        return proposals;
//    }

//    public void submitProposal(Integer gameSessionId, Integer playerId, Integer suspectId, SolutionProposalIn dto) {
//        GameSessionModel gameSession = gameSessionRepository.findById(gameSessionId)
//                .orElseThrow(() -> new ApiException("Game session not found"));
//
//        PlayerModel player = playerRepository.findById(playerId)
//                .orElseThrow(() -> new ApiException("Player not found"));
//
//        SuspectModel suspect = suspectRepository.findById(suspectId)
//                .orElseThrow(() -> new ApiException("Suspect not found"));
//
//        SolutionProposalModel proposal = modelMapper.map(dto, SolutionProposalModel.class);
//        proposal.setStatus("PENDING");
//        proposal.setAcceptCount(0);
//        proposal.setRejectCount(0);
//        proposal.setGameSession(gameSession);
//        proposal.setPlayer(player);
//        proposal.setSuspect(suspect);
//
//        solutionProposalRepository.save(proposal);
//    }

    public void acceptProposal(Integer proposalId) {
        SolutionProposalModel proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        proposal.setAcceptCount(proposal.getAcceptCount() + 1);
        solutionProposalRepository.save(proposal);
    }

    public void rejectProposal(Integer proposalId) {
        SolutionProposalModel proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        proposal.setRejectCount(proposal.getRejectCount() + 1);
        solutionProposalRepository.save(proposal);
    }

    public void changeStatus(Integer proposalId, String status) {
        SolutionProposalModel proposal = solutionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ApiException("Solution proposal not found"));

        proposal.setStatus(status);
        solutionProposalRepository.save(proposal);
    }
}

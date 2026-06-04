package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.GameSessionIn;
import com.example.redthreadgame.DTO.OUT.GameSessionOut;
import com.example.redthreadgame.Enums.SessionPlayerRole;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Model.*;
import com.example.redthreadgame.Repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.redthreadgame.Enums.GameSessionStatusType.*;
import static com.example.redthreadgame.Enums.InvitationStatusType.ACCEPTED;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    private final ModelMapper modelMapper;
    private final GameSessionRepository gameSessionRepository;
    private final CaseRepository caseRepository;
    private final PlayerRepository playerRepository;
    private final InvitationRepository invitationRepository;
    private final SessionPlayerRepository sessionPlayerRepository;


    //BASIC CRUD
    public List<GameSessionOut> getAllGameSessions(){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAll()){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }

    public void addGameSession(Integer caseId,Integer playerId,GameSessionIn gameSessionIn){
        Case sessionCase = checkCase(caseId);
        Player player = checkPlayer(playerId);
        String code = generateCode();

        GameSession gameSession = modelMapper.map(gameSessionIn, GameSession.class);
        gameSession.setSessionCode(code);
        gameSession.setSessionCase(sessionCase);
        gameSession.setOwner(player);
        gameSession.setStatus(PENDING);

        gameSessionRepository.save(gameSession);
    }

    public void updateGameSession(Integer id, GameSessionIn gameSessionIn){
        GameSession oldGameSession = checkGameSession(id);

        oldGameSession.setPlayersCount(gameSessionIn.getPlayersCount());
        oldGameSession.setIsPrivate(gameSessionIn.getIsPrivate());

        gameSessionRepository.save(oldGameSession);
    }

    public void deleteGameSession(Integer id){
        GameSession gameSession = checkGameSession(id);
        gameSessionRepository.delete(gameSession);
    }


    //EXTRA ENDPOINTS
    public void updateStatus(Integer id){
        GameSession gameSession = checkGameSession(id);
        if(gameSession.getStatus() == PENDING )
            gameSession.setStatus(IN_PROGRESS);
        else if(gameSession.getStatus() == IN_PROGRESS)
            gameSession.setStatus(COMPLETED);
        else throw new ApiException("game session is completed you cannot change the status");

        gameSessionRepository.save(gameSession);
    }

    public List<GameSessionOut> getPublicGameSessions(){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAllByIsPrivateFalse()){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }

    public void joinPublicGameSession(Integer gameSessionId, Integer playerId) {
        Player player = checkPlayer(playerId);
        GameSession gameSession = checkGameSession(gameSessionId);

        // check session is public
        if (gameSession.getIsPrivate())
            throw new ApiException("This session is private");

        // check session status
        if (gameSession.getStatus() != PENDING)
            throw new ApiException("Session is not available to join");

        // check if already joined
        if (sessionPlayerRepository.existsByGameSessionAndPlayer(gameSession, player))
            throw new ApiException("You already joined this session");

        // check players count
        if (sessionPlayerRepository.countByGameSession(gameSession) >= gameSession.getPlayersCount())
            throw new ApiException("Game session is full");

        joinMember(player, gameSession);
    }

    public void joinPrivateGameSession(String sessionCode, Integer playerId) {
        // check player
        Player player = checkPlayer(playerId);

        // check session code
        GameSession gameSession = gameSessionRepository.findBySessionCode(sessionCode);
        if (gameSession == null)
            throw new ApiException("Invalid session code");

        // check session status
        if (gameSession.getStatus() == IN_PROGRESS)
            throw new ApiException("You can't enter, session is already in progress");

        // check invitation
        Invitation invitation = invitationRepository.findByGameSessionIdAndPlayerId(gameSession.getId(), playerId);
        if (invitation == null)
            throw new ApiException("You are not invited to this game session");

        // check if already joined
        if (sessionPlayerRepository.existsByGameSessionAndPlayer(gameSession, player))
            throw new ApiException("You already joined this session");

        // check invitation status
        if (invitation.getStatus() != ACCEPTED)
            throw new ApiException("You have not accepted the invitation yet");

        // check players count
        if (sessionPlayerRepository.countByGameSession(gameSession) >= gameSession.getPlayersCount())
            throw new ApiException("Game session is full");

        //join
        joinMember(player, gameSession);

    }

    public void startSession(Integer gameSessionId, Integer playerId) {
        GameSession gameSession = checkGameSession(gameSessionId);

        // check owner
        if (!gameSession.getOwner().getId().equals(playerId))
            throw new ApiException("Only the owner can start the session");

        // check status
        if (gameSession.getStatus() != PENDING)
            throw new ApiException("Session is already started or completed");

        gameSession.setStatus(IN_PROGRESS);
        gameSession.setStartedAt(LocalDateTime.now());

        gameSessionRepository.save(gameSession);
    }



    //HELPER METHODS
    private GameSession checkGameSession(Integer id){
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if(gameSession == null) throw new ApiException("Game Session not found"); //check game session

        return gameSession;
    }

    private Case checkCase(Integer id){
        Case sessionCase = caseRepository.findCaseById(id);
        if(sessionCase == null) throw new ApiException("Case not found"); //check case

        return sessionCase;
    }

    private Player checkPlayer(Integer id){
        Player player = playerRepository.findPlayerById(id);
        if(player == null) throw new ApiException("player not found"); //check player

        return player;
    }

    private String generateCode(){
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (gameSessionRepository.existsBySessionCode(code));
        return code;
    }

    private void joinMember(Player player, GameSession gameSession){
        SessionPlayer sessionPlayer = new SessionPlayer();
        sessionPlayer.setGameSession(gameSession);
        sessionPlayer.setPlayer(player);
        sessionPlayer.setRole(SessionPlayerRole.MEMBER);
        sessionPlayer.setStatus(SessionPlayerStatus.JOINED);
        sessionPlayer.setJoinedAt(LocalDateTime.now());

        sessionPlayerRepository.save(sessionPlayer);
    }
}

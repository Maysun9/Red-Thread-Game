package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.SessionPlayerIn;
import com.example.redthreadgame.DTO.OUT.SessionPlayerOut;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Model.SessionPlayer;
import com.example.redthreadgame.Enums.SessionPlayerRole;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.PlayerRepository;
import com.example.redthreadgame.Repository.SessionPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionPlayerService {

    private final SessionPlayerRepository sessionPlayerRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;

    public List<SessionPlayerOut> getAllSessionPlayers() {
        List<SessionPlayerOut> sessionPlayers = new ArrayList<>();

        for (SessionPlayer s : sessionPlayerRepository.findAll()) {
            sessionPlayers.add(modelMapper.map(s, SessionPlayerOut.class));
        }

        return sessionPlayers;
    }

    public List<SessionPlayerOut> getSessionPlayersByGameSession(Integer gameSessionId) {
        List<SessionPlayerOut> sessionPlayers = new ArrayList<>();

        for (SessionPlayer s : sessionPlayerRepository.findAllByGameSessionId(gameSessionId)) {
            sessionPlayers.add(modelMapper.map(s, SessionPlayerOut.class));
        }

        return sessionPlayers;
    }

    public List<SessionPlayerOut> getSessionPlayersByPlayer(Integer playerId) {
        List<SessionPlayerOut> sessionPlayers = new ArrayList<>();

        for (SessionPlayer s : sessionPlayerRepository.findAllByPlayerId(playerId)) {
            sessionPlayers.add(modelMapper.map(s, SessionPlayerOut.class));
        }

        return sessionPlayers;
    }

    public void addSessionPlayer(Integer gameSessionId, Integer playerId, SessionPlayerIn dto) {
        GameSession gameSession = gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new ApiException("Game session not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException("Player not found"));

        if (sessionPlayerRepository.findSessionPlayerByGameSessionIdAndPlayerId(gameSessionId, playerId) != null) {
            throw new ApiException("Player already joined this game session");
        }

        SessionPlayer sessionPlayer = new SessionPlayer();
        sessionPlayer.setRole(parseRole(dto.getRole()));
        sessionPlayer.setStatus(parseStatus(dto.getStatus()));
        sessionPlayer.setGameSession(gameSession);
        sessionPlayer.setPlayer(player);

        sessionPlayerRepository.save(sessionPlayer);
    }

    public void updateSessionPlayer(Integer sessionPlayerId, SessionPlayerIn dto) {
        SessionPlayer sessionPlayer = checkSessionPlayer(sessionPlayerId);

        sessionPlayer.setRole(parseRole(dto.getRole()));
        sessionPlayer.setStatus(parseStatus(dto.getStatus()));

        sessionPlayerRepository.save(sessionPlayer);
    }

    public void deleteSessionPlayer(Integer sessionPlayerId) {
        SessionPlayer sessionPlayer = checkSessionPlayer(sessionPlayerId);
        sessionPlayerRepository.delete(sessionPlayer);
    }

    private SessionPlayer checkSessionPlayer(Integer id) {
        SessionPlayer sessionPlayer = sessionPlayerRepository.findSessionPlayerById(id);
        if (sessionPlayer == null) throw new ApiException("Session player not found");
        return sessionPlayer;
    }

    private SessionPlayerRole parseRole(String role) {
        try {
            return SessionPlayerRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("role must be 'HOST' or 'MEMBER' only");
        }
    }

    private SessionPlayerStatus parseStatus(String status) {
        try {
            return SessionPlayerStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("status must be 'JOINED' or 'LEFT' only");
        }
    }
}

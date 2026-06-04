package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Model.SessionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionPlayerRepository extends JpaRepository<SessionPlayer, Integer> {

    SessionPlayer findSessionPlayerById(Integer id);

    boolean existsByGameSessionAndPlayer(GameSession gameSession, Player player);

    int countByGameSession(GameSession gameSession);

    List<SessionPlayer> findAllByGameSessionId(Integer gameSessionId);

    List<SessionPlayer> findAllByPlayerId(Integer playerId);
}

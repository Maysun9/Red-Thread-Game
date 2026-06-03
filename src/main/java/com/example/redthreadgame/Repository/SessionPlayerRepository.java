package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.SessionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionPlayerRepository extends JpaRepository<SessionPlayer, Integer> {

    SessionPlayer findSessionPlayerById(Integer id);
    SessionPlayer findSessionPlayerByGameSessionIdAndPlayerId(Integer gameSessionId, Integer playerId);
    List<SessionPlayer> findAllByGameSessionId(Integer gameSessionId);
    List<SessionPlayer> findAllByPlayerId(Integer playerId);
}

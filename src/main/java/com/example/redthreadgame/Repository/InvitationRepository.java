package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    Invitation findInvitationById(Integer id);

    List<Invitation> findAllByPlayerId(Integer playerId);

    @Query("SELECT i FROM Invitation i WHERE i.gameSession.id = ?1 AND i.player.id = ?2")
    Invitation findByGameSessionIdAndPlayerId(Integer gameSessionId, Integer playerId);
}

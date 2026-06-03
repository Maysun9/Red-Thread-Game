package com.example.redthreadgame.Model;

import com.example.redthreadgame.Enums.SessionPlayerRole;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "session_players",
        uniqueConstraints = @UniqueConstraint(columnNames = {"game_session_id", "player_id"})
)
public class SessionPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionPlayerRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionPlayerStatus status;

    @Column(columnDefinition = "datetime", nullable = false)
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "game_session_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;

}

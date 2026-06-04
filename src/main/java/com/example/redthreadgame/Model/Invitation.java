package com.example.redthreadgame.Model;

import com.example.redthreadgame.Enums.InvitationStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invitation", uniqueConstraints = @UniqueConstraint(columnNames = {"gameSession_id", "player_id"}))
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private InvitationStatusType status;

    @ManyToOne
    @JoinColumn(name = "gameSession_id")
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
}

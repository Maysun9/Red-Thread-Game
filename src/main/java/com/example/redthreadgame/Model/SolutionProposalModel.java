package com.example.redthreadgame.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solution_proposals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionProposalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "int", nullable = true)
    private Integer rejectCount;

    @Column(columnDefinition = "int", nullable = true)
    private Integer acceptCount;

    // TODO: uncomment when GameSession is ready
    // @ManyToOne
    // @JoinColumn(name = "game_session_id")
    // private GameSession gameSession;

    // TODO: uncomment when Player is ready
    // @ManyToOne
    // @JoinColumn(name = "player_id")
    // private Player player;

    // TODO: uncomment when Suspect is ready
    // @ManyToOne
    // @JoinColumn(name = "suspect_id")
    // private Suspect suspect;
}

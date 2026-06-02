package com.example.redthreadgame.Model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hints")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class HintModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // TODO: uncomment when GameSession is ready
    // @ManyToOne
    // @JoinColumn(name = "game_session_id")
    // private GameSession gameSession;
}

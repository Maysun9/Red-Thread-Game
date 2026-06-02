package com.example.redthreadgame.Model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // TODO: uncomment when GameSession is ready
    // @ManyToOne
    // @JoinColumn(name = "game_session_id")
    // private GameSession gameSession;

    // TODO: uncomment when Player is ready
    // @ManyToOne
    // @JoinColumn(name = "player_id")
    // private Player player;
}

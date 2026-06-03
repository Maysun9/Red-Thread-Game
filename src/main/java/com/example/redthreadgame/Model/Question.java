package com.example.redthreadgame.Model;

import com.example.redthreadgame.Enums.QuestionTargetType;
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
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(500)", nullable = false)
    private String questionText;

    @Column(columnDefinition = "varchar(1000)")
    private String answerText;

    @Column(columnDefinition = "varchar(255)")
    private String voiceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionTargetType targetType;

    @Column(columnDefinition = "datetime", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "game_session_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "witness_id", referencedColumnName = "id")
    private Witness witness;

    @ManyToOne
    @JoinColumn(name = "suspect_id", referencedColumnName = "id")
    private Suspect suspect;

}

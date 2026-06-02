package com.example.redthreadgame.Model;

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
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(20) check (status = 'PENDING' or status = 'IN_PROGRESS' or status = 'COMPLETED')", nullable = false)
    private String status;

    @Column(columnDefinition = "boolean", nullable = false)
    private Boolean isPrivate;

    @Column(columnDefinition = "int check (playersCount > 0 and playersCount < 6 )", nullable = false)
    private Integer playersCount;

    @Column(columnDefinition = "int default 0")
    private Integer questionsCount;

    @Column(columnDefinition = "int")
    private Integer score;

    @Column(columnDefinition = "datetime")
    private LocalDateTime startedAt;

    @Column(columnDefinition = "datetime")
    private LocalDateTime endedAt;
}

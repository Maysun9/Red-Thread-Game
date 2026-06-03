package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionOut {

    private Integer id;
    private String questionText;
    private String answerText;
    private String voiceUrl;
    private String targetType;
    private LocalDateTime createdAt;
    private PlayerOut player;
    private WitnessOut witness;
    private SuspectOut suspect;
}

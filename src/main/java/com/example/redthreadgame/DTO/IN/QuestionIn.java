package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionIn {

    @NotEmpty(message = "Question text is required")
    private String questionText;

    private String answerText;

    private String voiceUrl;
}

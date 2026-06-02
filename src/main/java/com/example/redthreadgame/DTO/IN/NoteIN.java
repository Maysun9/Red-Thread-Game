package com.example.redthreadgame.DTO.IN;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteIN {

    @NotEmpty(message = "Content is required")
    private String content;
}

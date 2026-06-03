package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionPlayerIn {

    @NotEmpty(message = "Role is required")
    private String role;

    @NotEmpty(message = "Status is required")
    private String status;
}

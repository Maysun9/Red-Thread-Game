package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SessionPlayerOut {

    private Integer id;
    private String role;
    private String status;
    private LocalDateTime joinedAt;
    private PlayerOut player;
}

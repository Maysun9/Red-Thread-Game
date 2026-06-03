package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProposalVoteOut {

    private Integer id;
    private String vote;
    private LocalDateTime votedAt;
    private PlayerOut player;
}

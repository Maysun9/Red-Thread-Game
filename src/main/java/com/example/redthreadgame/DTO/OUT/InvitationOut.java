package com.example.redthreadgame.DTO.OUT;


import com.example.redthreadgame.Enums.InvitationStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvitationOut {

    private Integer id;
    private InvitationStatusType status;
    private GameSessionOut gameSession;
    private PlayerOut player;
}

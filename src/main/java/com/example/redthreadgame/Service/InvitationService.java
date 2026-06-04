package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.OUT.InvitationOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Enums.InvitationStatusType;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Invitation;
import com.example.redthreadgame.Model.Player;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.InvitationRepository;
import com.example.redthreadgame.Repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import static com.example.redthreadgame.Enums.GameSessionStatusType.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.redthreadgame.Enums.InvitationStatusType.*;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final ModelMapper modelMapper;
    private final InvitationRepository invitationRepository;
    private final GameSessionRepository gameSessionRepository;
    private final PlayerRepository playerRepository;
    private final WhatsAppService whatsAppService;


    //BASIC CRUD
    public List<InvitationOut> getAllInvitations(){
        List<InvitationOut> invitations = new ArrayList<>();
        for(Invitation i: invitationRepository.findAll()){
            invitations.add(modelMapper.map(i, InvitationOut.class));
        }
        return invitations;
    }

    public void addInvitation(Integer ownerId, Integer gameSessionId, Integer playerId){
        GameSession gameSession = checkGameSession(gameSessionId);
        checkPlayer(ownerId);

        // check owner
        if(!gameSession.getOwner().getId().equals(ownerId))
            throw new ApiException("Only game session owner can invite other players");

        // check game session status
        if (gameSession.getStatus() != GameSessionStatusType.PENDING)
            throw new ApiException("Cannot invite players to a session that is not pending");

        Player player = checkPlayer(playerId);
        Invitation invitation = new Invitation(null, InvitationStatusType.PENDING, gameSession, player);

        invitationRepository.save(invitation);
    }

    public void deleteInvitation(Integer id){
        Invitation invitation = checkInvitation(id);
        invitationRepository.delete(invitation);
    }


    //EXTRA ENDPOINTS
    public void acceptInvitation(Integer gameSessionId, Integer playerId){
        //check player and game session
        Player player = checkPlayer(playerId);
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId);

        //get player invitation
        Invitation invitation = invitationRepository.findByGameSessionIdAndPlayerId(gameSessionId, playerId);

        //check if he is invited or not
        if(invitation == null)
            throw new ApiException("You are not invited to this game session");

        //check status
        if(invitation.getStatus() == ACCEPTED)
            throw new ApiException("You accepted this invite already");

        //accept invitation
        invitation.setStatus(ACCEPTED);
        invitationRepository.save(invitation);

        System.out.println("phoneNumber: " + player.getPhoneNumber());
        System.out.println("sessionCode: " + gameSession.getSessionCode());

        //send code to the player
        whatsAppService.sendSessionCode(player.getPhoneNumber(), gameSession.getSessionCode());
    }

    public void rejectInvitation(Integer gameSessionId, Integer playerId){
        //check player
        checkPlayer(playerId);

        //get player invitation
        Invitation invitation = invitationRepository.findByGameSessionIdAndPlayerId(gameSessionId, playerId);

        //check if he is invited or not
        if(invitation == null)
            throw new ApiException("You are not invited to this game session");

        //check status
        if(invitation.getStatus() == REJECTED)
            throw new ApiException("You rejected this invite already");

        //reject invitation
        invitation.setStatus(REJECTED);
        invitationRepository.save(invitation);
    }


    //HELPER METHODS
    private Invitation checkInvitation(Integer id){
        Invitation invitation = invitationRepository.findInvitationById(id);
        if(invitation == null) throw new ApiException("Invitation not found"); //check invitation

        return invitation;
    }

    private GameSession checkGameSession(Integer id){
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if(gameSession == null) throw new ApiException("Game Session not found"); //check game session

        return gameSession;
    }

    private Player checkPlayer(Integer id){
        Player player = playerRepository.findPlayerById(id);
        if(player == null) throw new ApiException("Player not found"); //check player

        return player;
    }


}

package com.example.redthreadgame.Controller;

import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.DTO.IN.SessionPlayerIn;
import com.example.redthreadgame.Service.SessionPlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/session-player")
@RestController
@RequiredArgsConstructor
public class SessionPlayerController {

    private final SessionPlayerService sessionPlayerService;

    //BASIC CRUD ENDPOINTS
    @GetMapping("/get")
    public ResponseEntity<?> getAllSessionPlayers() {
        return ResponseEntity.status(200).body(sessionPlayerService.getAllSessionPlayers());
    }

    @DeleteMapping("/delete/{sessionPlayerId}")
    public ResponseEntity<?> deleteSessionPlayer(@PathVariable Integer sessionPlayerId) {
        sessionPlayerService.deleteSessionPlayer(sessionPlayerId);
        return ResponseEntity.status(200).body(new ApiResponse("Session player deleted successfully"));
    }


    //EXTRA ENDPOINTS
    @GetMapping("/get-by-session/{gameSessionId}")
    public ResponseEntity<?> getSessionPlayersByGameSession(@PathVariable Integer gameSessionId) {
        return ResponseEntity.status(200).body(sessionPlayerService.getSessionPlayersByGameSession(gameSessionId));
    }

    @GetMapping("/get-by-player/{playerId}")
    public ResponseEntity<?> getSessionPlayersByPlayer(@PathVariable Integer playerId) {
        return ResponseEntity.status(200).body(sessionPlayerService.getSessionPlayersByPlayer(playerId));
    }
}

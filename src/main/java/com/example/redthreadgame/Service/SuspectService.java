package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.QuestionIn;
import com.example.redthreadgame.DTO.IN.SuspectIn;
import com.example.redthreadgame.DTO.OUT.VoiceAnswerOut;
import com.example.redthreadgame.DTO.OUT.SuspectOut;
import com.example.redthreadgame.Model.Case;
import com.example.redthreadgame.Model.CaseSolution;
import com.example.redthreadgame.Model.GameSession;
import com.example.redthreadgame.Model.Suspect;
import com.example.redthreadgame.Repository.GameSessionRepository;
import com.example.redthreadgame.Repository.SuspectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuspectService {
    private final ModelMapper modelMapper;
    private final SuspectRepository suspectRepository;
    private final CaseService caseService;
    private final OpenAiService openAiService;
    private final ElevenLabsService elevenLabsService;
    private final GameSessionRepository gameSessionRepository;


    public List<SuspectOut> getAllSuspects() {
      List<SuspectOut> suspects = new ArrayList<>();
      for (Suspect s : suspectRepository.findAll()) {
          suspects.add(modelMapper.map(s, SuspectOut.class));
      }
      return suspects;
  }
    public void addSuspect(Integer caseId, SuspectIn dto) {
         Case c = caseService.checkCase(caseId);
        Suspect suspect = modelMapper.map(dto, Suspect.class);
        suspect.setSuspectCase(c);

        suspectRepository.save(suspect);
    }
    public void updateSuspect(Integer id, SuspectIn dto) {
        Suspect old = checkSuspect(id);
        old.setName(dto.getName());
        old.setAge(dto.getAge());

        suspectRepository.save(old);
    }
    public void deleteSuspect(Integer id) {
        suspectRepository.delete(checkSuspect(id));
    }
    //---------------------------------------------------END CRED-----------------------------------------------------------------------

    public List<SuspectOut> getSuspectsDetails(Integer caseId) {
        caseService.checkCase(caseId);
        List<SuspectOut> suspects = new ArrayList<>();
        for (Suspect s : suspectRepository.findSuspectsBySuspectCaseId(caseId)) {
            suspects.add(modelMapper.map(s, SuspectOut.class));
        }
        return suspects;
    }

    //endpoint by mohammed
    public VoiceAnswerOut askSuspect(Integer suspectId, QuestionIn dto) {
        Suspect suspect = checkSuspect(suspectId);

        String prompt = "Suspect name: " + suspect.getName()
                + "\nSuspect age: " + suspect.getAge()
                + "\nPlayer question: " + dto.getQuestionText();

        String answer = openAiService.generateAnswer(prompt);
        if (answer == null || answer.isBlank()) {
            answer = "I do not have anything else to say right now.";
        }

        String audioFileName = elevenLabsService.generateVoice(answer);
        return new VoiceAnswerOut(answer, audioFileName);
    }

    public String checkCorrectSuspect(Integer gameSessionId, Integer suspectId, String playerReason) {
        GameSession gameSession = gameSessionRepository.findGameSessionById(gameSessionId);
        if (gameSession == null)
            throw new ApiException("Game session not found");

        CaseSolution caseSolution = gameSession.getSessionCase().getCaseSolution();
        if (caseSolution == null)
            throw new ApiException("Case solution not found");

        Suspect suspect = checkSuspect(suspectId);

        if (!suspect.getSuspectCase().getId().equals(gameSession.getSessionCase().getId()))
            throw new ApiException("Suspect does not belong to this case");

        String prompt = """
            You are a mystery game judge analyzing a detective team's performance.
            
            Correct solution: %s
            
            Player accused: %s
            Player reason: %s
            
            Respond in this exact JSON format:
            {
              "result": "You won! Great detective work!" or "You lost! Better luck next time!",
              "analysis": "2-3 sentences analyzing how well the team played",
              "focusOn": "1-2 specific areas to improve next time"
            }
            Return ONLY the JSON, no extra text.
            """.formatted(caseSolution.getJustification(), suspect.getName(), playerReason);

        String result = openAiService.generateAnswer(prompt);
        return result.trim().replace("```json", "").replace("```", "").trim();
    }

    //helper method
    public Suspect checkSuspect(Integer id) {
        Suspect suspect = suspectRepository.findSuspectById(id);
        if (suspect == null) throw new ApiException("Suspect not found");
        return suspect;
    }


}

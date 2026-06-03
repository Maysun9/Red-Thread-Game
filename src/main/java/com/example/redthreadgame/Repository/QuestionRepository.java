package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Question findQuestionById(Integer id);
    List<Question> findAllByGameSessionId(Integer gameSessionId);
    List<Question> findAllByPlayerId(Integer playerId);
    List<Question> findAllByWitnessId(Integer witnessId);
    List<Question> findAllBySuspectId(Integer suspectId);
}

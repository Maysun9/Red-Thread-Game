package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findAllByGameSessionId(Integer gameSessionId);
}

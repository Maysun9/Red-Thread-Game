package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.HintModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HintRepository extends JpaRepository<HintModel, Integer> {
}

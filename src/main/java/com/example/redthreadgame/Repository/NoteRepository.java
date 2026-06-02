package com.example.redthreadgame.Repository;

import com.example.redthreadgame.Model.NoteModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<NoteModel, Integer> {
}

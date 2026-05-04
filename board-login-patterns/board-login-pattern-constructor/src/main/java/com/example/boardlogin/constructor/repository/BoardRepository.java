package com.example.boardlogin.constructor.repository;

import com.example.boardlogin.constructor.domain.Board;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.author ORDER BY b.createdAt DESC")
    List<Board> findAllWithAuthor();

    @Query("SELECT b FROM Board b JOIN FETCH b.author WHERE b.id = :id")
    Optional<Board> findByIdWithAuthor(@Param("id") Long id);
}

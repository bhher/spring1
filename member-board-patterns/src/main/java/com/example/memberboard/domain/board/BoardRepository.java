package com.example.memberboard.domain.board;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {

	@Query("select distinct b from Board b join fetch b.author order by b.id desc")
	List<Board> findAllWithAuthor();

	@Query("select b from Board b join fetch b.author where b.id = :id")
	Optional<Board> findByIdWithAuthor(Long id);
}

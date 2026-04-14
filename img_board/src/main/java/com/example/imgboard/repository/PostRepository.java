package com.example.imgboard.repository;

import com.example.imgboard.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.images WHERE p.id = :id")
	Optional<Post> findByIdWithImages(@Param("id") Long id);
}

package com.example.board1.service;

import com.example.board1.domain.Post;
import com.example.board1.exception.PostNotFoundException;
import com.example.board1.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;

	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	public Page<Post> findAll(Pageable pageable) {
		return postRepository.findAll(pageable);
	}

	public Post findById(Long id) {
		return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
	}

	@Transactional
	public Post create(Post post) {
		return postRepository.save(post);
	}

	@Transactional
	public Post update(Long id, Post form) {
		Post post = findById(id);
		post.setTitle(form.getTitle());
		post.setContent(form.getContent());
		post.setAuthor(form.getAuthor());
		return post;
	}

	@Transactional
	public void delete(Long id) {
		if (!postRepository.existsById(id)) {
			throw new PostNotFoundException(id);
		}
		postRepository.deleteById(id);
	}
}

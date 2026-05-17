package com.example.roomfit.service;

import com.example.roomfit.domain.InteriorPost;
import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.PostImage;
import com.example.roomfit.domain.PostLike;
import com.example.roomfit.domain.PostStatus;
import com.example.roomfit.domain.PostType;
import com.example.roomfit.dto.InteriorPostFormDto;
import com.example.roomfit.exception.BusinessException;
import com.example.roomfit.exception.ResourceNotFoundException;
import com.example.roomfit.repository.CommentRepository;
import com.example.roomfit.repository.InteriorPostRepository;
import com.example.roomfit.repository.PostLikeRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class InteriorPostService {

	private final InteriorPostRepository interiorPostRepository;
	private final PostLikeRepository postLikeRepository;
	private final CommentRepository commentRepository;
	private final MemberService memberService;
	private final FileStorageService fileStorageService;

	public Page<InteriorPost> list(InteriorStyle style, Pageable pageable) {
		if (style == null) {
			return interiorPostRepository.findByStatus(PostStatus.VISIBLE, pageable);
		}
		return interiorPostRepository.findByStatusAndStyle(PostStatus.VISIBLE, style, pageable);
	}

	@Transactional
	public InteriorPost getDetail(Long id, Long memberId) {
		InteriorPost post = interiorPostRepository.findByIdAndStatus(id, PostStatus.VISIBLE)
				.orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
		post.increaseViewCount();
		return post;
	}

	@Transactional
	public Long create(Long memberId, InteriorPostFormDto dto, MultipartFile image) throws IOException {
		Member author = memberService.findById(memberId);
		InteriorPost post = InteriorPost.builder()
				.author(author)
				.style(dto.getStyle())
				.title(dto.getTitle())
				.content(dto.getContent())
				.roomSize(dto.getRoomSize())
				.budget(dto.getBudget())
				.hasFurnitureTag(dto.isHasFurnitureTag())
				.build();
		String path = fileStorageService.storeInteriorImage(image);
		if (path != null) {
			post.addImage(PostImage.builder().filePath(path).thumbnail(true).sortOrder(0).build());
		}
		return interiorPostRepository.save(post).getId();
	}

	@Transactional
	public void update(Long postId, Long memberId, InteriorPostFormDto dto, MultipartFile image) throws IOException {
		InteriorPost post = getOwnedPost(postId, memberId);
		post.setStyle(dto.getStyle());
		post.setTitle(dto.getTitle());
		post.setContent(dto.getContent());
		post.setRoomSize(dto.getRoomSize());
		post.setBudget(dto.getBudget());
		post.setHasFurnitureTag(dto.isHasFurnitureTag());
		post.setUpdatedAt(LocalDateTime.now());
		if (image != null && !image.isEmpty()) {
			String path = fileStorageService.storeInteriorImage(image);
			post.getImages().clear();
			post.addImage(PostImage.builder().filePath(path).thumbnail(true).build());
		}
	}

	@Transactional
	public void delete(Long postId, Long memberId) {
		InteriorPost post = getOwnedPost(postId, memberId);
		post.setStatus(PostStatus.DELETED);
	}

	@Transactional
	public boolean toggleLike(Long postId, Long memberId) {
		InteriorPost post = interiorPostRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
		var existing = postLikeRepository.findByMemberIdAndPostId(memberId, postId);
		if (existing.isPresent()) {
			postLikeRepository.delete(existing.get());
			post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
			return false;
		}
		Member member = memberService.findById(memberId);
		postLikeRepository.save(PostLike.builder().member(member).post(post).build());
		post.setLikeCount(post.getLikeCount() + 1);
		return true;
	}

	public boolean isLiked(Long postId, Long memberId) {
		return memberId != null && postLikeRepository.existsByMemberIdAndPostId(memberId, postId);
	}

	@Transactional
	public void addComment(Long postId, Long memberId, String content, Long parentId) {
		InteriorPost post = interiorPostRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
		Member author = memberService.findById(memberId);
		var comment = com.example.roomfit.domain.Comment.builder()
				.postType(PostType.INTERIOR)
				.postId(postId)
				.author(author)
				.content(content)
				.build();
		if (parentId != null) {
			comment.setParent(commentRepository.findById(parentId).orElse(null));
		}
		commentRepository.save(comment);
		post.setCommentCount((int) commentRepository.countByPostTypeAndPostIdAndStatus(
				PostType.INTERIOR, postId, PostStatus.VISIBLE));
	}

	public List<com.example.roomfit.domain.Comment> getComments(Long postId) {
		return commentRepository.findByPostTypeAndPostIdAndStatusOrderByCreatedAtAsc(
				PostType.INTERIOR, postId, PostStatus.VISIBLE);
	}

	private InteriorPost getOwnedPost(Long postId, Long memberId) {
		InteriorPost post = interiorPostRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
		if (!post.getAuthor().getId().equals(memberId)) {
			throw new BusinessException("수정 권한이 없습니다.");
		}
		return post;
	}
}

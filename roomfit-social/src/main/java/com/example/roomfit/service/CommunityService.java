package com.example.roomfit.service;

import com.example.roomfit.domain.CommunityBoardType;
import com.example.roomfit.domain.CommunityPost;
import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.PostStatus;
import com.example.roomfit.exception.ResourceNotFoundException;
import com.example.roomfit.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {

	private final CommunityPostRepository communityPostRepository;
	private final MemberService memberService;

	public Page<CommunityPost> list(CommunityBoardType boardType, Pageable pageable) {
		return communityPostRepository.findByBoardTypeAndStatus(boardType, PostStatus.VISIBLE, pageable);
	}

	@Transactional
	public CommunityPost getDetail(Long id) {
		CommunityPost post = communityPostRepository.findByIdAndStatus(id, PostStatus.VISIBLE)
				.orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
		post.setViewCount(post.getViewCount() + 1);
		return post;
	}

	@Transactional
	public Long create(Long memberId, CommunityBoardType boardType, String title, String content) {
		Member author = memberService.findById(memberId);
		return communityPostRepository.save(CommunityPost.builder()
				.boardType(boardType)
				.author(author)
				.title(title)
				.content(content)
				.build()).getId();
	}
}

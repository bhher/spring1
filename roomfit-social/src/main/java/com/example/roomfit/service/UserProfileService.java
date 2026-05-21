package com.example.roomfit.service;

import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.UserProfile;
import com.example.roomfit.dto.ProfileFormDto;
import com.example.roomfit.repository.UserProfileRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

	private final UserProfileRepository userProfileRepository;
	private final MemberService memberService;

	@Transactional
	public void saveOrUpdate(Long memberId, ProfileFormDto dto) {
		Member member = memberService.findById(memberId);
		UserProfile profile = userProfileRepository.findByMemberId(memberId)
				.orElse(UserProfile.builder().member(member).build());
		profile.setRoomSize(dto.getRoomSize());
		profile.setBudget(dto.getBudget());
		profile.setPreferredStyle(dto.getPreferredStyle());
		profile.setLifestyle(dto.getLifestyle());
		profile.setHasFurniture(dto.isHasFurniture());
		profile.setSleepPattern(dto.getSleepPattern());
		profile.setUpdatedAt(LocalDateTime.now());
		userProfileRepository.save(profile);
	}

	public UserProfile findByMemberId(Long memberId) {
		return userProfileRepository.findByMemberId(memberId).orElse(null);
	}
}

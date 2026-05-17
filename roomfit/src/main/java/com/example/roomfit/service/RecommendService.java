package com.example.roomfit.service;

import com.example.roomfit.dto.RecommendResultDto;
import com.example.roomfit.recommend.RecommendEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendService {

	private final RecommendEngine recommendEngine;

	public RecommendResultDto getRecommendations(Long memberId) {
		return recommendEngine.recommend(memberId);
	}
}

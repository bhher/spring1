package com.example.roomfit.dto;

import com.example.roomfit.domain.InteriorStyle;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendResultDto {

	private final List<ScoredPostDto> posts;
	private final List<String> colorPalette;
	private final String layoutAdvice;
	private final InteriorStyle preferredStyle;
}

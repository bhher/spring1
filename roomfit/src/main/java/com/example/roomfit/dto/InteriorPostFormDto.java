package com.example.roomfit.dto;

import com.example.roomfit.domain.InteriorStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InteriorPostFormDto {

	@NotNull
	private InteriorStyle style;

	@NotBlank
	@Size(max = 200)
	private String title;

	@NotBlank
	private String content;

	private BigDecimal roomSize;
	private Integer budget;
	private boolean hasFurnitureTag;
}

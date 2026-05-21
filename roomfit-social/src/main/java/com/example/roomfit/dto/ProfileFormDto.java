package com.example.roomfit.dto;

import com.example.roomfit.domain.InteriorStyle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileFormDto {

	@NotNull
	@DecimalMin("1.0")
	private BigDecimal roomSize;

	@NotNull
	@Min(10)
	private Integer budget;

	@NotNull
	private InteriorStyle preferredStyle;

	private String lifestyle;
	private boolean hasFurniture;
	private String sleepPattern;
}

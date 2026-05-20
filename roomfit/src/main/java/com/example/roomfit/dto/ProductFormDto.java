package com.example.roomfit.dto;

import com.example.roomfit.domain.InteriorStyle;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFormDto {

	private Long id;

	@NotBlank
	@Size(max = 200)
	private String name;

	@Min(0)
	private int price;

	@Min(0)
	private int stock = 100;

	@NotNull
	private InteriorStyle styleTag;

	private boolean onSale = true;
}

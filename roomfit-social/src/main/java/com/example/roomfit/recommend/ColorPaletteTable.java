package com.example.roomfit.recommend;

import com.example.roomfit.domain.InteriorStyle;
import java.util.List;
import java.util.Map;

public final class ColorPaletteTable {

	private static final Map<InteriorStyle, List<String>> PALETTES = Map.of(
			InteriorStyle.MINIMAL, List.of("#F5F5F5", "#E0E0E0", "#333333", "#FFFFFF"),
			InteriorStyle.SCANDINAVIAN, List.of("#FFFFFF", "#D4C4B0", "#5C7A6B", "#F0EBE3"),
			InteriorStyle.MODERN, List.of("#1A1A1A", "#C0C0C0", "#FFFFFF", "#4A4A4A"),
			InteriorStyle.EMOTIONAL, List.of("#F8E8EE", "#C9B8D9", "#E8D5B7", "#FFFFFF"),
			InteriorStyle.BUDGET, List.of("#FAFAFA", "#B0BEC5", "#FF7043", "#EEEEEE"),
			InteriorStyle.BEGINNER, List.of("#FFF8E1", "#90CAF9", "#FFFFFF", "#8D6E63"));

	private ColorPaletteTable() {
	}

	public static List<String> get(InteriorStyle style) {
		return PALETTES.getOrDefault(style, PALETTES.get(InteriorStyle.MINIMAL));
	}
}

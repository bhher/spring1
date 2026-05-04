package com.example.crud2.mapper.support;

import com.example.crud2.mapper.dto.DoDto;
import com.example.crud2.mapper.entity.DoIt;

/** DTO ↔ 엔티티 변환 한곳 모음 */
public final class DoMapper {

	private DoMapper() {
	}

	public static DoIt toNewEntity(DoDto dto) {
		return new DoIt(null, dto.getTitle(), dto.getContent());
	}

	public static DoIt toEntityWithId(DoDto dto) {
		return new DoIt(dto.getNum(), dto.getTitle(), dto.getContent());
	}
}

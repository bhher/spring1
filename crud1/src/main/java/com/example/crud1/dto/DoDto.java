package com.example.crud1.dto;

import com.example.crud1.entity.DoIt;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class DoDto {
    private Long num;
    private String title;
    private String content;

    public DoIt toEntity() {
        return new DoIt(num,title,content);
    }
}

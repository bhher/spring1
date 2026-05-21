package com.example.roomfit.dto;

import com.example.roomfit.domain.InteriorPost;

public record ScoredPostDto(InteriorPost post, int score) {}

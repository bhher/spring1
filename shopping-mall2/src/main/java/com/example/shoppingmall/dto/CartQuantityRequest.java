package com.example.shoppingmall.dto;

import jakarta.validation.constraints.Min;

public record CartQuantityRequest(@Min(1) int quantity) {
}

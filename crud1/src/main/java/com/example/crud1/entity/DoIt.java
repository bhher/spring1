package com.example.crud1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DoIt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;
    @Column
    private String title;
    @Column
    private String content;
}

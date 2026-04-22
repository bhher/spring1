package com.example.join1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.join1.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

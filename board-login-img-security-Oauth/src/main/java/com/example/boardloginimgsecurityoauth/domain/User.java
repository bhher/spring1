package com.example.boardloginimgsecurityoauth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    /** 소셜 연동·이메일 매칭용 (일반 가입은 null 가능) */
    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String oauthProvider;

    @Column(length = 255)
    private String oauthProviderSubject;

    protected User() {
    }

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public String getOauthProviderSubject() {
        return oauthProviderSubject;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public void setOauthProviderSubject(String oauthProviderSubject) {
        this.oauthProviderSubject = oauthProviderSubject;
    }
}

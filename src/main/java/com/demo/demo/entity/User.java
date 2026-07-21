package com.demo.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Data  //@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Matches your database column
    @Column(name = "mobile_no", nullable = false, unique = true)
    private String mobile;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "role", nullable = false)
    private String role;
}
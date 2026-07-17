package com.demo.demo.entity;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long userId;

    @Column(name = "Name", nullable = false)
    private String name;


    @Column(name = "Email", nullable = false, unique = true)
    private String email;


    @Column(name= "Mobile_No", unique = true,nullable = false)
    private String Mobile;


    @Column(name = "Password_Hash", nullable = false)
    private String password_hash;

    @Column(name= " Role",nullable = false)
    private String role;


}

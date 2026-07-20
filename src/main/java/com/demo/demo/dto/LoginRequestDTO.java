package com.demo.demo.dto;

public class LoginRequestDTO {
    private String email;
    private String password;

    public String getEmail(){return email;}
    public void setEmail(String e){this.email=e;}
    public String getPassword(){return password;}
    public void setPassword(String p){this.password=p;}
}

package com.ecommerce.project.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Setter;

public class LoginRequest {
    @NotBlank
    private String username;

    @Setter
    @NotBlank
    private String password;

    public void setUsername(){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}

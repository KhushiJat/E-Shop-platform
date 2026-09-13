package com.ecommerce.project.security.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserInfoResponse {
    @Setter
    @Getter
    private Long id;
    @Setter
    @Getter
    private String jwtToken;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private List<String> roles;

    public UserInfoResponse(Long id, String username, String jwtToken, String email, List<String> roles){
        this.id = id;
        this.username = username;
        this.jwtToken = jwtToken;
        this.email = email;
        this.roles = roles;
    }

    public UserInfoResponse(Long id, String username, List<String> roles){
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}

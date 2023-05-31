package com.cg.domain.esport.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class JwtResponse {
    private Long userId;
    private Long id;
    private String token;
    private String type = "Bearer";
    private String username;
    private String name;
    private String code;
    private Collection<? extends GrantedAuthority> roles;

    public JwtResponse(String accessToken, Long userId, String username, String name, Collection<? extends GrantedAuthority> roles, String code) {
        this.token = accessToken;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.roles = roles;
        this.code = code;
    }
    public JwtResponse(String accessToken, Long userId, String username, String name, Collection<? extends GrantedAuthority> roles, String code, Long id) {
        this.token = accessToken;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.roles = roles;
        this.code = code;
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" + "'" +"userId" + "'" + ":" + userId +
                "/"+ "'" + "username" +"'" +":" + "'"+ username + "'" +
                "/" + "'" + "name" + "'" + ":"+ "'" + name + "'" +
                "/" + "'" + "code" + "'" + ":"+ "'" + code + "'" +
                "/" + "'" + "id" + "'" + ":"+ id +
                "}" ;
    }
}


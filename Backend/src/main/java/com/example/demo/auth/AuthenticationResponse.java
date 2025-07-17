package com.example.demo.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
public class AuthenticationResponse {

    private String token;
}

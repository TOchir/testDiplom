package ru.netology.diplomproject.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class AuthResponse {
    @JsonProperty("auth-token")
    private String jwtToken;
}

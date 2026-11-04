package com.devluiz.userservice.dto;

/* Resposta do login: o token e em quantos segundos ele expira. */
public record TokenResponse(String accessToken, long expiresIn) {
}

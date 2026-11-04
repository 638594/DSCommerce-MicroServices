package com.devluiz.userservice.controllers;

import com.devluiz.userservice.dto.UserDTO;
import com.devluiz.userservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*
     * @AuthenticationPrincipal Jwt: o Spring já conferiu o token e entrega o conteúdo dele.
     * jwt.getSubject() é o email que o TokenService colocou no campo "sub".
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.findMe(jwt.getSubject()));
    }
}

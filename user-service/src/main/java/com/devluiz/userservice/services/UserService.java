package com.devluiz.userservice.services;

import com.devluiz.userservice.dto.LoginRequest;
import com.devluiz.userservice.dto.TokenResponse;
import com.devluiz.userservice.dto.UserDTO;
import com.devluiz.userservice.entities.User;
import com.devluiz.userservice.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public UserService(AuthenticationManager authenticationManager,
                       TokenService tokenService,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // 1. Confere email + senha. Se estiver errado, lança BadCredentialsException (vira 401)
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));

        // 2. Deu certo: busca o usuário para colocar id e papéis no token
        User user = findByEmail(request.email());

        // 3. Gera o token assinado
        String token = tokenService.generateToken(user);
        return new TokenResponse(token, tokenService.getExpirationSeconds());
    }

    @Transactional(readOnly = true)
    public UserDTO findMe(String email) {
        return new UserDTO(findByEmail(email));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
    }
}

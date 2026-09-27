package com.DEVSenior.vetturno.service;

import com.DEVSenior.vetturno.dto.AuthResponse;
import com.DEVSenior.vetturno.dto.LoginRequest;
import com.DEVSenior.vetturno.dto.RegistroRequest;
import com.DEVSenior.vetturno.model.Usuario;
import com.DEVSenior.vetturno.repository.UsuarioRepository;
import com.DEVSenior.vetturno.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse registro(RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        String passwordEncoded = passwordEncoder.encode(request.password());
        Usuario usuario = new Usuario(null, request.email(), passwordEncoded, "USER");
        usuarioRepository.save(usuario);

        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getRol());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String token = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getRol());
        return new AuthResponse(token);
    }
}

package com.dwes.restauranteapi.controllers;


import com.dwes.restauranteapi.DTO.LoginRequestDTO;
import com.dwes.restauranteapi.DTO.LoginResponseDTO;
import com.dwes.restauranteapi.DTO.UserRegisterDTO;
import com.dwes.restauranteapi.config.JwtTokenProvider;
import com.dwes.restauranteapi.entities.Cliente;
import com.dwes.restauranteapi.entities.UserEntity;
import com.dwes.restauranteapi.repositories.ClienteRepository;
import com.dwes.restauranteapi.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private UserEntityRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, String>> save(@RequestBody UserRegisterDTO userDTO) {
        try {
            UserEntity userEntity = userRepository.save(
                    UserEntity.builder()
                            .username(userDTO.getUsername())
                            .password(passwordEncoder.encode(userDTO.getPassword()))
                            .email(userDTO.getEmail())
                            .authorities(List.of("ROLE_USER"))
                            .foto(userDTO.getFoto())
                            .build());

            Cliente cliente = new Cliente();
            cliente.setNombre(userDTO.getUsername());
            cliente.setEmail(userDTO.getEmail());
            cliente.setTelefono(userDTO.getTelefono());
            cliente.setUsuario(userEntity);

            clienteRepository.save(cliente);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("email", userEntity.getEmail(), "username", userEntity.getUsername())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email o username ya utilizado"));
        }
    }

    //
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        try {
            UsernamePasswordAuthenticationToken userPassAuthToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            Authentication auth = authenticationManager.authenticate(userPassAuthToken);

            UserEntity user = (UserEntity) auth.getPrincipal();

            String token = this.tokenProvider.generateToken(auth);

            return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), user.getEmail(), token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of(
                            "path", "/auth/login",
                            "message", "Credenciales erróneas",
                            "timestamp", new Date()
                    )
            );
        }
    }

}

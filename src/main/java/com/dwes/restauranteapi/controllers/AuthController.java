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
            // 1️⃣ Crear el usuario en la tabla user_entity
            UserEntity userEntity = userRepository.save(
                    UserEntity.builder()
                            .username(userDTO.getUsername())
                            .password(passwordEncoder.encode(userDTO.getPassword()))
                            .email(userDTO.getEmail())
                            .authorities(List.of("ROLE_USER")) // Ajusta los roles según tu necesidad
                            .foto(userDTO.getFoto())
                            .build());

            // 2️⃣ Crear automáticamente un cliente en la tabla cliente
            Cliente cliente = new Cliente();
            cliente.setNombre(userDTO.getUsername());
            cliente.setEmail(userDTO.getEmail());
            cliente.setTelefono("123456789"); // Puedes cambiarlo por un campo opcional en el registro
            cliente.setUsuario(userEntity); // Relacionar el cliente con el usuario

            clienteRepository.save(cliente); // Guardar cliente en la BD

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("email", userEntity.getEmail(), "username", userEntity.getUsername())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email o username ya utilizado"));
        }
    }





    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        try {
            // Validamos al usuario en Spring Security
            UsernamePasswordAuthenticationToken userPassAuthToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
            Authentication auth = authenticationManager.authenticate(userPassAuthToken);

            // Obtenemos el usuario autenticado
            UserEntity user = (UserEntity) auth.getPrincipal();

            // Generamos el token con la información del usuario
            String token = this.tokenProvider.generateToken(auth);

            // Devolvemos la respuesta incluyendo el email
            return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), user.getEmail(), token)); // <-- AÑADE EL EMAIL AQUÍ

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

package com.dwes.restauranteapi.services;

import com.dwes.restauranteapi.entities.UserEntity;
import com.dwes.restauranteapi.repositories.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserEntityRepository userRepository;

    UserDetailsServiceImpl(UserEntityRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //Extrae el usuario de la BD
        return this.userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException(email+" no encontrado")
        );

    }
}

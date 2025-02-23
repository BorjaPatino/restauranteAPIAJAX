package com.dwes.restauranteapi.repositories;

import com.dwes.restauranteapi.entities.Cliente;
import com.dwes.restauranteapi.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuario(UserEntity usuario);
    Optional<Cliente> findByEmail(String email);

}

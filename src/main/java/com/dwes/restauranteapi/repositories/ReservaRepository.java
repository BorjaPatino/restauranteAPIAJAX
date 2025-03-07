package com.dwes.restauranteapi.repositories;

import com.dwes.restauranteapi.entities.Mesa;
import com.dwes.restauranteapi.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByFecha(LocalDate fecha);
    List<Reserva> findByFechaAndHora(LocalDate fecha, LocalTime hora);
    boolean existsByMesaAndFechaAndHora(Mesa mesa, LocalDate fecha, LocalTime hora);
    List<Reserva> findByClienteEmail(String email);

}

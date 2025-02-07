package com.dwes.restauranteapi.controllers;

import com.dwes.restauranteapi.DTO.ReservaConClientesMesa;
import com.dwes.restauranteapi.entities.Reserva;
import com.dwes.restauranteapi.repositories.ReservaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ReservaController {

    @Autowired
    ReservaRepository reservaRepository;

    // Metodo existente para listar todas las reservas con clientes y mesas
    @GetMapping("/reservas_con_clientes_mesas")
    public ResponseEntity<List<ReservaConClientesMesa>> getReservasConClientesYMesas() {
        List<ReservaConClientesMesa> reservasDTO = new ArrayList<>();

        reservaRepository.findAll().forEach(reserva -> {
            reservasDTO.add(
                    ReservaConClientesMesa.builder()
                            .id(reserva.getId())
                            .nombreCliente(reserva.getCliente().getNombre())
                            .emailCliente(reserva.getCliente().getEmail())
                            .telefonoCliente(reserva.getCliente().getTelefono())
                            .numeroMesa(reserva.getMesa().getNumero())
                            .descripcionMesa(reserva.getMesa().getDescripcion())
                            .numeroPersonas(reserva.getNumeroPersonas())
                            .fechaReserva(reserva.getFecha())
                            .horaReserva(reserva.getHora())
                            .build()
            );
        });

        return ResponseEntity.ok(reservasDTO);
    }

    // Nuevo metodo para listar reservas por fecha específicaa
    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaConClientesMesa>> getReservasPorFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<ReservaConClientesMesa> reservasDTO = new ArrayList<>();

        // Filtrar reservas por la fecha proporcionada
        reservaRepository.findByFecha(fecha).forEach(reserva -> {
            reservasDTO.add(
                    ReservaConClientesMesa.builder()
                            .id(reserva.getId())
                            .nombreCliente(reserva.getCliente().getNombre())
                            .emailCliente(reserva.getCliente().getEmail())
                            .telefonoCliente(reserva.getCliente().getTelefono())
                            .numeroMesa(reserva.getMesa().getNumero())
                            .descripcionMesa(reserva.getMesa().getDescripcion())
                            .numeroPersonas(reserva.getNumeroPersonas())
                            .fechaReserva(reserva.getFecha())
                            .horaReserva(reserva.getHora())
                            .build()
            );
        });

        return ResponseEntity.ok(reservasDTO);
    }

    @PostMapping("/reservas/add")
    public ResponseEntity<String> createReserva(@RequestBody @Valid Reserva nuevaReserva) {
        // Verificar si la mesa está disponible para la fecha y hora
        boolean mesaDisponible = reservaRepository.existsByMesaAndFechaAndHora(
                nuevaReserva.getMesa(),
                nuevaReserva.getFecha(),
                nuevaReserva.getHora()
        );

        if (mesaDisponible) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("La mesa ya está reservada para la fecha y hora seleccionadas.");
        }

        // Guardar la nueva reserva si la mesa está disponible
        reservaRepository.save(nuevaReserva);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Reserva creada exitosamente.");
    }


    @DeleteMapping("/reservas/delete/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reservaRepository.delete(reserva);
                    return ResponseEntity.noContent().build(); // 204 No Content
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 Not Found
    }

}

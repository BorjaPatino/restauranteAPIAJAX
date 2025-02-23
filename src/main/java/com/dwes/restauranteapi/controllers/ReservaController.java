package com.dwes.restauranteapi.controllers;

import com.dwes.restauranteapi.DTO.ReservaConClientesMesa;
import com.dwes.restauranteapi.entities.Cliente;
import com.dwes.restauranteapi.entities.Reserva;
import com.dwes.restauranteapi.entities.UserEntity;
import com.dwes.restauranteapi.repositories.ClienteRepository;
import com.dwes.restauranteapi.repositories.ReservaRepository;
import com.dwes.restauranteapi.repositories.UserEntityRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ReservaController {

    @Autowired
    ReservaRepository reservaRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    UserEntityRepository userEntityRepository;

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

    @GetMapping("/reservas_todas")
    public ResponseEntity<List<ReservaConClientesMesa>> getAllReservas() {
        List<ReservaConClientesMesa> reservasDTO = new ArrayList<>();

        // Obtener todas las reservas desde la base de datos
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


    @PostMapping("/reservas/add")
    public ResponseEntity<String> createReserva(@RequestBody @Valid Reserva nuevaReserva, Authentication authentication) {
        System.out.println(" Recibiendo solicitud para crear reserva...");

        // Obtener el usuario autenticado
        String username = authentication.getName();
        System.out.println(" Buscando usuario con username: " + username);

        // Buscar el usuario en la base de datos
        UserEntity usuario = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el cliente relacionado con el usuario
        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        System.out.println("✅ Cliente encontrado: " + cliente.getNombre());

        // Asignar cliente a la reserva
        nuevaReserva.setCliente(cliente);

        // Verificar si la mesa está disponible
        boolean mesaDisponible = reservaRepository.existsByMesaAndFechaAndHora(
                nuevaReserva.getMesa(),
                nuevaReserva.getFecha(),
                nuevaReserva.getHora()
        );

        if (mesaDisponible) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("La mesa ya está reservada para la fecha y hora seleccionadas.");
        }

        // Guardar la reserva
        reservaRepository.save(nuevaReserva);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Reserva creada exitosamente.");
    }




    @DeleteMapping("/reservas/delete/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable Long id, Authentication authentication) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    String emailAutenticado = authentication.getName(); // Esto ahora devuelve el email
                    String emailCliente = reserva.getCliente().getEmail();

                    System.out.println(" Usuario autenticado: " + emailAutenticado);
                    System.out.println(" Cliente de la reserva: " + emailCliente);

                    if (!emailCliente.equals(emailAutenticado)) {
                        System.out.println(" No autorizado para eliminar esta reserva");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes borrar reservas de otros usuarios.");
                    }

                    reservaRepository.delete(reserva);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }






}

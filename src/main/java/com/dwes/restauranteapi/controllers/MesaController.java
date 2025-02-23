package com.dwes.restauranteapi.controllers;

import com.dwes.restauranteapi.entities.Cliente;
import com.dwes.restauranteapi.entities.Mesa;
import com.dwes.restauranteapi.entities.Reserva;
import com.dwes.restauranteapi.repositories.ClienteRepository;
import com.dwes.restauranteapi.repositories.MesaRepository;
import com.dwes.restauranteapi.repositories.ReservaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MesaController {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    /**
     * Obtener todos los empleados en un JSON
     */
    @GetMapping("/mesas")
    public ResponseEntity<List<Mesa>> getListMesas(){
        var mesas = mesaRepository.findAll();
        return ResponseEntity.ok(mesas);    //Devuelve el código status 200 OK
    }

    /**
     * Insertar un empleado (recibe los datos en el cuerpo (body) en formato JSON)
     */
    @PostMapping("/mesas")
    public ResponseEntity<Mesa> insertMesa(@RequestBody Mesa mesa){
        var mesaGuardado = mesaRepository.save(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaGuardado);    //Devuelve el código status 201 Createdd
    }

    @DeleteMapping("/mesas/{id}")
    public ResponseEntity<?> deleteMesa(@PathVariable Long id){

        return mesaRepository.findById(id)
                .map(mesa -> {
                    mesaRepository.delete(mesa);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/mesas/{id}")
    public ResponseEntity<Mesa> updateMesa(@PathVariable Long id, @RequestBody @Valid Mesa mesaDetalles) {
        return mesaRepository.findById(id)
                .map(mesa -> {
                    if (mesaDetalles.getNumero() != null) {
                        mesa.setNumero(mesaDetalles.getNumero());
                    }
                    if (mesaDetalles.getDescripcion() != null) {
                        mesa.setDescripcion(mesaDetalles.getDescripcion());
                    }
                    Mesa mesaActualizado = mesaRepository.save(mesa);
                    return ResponseEntity.ok(mesaActualizado); // HTTP 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // HTTP 404 Not Found
    }

    @GetMapping("/mesas/disponibles")
    public ResponseEntity<List<Mesa>> getMesasDisponibles(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("hora") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora) {

        // Obtener todas las mesas
        List<Mesa> todasLasMesas = mesaRepository.findAll();

        // Obtener reservas para la fecha y hora seleccionadas
        List<Reserva> reservas = reservaRepository.findByFechaAndHora(fecha, hora);

        // Extraer mesas ocupadas de las reservas
        List<Mesa> mesasOcupadas = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getMesa() != null) {
                mesasOcupadas.add(reserva.getMesa());
            }
        }

        // Filtrar mesas disponibles
        List<Mesa> mesasDisponibles = todasLasMesas.stream()
                .filter(mesa -> !mesasOcupadas.contains(mesa))
                .toList();

        return ResponseEntity.ok(mesasDisponibles);
    }





}

package com.dwes.restauranteapi.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class ReservaConClientesMesa {
    private Long id;
    private String nombreCliente;
    private String emailCliente;
    private String telefonoCliente;

    private Integer numeroMesa;
    private String descripcionMesa;

    private Integer numeroPersonas;

    private LocalDate fechaReserva;
    private LocalTime horaReserva;
}

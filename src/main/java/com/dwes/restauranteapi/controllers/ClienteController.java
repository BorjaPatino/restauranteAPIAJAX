package com.dwes.restauranteapi.controllers;

import com.dwes.restauranteapi.entities.Cliente;
import com.dwes.restauranteapi.repositories.ClienteRepository;
import com.dwes.restauranteapi.repositories.MesaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MesaRepository mesaRepository;
    /**
     * Obtener todos los empleados en un JSON
     */
    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> getListClientes(){
        var clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);    //Devuelve el código status 200 OK
    }

    /**
     * Insertar un empleado (recibe los datos en el cuerpo (body) en formato JSON)
     */
    @PostMapping("/clientes")
    public ResponseEntity<Cliente> insertEmpleado(@RequestBody @Valid Cliente cliente){
        var clienteGuardado = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);    //Devuelve el código status 201 Created
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id){

        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteRepository.delete(cliente);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody @Valid Cliente clienteDetalles) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    if (clienteDetalles.getNombre() != null) {
                        cliente.setNombre(clienteDetalles.getNombre());
                    }
                    if (clienteDetalles.getEmail() != null) {
                        cliente.setEmail(clienteDetalles.getEmail());
                    }
                    if (clienteDetalles.getTelefono() != null) {
                        cliente.setTelefono(clienteDetalles.getTelefono());
                    }
                    Cliente clienteActualizado = clienteRepository.save(cliente);
                    return ResponseEntity.ok(clienteActualizado); // HTTP 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // HTTP 404 Not Found
    }

}

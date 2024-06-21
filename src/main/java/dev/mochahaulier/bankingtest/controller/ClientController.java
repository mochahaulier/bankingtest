package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ClientRequest;
import dev.mochahaulier.bankingtest.model.Client;
import dev.mochahaulier.bankingtest.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Client createClient(@RequestBody @Valid ClientRequest clientRequest) {
        return clientService.saveClient(clientRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody ClientRequest clientRequest) {
        return clientService.updateClient(id, clientRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
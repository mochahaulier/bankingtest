package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.dto.ClientRequest;
import dev.mochahaulier.bankingtest.model.Client;
import dev.mochahaulier.bankingtest.repository.ClientRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client saveClient(ClientRequest clientRequest) {
        Client client = new Client();

        client.setFirstName(clientRequest.getFirstName());
        client.setLastName(clientRequest.getLastName());
        client.setEmail(clientRequest.getEmail());
        client.setPhone(clientRequest.getPhone());

        return clientRepository.save(client);
    }

    @Transactional
    public ResponseEntity<Client> updateClient(Long id, ClientRequest clientRequest) {
        Optional<Client> clientOptional = getClientById(id);
        boolean updated = false;
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            if (clientRequest.getFirstName() != null) {
                client.setFirstName(clientRequest.getFirstName());
                updated = true;
            }
            if (clientRequest.getLastName() != null) {
                client.setLastName(clientRequest.getLastName());
                updated = true;
            }
            if (clientRequest.getEmail() != null) {
                client.setEmail(clientRequest.getEmail());
                updated = true;
            }
            if (clientRequest.getPhone() != null) {
                client.setPhone(clientRequest.getPhone());
                updated = true;
            }
            if (updated) {
                clientRepository.save(client);
            }
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
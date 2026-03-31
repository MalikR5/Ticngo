package fr.ticngo.service;

import fr.ticngo.model.Client;
import fr.ticngo.repository.ClientRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepo) {
        this.clientRepo = clientRepo;
    }

    public List<Client> findAll() { return clientRepo.findAll(); }

    public Optional<Client> findById(int id) { return clientRepo.findById(id); }

    public List<Client> search(String query) {
        if (query == null || query.isBlank()) return findAll();
        return clientRepo.search(query);
    }

    public Client save(Client client) {
        if (client.getId() == null) {
            client.setDateInscription(LocalDateTime.now());
        }
        return clientRepo.save(client);
    }

    public void delete(int id) { clientRepo.deleteById(id); }
    public long count() { return clientRepo.count(); }
}

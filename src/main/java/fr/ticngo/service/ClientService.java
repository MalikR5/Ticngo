package fr.ticngo.service;

import fr.ticngo.model.Client;
import fr.ticngo.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {
    private final ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepo) {
        this.clientRepo = clientRepo;
    }

    public List<Client> findAll() { return clientRepo.findAll(); }
    public Optional<Client> findById(Long id) { return clientRepo.findById(id); }

    public List<Client> search(String query) {
        if (query == null || query.isBlank()) return findAll();
        return clientRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
            query, query, query);
    }

    public Client save(Client client) {
        if (client.getId() == null) {
            client.setDateInscription(LocalDateTime.now());
        }
        return clientRepo.save(client);
    }

    public void delete(Long id) { clientRepo.deleteById(id); }
    public long count() { return clientRepo.count(); }
}

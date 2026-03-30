package fr.ticngo.service;

import fr.ticngo.model.Spectacle;
import fr.ticngo.repository.SpectacleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SpectacleService {
    private final SpectacleRepository spectacleRepo;

    public SpectacleService(SpectacleRepository spectacleRepo) {
        this.spectacleRepo = spectacleRepo;
    }

    public List<Spectacle> findAll() { return spectacleRepo.findAll(); }
    public Optional<Spectacle> findById(Long id) { return spectacleRepo.findById(id); }

    public List<Spectacle> search(String titre) {
        if (titre == null || titre.isBlank()) return findAll();
        return spectacleRepo.findByTitreContainingIgnoreCase(titre);
    }

    public Spectacle save(Spectacle spectacle) {
        if (spectacle.getId() == null) {
            spectacle.setDateCreation(LocalDateTime.now());
        }
        return spectacleRepo.save(spectacle);
    }

    public void delete(Long id) { spectacleRepo.deleteById(id); }
    public long count() { return spectacleRepo.count(); }
}

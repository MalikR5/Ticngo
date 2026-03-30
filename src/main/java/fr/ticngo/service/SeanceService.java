package fr.ticngo.service;

import fr.ticngo.model.Seance;
import fr.ticngo.repository.SeanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeanceService {
    private final SeanceRepository seanceRepo;

    public SeanceService(SeanceRepository seanceRepo) {
        this.seanceRepo = seanceRepo;
    }

    public List<Seance> findAll() { return seanceRepo.findAll(); }
    public List<Seance> findBySpectacleId(Long spectacleId) { return seanceRepo.findBySpectacleId(spectacleId); }
    public Optional<Seance> findById(Long id) { return seanceRepo.findById(id); }
    public Seance save(Seance seance) { return seanceRepo.save(seance); }
    public void delete(Long id) { seanceRepo.deleteById(id); }
}

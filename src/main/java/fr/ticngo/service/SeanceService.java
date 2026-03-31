package fr.ticngo.service;

import fr.ticngo.model.Seance;
import fr.ticngo.repository.SeanceRepository;

import java.util.List;
import java.util.Optional;

public class SeanceService {
    private final SeanceRepository seanceRepo;

    public SeanceService(SeanceRepository seanceRepo) {
        this.seanceRepo = seanceRepo;
    }

    public List<Seance> findAll() { return seanceRepo.findAll(); }
    public List<Seance> findBySpectacleId(int spectacleId) { return seanceRepo.findBySpectacleId(spectacleId); }
    public Optional<Seance> findById(int id) { return seanceRepo.findById(id); }
    public Seance save(Seance seance) { return seanceRepo.save(seance); }
    public void delete(int id) { seanceRepo.deleteById(id); }
}

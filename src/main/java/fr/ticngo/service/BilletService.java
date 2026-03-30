package fr.ticngo.service;

import fr.ticngo.model.Billet;
import fr.ticngo.model.Billet.StatutBillet;
import fr.ticngo.model.Client;
import fr.ticngo.model.Seance;
import fr.ticngo.repository.BilletRepository;
import fr.ticngo.repository.SeanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BilletService {

    private final BilletRepository billetRepo;
    private final SeanceRepository seanceRepo;

    public BilletService(BilletRepository billetRepo, SeanceRepository seanceRepo) {
        this.billetRepo = billetRepo;
        this.seanceRepo = seanceRepo;
    }

    public List<Billet> findAll() {
        return billetRepo.findAll();
    }

    public Optional<Billet> findById(Long id) {
        return billetRepo.findById(id);
    }

    public List<Billet> search(String search, StatutBillet statut) {
        return billetRepo.searchBillets(
            (search == null || search.isBlank()) ? null : search.trim(),
            statut
        );
    }

    public Billet creer(Client client, Seance seance, BigDecimal prix) {
        if (seance.getPlacesDisponibles() <= 0) {
            throw new IllegalStateException("Plus de places disponibles pour cette séance");
        }
        String numero = "TCK-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Billet billet = Billet.builder()
                .numeroBillet(numero)
                .client(client)
                .seance(seance)
                .prix(prix)
                .statut(StatutBillet.VALIDE)
                .dateAchat(LocalDateTime.now())
                .build();

        seance.setPlacesDisponibles(seance.getPlacesDisponibles() - 1);
        seanceRepo.save(seance);

        return billetRepo.save(billet);
    }

    public Billet save(Billet billet) {
        return billetRepo.save(billet);
    }

    public void annuler(Long id) {
        Billet b = billetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Billet introuvable"));
        if (b.getStatut() != StatutBillet.VALIDE) {
            throw new IllegalStateException("Seul un billet VALIDE peut être annulé");
        }
        b.setStatut(StatutBillet.ANNULE);
        Seance s = b.getSeance();
        s.setPlacesDisponibles(s.getPlacesDisponibles() + 1);
        seanceRepo.save(s);
        billetRepo.save(b);
    }

    public void valider(Long id) {
        Billet b = billetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Billet introuvable"));
        if (b.getStatut() != StatutBillet.VALIDE) {
            throw new IllegalStateException("Ce billet ne peut pas être validé");
        }
        b.setStatut(StatutBillet.UTILISE);
        billetRepo.save(b);
    }

    public void delete(Long id) {
        billetRepo.deleteById(id);
    }

    public long countByStatut(StatutBillet statut) {
        return billetRepo.countByStatut(statut);
    }

    public long countAll() {
        return billetRepo.count();
    }

    public BigDecimal getTotalRevenu() {
        BigDecimal rev = billetRepo.sumRevenu();
        return rev != null ? rev : BigDecimal.ZERO;
    }
}

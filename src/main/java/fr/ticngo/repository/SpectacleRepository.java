package fr.ticngo.repository;

import fr.ticngo.model.Spectacle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpectacleRepository extends JpaRepository<Spectacle, Long> {
    List<Spectacle> findByTitreContainingIgnoreCase(String titre);
    List<Spectacle> findByCategorie(String categorie);
}

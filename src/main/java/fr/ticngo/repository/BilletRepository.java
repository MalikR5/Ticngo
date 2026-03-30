package fr.ticngo.repository;

import fr.ticngo.model.Billet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    Optional<Billet> findByNumeroBillet(String numeroBillet);
    List<Billet> findByClientId(Long clientId);
    List<Billet> findByStatut(Billet.StatutBillet statut);

    @Query("SELECT b FROM Billet b WHERE " +
           "(:search IS NULL OR b.numeroBillet LIKE %:search% OR " +
           " LOWER(b.client.nom) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
           " LOWER(b.client.prenom) LIKE LOWER(CONCAT('%',:search,'%'))) AND " +
           "(:statut IS NULL OR b.statut = :statut)")
    List<Billet> searchBillets(@Param("search") String search,
                                @Param("statut") Billet.StatutBillet statut);

    @Query("SELECT COALESCE(SUM(b.prix), 0) FROM Billet b WHERE b.statut = 'VALIDE' OR b.statut = 'UTILISE'")
    BigDecimal sumRevenu();

    long countByStatut(Billet.StatutBillet statut);
}

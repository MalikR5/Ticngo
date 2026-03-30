package fr.ticngo.config;

import fr.ticngo.model.*;
import fr.ticngo.repository.*;
import fr.ticngo.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdministrateurRepository adminRepo;
    private final LieuRepository lieuRepo;
    private final SpectacleRepository spectacleRepo;
    private final SeanceRepository seanceRepo;
    private final ClientRepository clientRepo;
    private final BilletRepository billetRepo;
    private final AuthService authService;

    @Value("${app.init-data:true}")
    private boolean initData;

    public DataInitializer(AdministrateurRepository adminRepo, LieuRepository lieuRepo,
                           SpectacleRepository spectacleRepo, SeanceRepository seanceRepo,
                           ClientRepository clientRepo, BilletRepository billetRepo,
                           AuthService authService) {
        this.adminRepo = adminRepo; this.lieuRepo = lieuRepo;
        this.spectacleRepo = spectacleRepo; this.seanceRepo = seanceRepo;
        this.clientRepo = clientRepo; this.billetRepo = billetRepo;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        // Admin par défaut
        if (adminRepo.count() == 0) {
            adminRepo.save(Administrateur.builder()
                    .email("admin@ticngo.fr")
                    .motDePasse(authService.encodePassword("Admin1234!"))
                    .nom("Admin").prenom("Super").build());
        }

        if (!initData || spectacleRepo.count() > 0) return;

        // Lieux
        Lieu olympia = lieuRepo.save(Lieu.builder().nom("L'Olympia").ville("Paris").adresse("28 Bd des Capucines").capacite(2000).build());
        Lieu zenith = lieuRepo.save(Lieu.builder().nom("Zénith de Paris").ville("Paris").adresse("211 Av. Jean Jaurès").capacite(6300).build());

        // Spectacles
        Spectacle hamlet = spectacleRepo.save(Spectacle.builder()
                .titre("Hamlet").categorie("Théâtre").description("La grande tragédie de Shakespeare")
                .prixBase(new BigDecimal("45.00")).lieu(olympia).dateCreation(LocalDateTime.now()).build());
        Spectacle concert = spectacleRepo.save(Spectacle.builder()
                .titre("Concert Jazz Night").categorie("Concert").description("Une nuit de jazz")
                .prixBase(new BigDecimal("35.00")).lieu(zenith).dateCreation(LocalDateTime.now()).build());
        Spectacle comedie = spectacleRepo.save(Spectacle.builder()
                .titre("La Comédie du Siècle").categorie("Comédie").description("Un spectacle hilarant")
                .prixBase(new BigDecimal("29.00")).lieu(olympia).dateCreation(LocalDateTime.now()).build());

        // Séances
        Seance s1 = seanceRepo.save(Seance.builder().spectacle(hamlet)
                .dateHeure(LocalDateTime.now().plusDays(10)).placesTotales(200).placesDisponibles(150).build());
        Seance s2 = seanceRepo.save(Seance.builder().spectacle(concert)
                .dateHeure(LocalDateTime.now().plusDays(15)).placesTotales(500).placesDisponibles(320).build());
        Seance s3 = seanceRepo.save(Seance.builder().spectacle(comedie)
                .dateHeure(LocalDateTime.now().plusDays(5)).placesTotales(300).placesDisponibles(200).build());
        Seance s4 = seanceRepo.save(Seance.builder().spectacle(hamlet)
                .dateHeure(LocalDateTime.now().plusDays(20)).placesTotales(200).placesDisponibles(180).build());

        // Clients
        Client c1 = clientRepo.save(Client.builder().nom("Martin").prenom("Sophie")
                .email("sophie.martin@email.fr").telephone("06 12 34 56 78")
                .dateInscription(LocalDateTime.now().minusMonths(2)).build());
        Client c2 = clientRepo.save(Client.builder().nom("Dupont").prenom("Lucas")
                .email("lucas.dupont@email.fr").telephone("07 98 76 54 32")
                .dateInscription(LocalDateTime.now().minusMonths(1)).build());
        Client c3 = clientRepo.save(Client.builder().nom("Bernard").prenom("Emma")
                .email("emma.bernard@email.fr").telephone("06 55 44 33 22")
                .dateInscription(LocalDateTime.now().minusDays(15)).build());

        // Billets
        billetRepo.save(Billet.builder().numeroBillet("TCK-20240301-A1B2C3").client(c1).seance(s1)
                .prix(new BigDecimal("45.00")).statut(Billet.StatutBillet.VALIDE).dateAchat(LocalDateTime.now().minusDays(5)).build());
        billetRepo.save(Billet.builder().numeroBillet("TCK-20240302-D4E5F6").client(c2).seance(s2)
                .prix(new BigDecimal("35.00")).statut(Billet.StatutBillet.VALIDE).dateAchat(LocalDateTime.now().minusDays(3)).build());
        billetRepo.save(Billet.builder().numeroBillet("TCK-20240303-G7H8I9").client(c3).seance(s3)
                .prix(new BigDecimal("29.00")).statut(Billet.StatutBillet.UTILISE).dateAchat(LocalDateTime.now().minusDays(10)).build());
        billetRepo.save(Billet.builder().numeroBillet("TCK-20240304-J0K1L2").client(c1).seance(s4)
                .prix(new BigDecimal("45.00")).statut(Billet.StatutBillet.ANNULE).dateAchat(LocalDateTime.now().minusDays(1)).build());
    }
}

package fr.ticngo.config;

import fr.ticngo.model.*;
import fr.ticngo.repository.*;
import fr.ticngo.service.AuthService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Initialisation des données de démonstration.
 * Appelé depuis TicnGoApplication si la base est vide.
 */
public class DataInitializer {

    private final AdministrateurRepository adminRepo;
    private final LieuRepository lieuRepo;
    private final SpectacleRepository spectacleRepo;
    private final SeanceRepository seanceRepo;
    private final ClientRepository clientRepo;
    private final BilletRepository billetRepo;
    private final AuthService authService;

    public DataInitializer(AdministrateurRepository adminRepo, LieuRepository lieuRepo,
                           SpectacleRepository spectacleRepo, SeanceRepository seanceRepo,
                           ClientRepository clientRepo, BilletRepository billetRepo,
                           AuthService authService) {
        this.adminRepo = adminRepo;
        this.lieuRepo = lieuRepo;
        this.spectacleRepo = spectacleRepo;
        this.seanceRepo = seanceRepo;
        this.clientRepo = clientRepo;
        this.billetRepo = billetRepo;
        this.authService = authService;
    }

    public void run() {
        // Admin par défaut
        if (adminRepo.count() == 0) {
            Administrateur admin = new Administrateur();
            admin.setEmail("admin@ticngo.fr");
            admin.setMotDePasse(authService.encodePassword("Admin1234!"));
            admin.setNom("Admin");
            admin.setPrenom("Super");
            adminRepo.save(admin);
        }

        if (spectacleRepo.count() > 0) return;

        // Lieux
        Lieu olympia = new Lieu();
        olympia.setNom("L'Olympia");
        olympia.setVille("Paris");
        olympia.setAdresse("28 Bd des Capucines");
        olympia.setCapacite(2000);
        olympia = lieuRepo.save(olympia);

        Lieu zenith = new Lieu();
        zenith.setNom("Zénith de Paris");
        zenith.setVille("Paris");
        zenith.setAdresse("211 Av. Jean Jaurès");
        zenith.setCapacite(6300);
        zenith = lieuRepo.save(zenith);

        // Spectacles
        Spectacle hamlet = new Spectacle();
        hamlet.setTitre("Hamlet");
        hamlet.setCategorie("Théâtre");
        hamlet.setDescription("La grande tragédie de Shakespeare");
        hamlet.setPrixBase(new BigDecimal("45.00"));
        hamlet.setLieu(olympia);
        hamlet.setDateCreation(LocalDateTime.now());
        hamlet = spectacleRepo.save(hamlet);

        Spectacle concert = new Spectacle();
        concert.setTitre("Concert Jazz Night");
        concert.setCategorie("Concert");
        concert.setDescription("Une nuit de jazz");
        concert.setPrixBase(new BigDecimal("35.00"));
        concert.setLieu(zenith);
        concert.setDateCreation(LocalDateTime.now());
        concert = spectacleRepo.save(concert);

        Spectacle comedie = new Spectacle();
        comedie.setTitre("La Comédie du Siècle");
        comedie.setCategorie("Comédie");
        comedie.setDescription("Un spectacle hilarant");
        comedie.setPrixBase(new BigDecimal("29.00"));
        comedie.setLieu(olympia);
        comedie.setDateCreation(LocalDateTime.now());
        comedie = spectacleRepo.save(comedie);

        // Séances
        Seance s1 = new Seance();
        s1.setSpectacle(hamlet);
        s1.setDateHeure(LocalDateTime.now().plusDays(10));
        s1.setPlacesTotales(200);
        s1.setPlacesDisponibles(150);
        s1 = seanceRepo.save(s1);

        Seance s2 = new Seance();
        s2.setSpectacle(concert);
        s2.setDateHeure(LocalDateTime.now().plusDays(15));
        s2.setPlacesTotales(500);
        s2.setPlacesDisponibles(320);
        s2 = seanceRepo.save(s2);

        Seance s3 = new Seance();
        s3.setSpectacle(comedie);
        s3.setDateHeure(LocalDateTime.now().plusDays(5));
        s3.setPlacesTotales(300);
        s3.setPlacesDisponibles(200);
        s3 = seanceRepo.save(s3);

        Seance s4 = new Seance();
        s4.setSpectacle(hamlet);
        s4.setDateHeure(LocalDateTime.now().plusDays(20));
        s4.setPlacesTotales(200);
        s4.setPlacesDisponibles(180);
        s4 = seanceRepo.save(s4);

        // Clients
        Client c1 = new Client();
        c1.setNom("Martin");
        c1.setPrenom("Sophie");
        c1.setEmail("sophie.martin@email.fr");
        c1.setTelephone("06 12 34 56 78");
        c1.setDateInscription(LocalDateTime.now().minusMonths(2));
        c1 = clientRepo.save(c1);

        Client c2 = new Client();
        c2.setNom("Dupont");
        c2.setPrenom("Lucas");
        c2.setEmail("lucas.dupont@email.fr");
        c2.setTelephone("07 98 76 54 32");
        c2.setDateInscription(LocalDateTime.now().minusMonths(1));
        c2 = clientRepo.save(c2);

        Client c3 = new Client();
        c3.setNom("Bernard");
        c3.setPrenom("Emma");
        c3.setEmail("emma.bernard@email.fr");
        c3.setTelephone("06 55 44 33 22");
        c3.setDateInscription(LocalDateTime.now().minusDays(15));
        c3 = clientRepo.save(c3);

        // Billets
        Billet b1 = new Billet();
        b1.setNumeroBillet("TCK-20240301-A1B2C3");
        b1.setClient(c1); b1.setSeance(s1);
        b1.setPrix(new BigDecimal("45.00"));
        b1.setStatut(Billet.StatutBillet.VALIDE);
        b1.setDateAchat(LocalDateTime.now().minusDays(5));
        billetRepo.save(b1);

        Billet b2 = new Billet();
        b2.setNumeroBillet("TCK-20240302-D4E5F6");
        b2.setClient(c2); b2.setSeance(s2);
        b2.setPrix(new BigDecimal("35.00"));
        b2.setStatut(Billet.StatutBillet.VALIDE);
        b2.setDateAchat(LocalDateTime.now().minusDays(3));
        billetRepo.save(b2);

        Billet b3 = new Billet();
        b3.setNumeroBillet("TCK-20240303-G7H8I9");
        b3.setClient(c3); b3.setSeance(s3);
        b3.setPrix(new BigDecimal("29.00"));
        b3.setStatut(Billet.StatutBillet.UTILISE);
        b3.setDateAchat(LocalDateTime.now().minusDays(10));
        billetRepo.save(b3);

        Billet b4 = new Billet();
        b4.setNumeroBillet("TCK-20240304-J0K1L2");
        b4.setClient(c1); b4.setSeance(s4);
        b4.setPrix(new BigDecimal("45.00"));
        b4.setStatut(Billet.StatutBillet.ANNULE);
        b4.setDateAchat(LocalDateTime.now().minusDays(1));
        billetRepo.save(b4);
    }
}

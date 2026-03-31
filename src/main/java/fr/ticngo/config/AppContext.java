package fr.ticngo.config;

import fr.ticngo.repository.*;
import fr.ticngo.service.*;
import fr.ticngo.util.FxmlLoader;
import fr.ticngo.util.NavigationService;

/**
 * Conteneur DI maison (remplace Spring ApplicationContext).
 * Instancie et câble tous les repos, services et utilitaires.
 */
public class AppContext {

    private static AppContext instance;

    // Repositories
    private final AdministrateurRepository administrateurRepository;
    private final LieuRepository lieuRepository;
    private final SpectacleRepository spectacleRepository;
    private final SeanceRepository seanceRepository;
    private final ClientRepository clientRepository;
    private final BilletRepository billetRepository;

    // Services
    private final AuthService authService;
    private final ClientService clientService;
    private final SpectacleService spectacleService;
    private final SeanceService seanceService;
    private final BilletService billetService;
    private final PdfService pdfService;

    // Utilitaires
    private final FxmlLoader fxmlLoader;
    private final NavigationService navigationService;

    private AppContext() {
        administrateurRepository = new AdministrateurRepository();
        lieuRepository           = new LieuRepository();
        spectacleRepository      = new SpectacleRepository(lieuRepository);
        seanceRepository         = new SeanceRepository();
        clientRepository         = new ClientRepository();
        billetRepository         = new BilletRepository();

        authService      = new AuthService(administrateurRepository);
        clientService    = new ClientService(clientRepository);
        spectacleService = new SpectacleService(spectacleRepository);
        seanceService    = new SeanceService(seanceRepository);
        billetService    = new BilletService(billetRepository, seanceRepository);
        pdfService       = new PdfService();

        fxmlLoader       = new FxmlLoader();
        navigationService = new NavigationService(fxmlLoader);
    }

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // --- Repositories ---
    public AdministrateurRepository getAdministrateurRepository() { return administrateurRepository; }
    public LieuRepository getLieuRepository()                     { return lieuRepository; }
    public SpectacleRepository getSpectacleRepository()           { return spectacleRepository; }
    public SeanceRepository getSeanceRepository()                 { return seanceRepository; }
    public ClientRepository getClientRepository()                 { return clientRepository; }
    public BilletRepository getBilletRepository()                 { return billetRepository; }

    // --- Services ---
    public AuthService getAuthService()           { return authService; }
    public ClientService getClientService()       { return clientService; }
    public SpectacleService getSpectacleService() { return spectacleService; }
    public SeanceService getSeanceService()       { return seanceService; }
    public BilletService getBilletService()       { return billetService; }
    public PdfService getPdfService()             { return pdfService; }

    // --- Utilitaires ---
    public FxmlLoader getFxmlLoader()               { return fxmlLoader; }
    public NavigationService getNavigationService() { return navigationService; }
}

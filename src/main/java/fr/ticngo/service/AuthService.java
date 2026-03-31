package fr.ticngo.service;

import fr.ticngo.model.Administrateur;
import fr.ticngo.repository.AdministrateurRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    private final AdministrateurRepository adminRepo;

    public AuthService(AdministrateurRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    public Optional<Administrateur> authenticate(String email, String password) {
        return adminRepo.findByEmail(email)
                .filter(admin -> BCrypt.checkpw(password, admin.getMotDePasse()));
    }

    public String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}

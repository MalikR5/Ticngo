package fr.ticngo.service;

import fr.ticngo.model.Administrateur;
import fr.ticngo.repository.AdministrateurRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AdministrateurRepository adminRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(AdministrateurRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    public Optional<Administrateur> authenticate(String email, String password) {
        return adminRepo.findByEmail(email)
                .filter(admin -> encoder.matches(password, admin.getMotDePasse()));
    }

    public String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}

package fr.ticngo.service;

import fr.ticngo.model.Administrateur;
import fr.ticngo.repository.AdministrateurRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Optional;

public class AuthService {

    private final AdministrateurRepository adminRepo;

    public AuthService(AdministrateurRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    public Optional<Administrateur> authenticate(String email, String password) {
        return adminRepo.findByEmail(email)
                .filter(admin -> checkPassword(password, admin.getMotDePasse()));
    }

    public String encodePassword(String rawPassword) {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        String salt = HexFormat.of().formatHex(saltBytes);
        String hash = sha256(salt + rawPassword);
        return salt + ":" + hash;
    }

    private boolean checkPassword(String rawPassword, String stored) {
        String[] parts = stored.split(":", 2);
        if (parts.length != 2) return false;
        String salt = parts[0];
        String expectedHash = parts[1];
        return sha256(salt + rawPassword).equals(expectedHash);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 non disponible", e);
        }
    }
}

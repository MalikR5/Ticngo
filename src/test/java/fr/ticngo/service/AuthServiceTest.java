package fr.ticngo.service;

import fr.ticngo.model.Administrateur;
import fr.ticngo.repository.AdministrateurRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires de AuthService.
 *
 * Objectif : prouver que le hachage de mot de passe (salt aléatoire + SHA-256)
 * fonctionne correctement et que l'authentification refuse bien un mauvais
 * mot de passe. Le repository est mocké pour isoler la logique de sécurité
 * de l'accès JDBC réel.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

 @Mock
 private AdministrateurRepository adminRepo;

 private AuthService authService;

 @BeforeEach
 void setUp() {
 authService = new AuthService(adminRepo);
 }

 @Test
 void encodePassword_doitProduireUnHashDifferentAChaqueAppel() {
 // Le salt étant aléatoire (SecureRandom), deux encodages du même mot de
 // passe doivent produire deux résultats différents (protection contre
 // les attaques par dictionnaire / rainbow tables).
 String hash1 = authService.encodePassword("MotDePasse123!");
 String hash2 = authService.encodePassword("MotDePasse123!");

 assertNotEquals(hash1, hash2);
 assertTrue(hash1.contains(":"), "Le format attendu est salt:hash");
 }

 @Test
 void authenticate_doitReussir_siEmailEtMotDePasseCorrects() {
 // Arrange : un admin dont le mot de passe stocké est le hash de "secret123"
 String hashStocke = authService.encodePassword("secret123");
 Administrateur admin = new Administrateur();
 admin.setEmail("admin@ticngo.fr");
 admin.setMotDePasse(hashStocke);

 when(adminRepo.findByEmail("admin@ticngo.fr")).thenReturn(Optional.of(admin));

 // Act
 Optional<Administrateur> resultat = authService.authenticate("admin@ticngo.fr", "secret123");

 // Assert
 assertTrue(resultat.isPresent());
 assertEquals("admin@ticngo.fr", resultat.get().getEmail());
 }

 @Test
 void authenticate_doitEchouer_siMotDePasseIncorrect() {
 String hashStocke = authService.encodePassword("bonMotDePasse");
 Administrateur admin = new Administrateur();
 admin.setEmail("admin@ticngo.fr");
 admin.setMotDePasse(hashStocke);

 when(adminRepo.findByEmail("admin@ticngo.fr")).thenReturn(Optional.of(admin));

 Optional<Administrateur> resultat = authService.authenticate("admin@ticngo.fr", "mauvaisMotDePasse");

 assertTrue(resultat.isEmpty());
 }

 @Test
 void authenticate_doitEchouer_siEmailInconnu() {
 when(adminRepo.findByEmail("inconnu@ticngo.fr")).thenReturn(Optional.empty());

 Optional<Administrateur> resultat = authService.authenticate("inconnu@ticngo.fr", "peuImporte");

 assertTrue(resultat.isEmpty());
 }
}

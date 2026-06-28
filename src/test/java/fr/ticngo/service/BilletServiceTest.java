package fr.ticngo.service;

import fr.ticngo.model.Billet;
import fr.ticngo.model.Billet.StatutBillet;
import fr.ticngo.model.Client;
import fr.ticngo.model.Seance;
import fr.ticngo.repository.BilletRepository;
import fr.ticngo.repository.SeanceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de BilletService.
 *
 * Les repositories (accès BDD JDBC) sont mockés avec Mockito : on ne teste pas
 * la base de données ici, mais uniquement la logique métier de la classe
 * (règles de gestion des places, des statuts de billet, etc.), conformément
 * à l'architecture en couches (Controller -> Service -> Repository) du projet.
 */
@ExtendWith(MockitoExtension.class)
class BilletServiceTest {

 @Mock
 private BilletRepository billetRepo;

 @Mock
 private SeanceRepository seanceRepo;

 private BilletService billetService;

 @BeforeEach
 void setUp() {
 billetService = new BilletService(billetRepo, seanceRepo);
 }

 @Test
 void creer_doitLeverUneException_siAucunePlaceDisponible() {
 // Arrange : une séance sans place disponible
 Seance seance = new Seance();
 seance.setId(1);
 seance.setPlacesDisponibles(0);
 Client client = new Client();

 // Act + Assert : la création doit être refusée
 IllegalStateException exception = assertThrows(
 IllegalStateException.class,
 () -> billetService.creer(client, seance, BigDecimal.valueOf(25))
 );

 assertEquals("Plus de places disponibles pour cette séance", exception.getMessage());

 // Aucune écriture en base ne doit avoir lieu si la règle métier est violée
 verify(billetRepo, never()).save(any());
 verify(seanceRepo, never()).save(any());
 }

 @Test
 void creer_doitDecrementerLesPlacesEtSauvegarderLeBillet_siPlacesDisponibles() {
 // Arrange : une séance avec une place restante
 Seance seance = new Seance();
 seance.setId(1);
 seance.setPlacesDisponibles(1);
 Client client = new Client();
 client.setId(42);

 when(billetRepo.save(any(Billet.class))).thenAnswer(invocation -> invocation.getArgument(0));

 // Act
 Billet resultat = billetService.creer(client, seance, BigDecimal.valueOf(25));

 // Assert : le stock de la séance est décrémenté et persisté
 assertEquals(0, seance.getPlacesDisponibles());
 verify(seanceRepo, times(1)).save(seance);

 // Le billet créé porte bien les bonnes informations
 assertEquals(StatutBillet.VALIDE, resultat.getStatut());
 assertEquals(client, resultat.getClient());
 assertEquals(seance, resultat.getSeance());
 assertNotNull(resultat.getNumeroBillet());
 assertTrue(resultat.getNumeroBillet().startsWith("TCK-"));
 verify(billetRepo, times(1)).save(any(Billet.class));
 }

 @Test
 void annuler_doitRemettreLaPlaceEnStock_siLeBilletEstValide() {
 // Arrange : un billet VALIDE lié à une séance
 Seance seance = new Seance();
 seance.setId(1);
 seance.setPlacesDisponibles(0);

 Billet billet = new Billet();
 billet.setId(99);
 billet.setStatut(StatutBillet.VALIDE);
 billet.setSeance(seance);

 when(billetRepo.findById(99)).thenReturn(java.util.Optional.of(billet));

 // Act
 billetService.annuler(99);

 // Assert : le billet passe à ANNULE et la place est restituée
 assertEquals(StatutBillet.ANNULE, billet.getStatut());
 assertEquals(1, seance.getPlacesDisponibles());
 verify(seanceRepo, times(1)).save(seance);
 verify(billetRepo, times(1)).save(billet);
 }

 @Test
 void annuler_doitLeverUneException_siLeBilletNestPasValide() {
 // Arrange : un billet déjà utilisé, qu'on ne peut pas annuler
 Billet billet = new Billet();
 billet.setId(7);
 billet.setStatut(StatutBillet.UTILISE);

 when(billetRepo.findById(7)).thenReturn(java.util.Optional.of(billet));

 // Act + Assert
 IllegalStateException exception = assertThrows(
 IllegalStateException.class,
 () -> billetService.annuler(7)
 );

 assertEquals("Seul un billet VALIDE peut être annulé", exception.getMessage());
 verify(seanceRepo, never()).save(any());
 verify(billetRepo, never()).save(any());
 }

 @Test
 void annuler_doitLeverUneException_siLeBilletEstIntrouvable() {
 when(billetRepo.findById(404)).thenReturn(java.util.Optional.empty());

 assertThrows(
 IllegalArgumentException.class,
 () -> billetService.annuler(404)
 );
 }
}

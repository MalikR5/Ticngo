package fr.ticngo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Billet {

    public enum StatutBillet {
        VALIDE, ANNULE, REMBOURSE, UTILISE
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_billet", unique = true)
    private String numeroBillet;

    @Enumerated(EnumType.STRING)
    private StatutBillet statut;

    private BigDecimal prix;

    @Column(name = "date_achat")
    private LocalDateTime dateAchat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seance_id")
    private Seance seance;
}

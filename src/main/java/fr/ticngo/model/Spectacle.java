package fr.ticngo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "spectacles")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Spectacle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String categorie;

    @Column(name = "prix_base")
    private BigDecimal prixBase;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "lieu_id")
    private Lieu lieu;

    @OneToMany(mappedBy = "spectacle", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Seance> seances;

    @Override
    public String toString() {
        return titre;
    }
}

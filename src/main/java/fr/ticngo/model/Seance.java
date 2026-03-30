package fr.ticngo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "seances")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Seance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "places_disponibles")
    private Integer placesDisponibles;

    @Column(name = "places_totales")
    private Integer placesTotales;

    @ManyToOne
    @JoinColumn(name = "spectacle_id")
    private Spectacle spectacle;

    @OneToMany(mappedBy = "seance", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Billet> billets;

    @Override
    public String toString() {
        if (spectacle != null && dateHeure != null) {
            return spectacle.getTitre() + " — " +
                   dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "Séance #" + id;
    }
}

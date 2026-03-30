package fr.ticngo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "lieux")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Lieu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String adresse;
    private String ville;
    private Integer capacite;

    @OneToMany(mappedBy = "lieu", cascade = CascadeType.ALL)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Spectacle> spectacles;

    @Override
    public String toString() {
        return nom + (ville != null ? " — " + ville : "");
    }
}

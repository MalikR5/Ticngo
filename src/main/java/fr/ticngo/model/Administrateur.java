package fr.ticngo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrateurs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Administrateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String nom;
    private String prenom;

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}

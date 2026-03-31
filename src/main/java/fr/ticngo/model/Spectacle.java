package fr.ticngo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Spectacle {

    private Integer id;
    private String titre;
    private String categorie;
    private String description;
    private String descriptionLongue;
    private String langue;
    private int duree;
    private int ageMinimum;
    private BigDecimal prixBase;
    private Lieu lieu;
    private LocalDateTime dateCreation;

    public Spectacle() {}

    @Override
    public String toString() {
        return titre != null ? titre : "";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDescriptionLongue() { return descriptionLongue; }
    public void setDescriptionLongue(String descriptionLongue) { this.descriptionLongue = descriptionLongue; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public int getAgeMinimum() { return ageMinimum; }
    public void setAgeMinimum(int ageMinimum) { this.ageMinimum = ageMinimum; }

    public BigDecimal getPrixBase() { return prixBase; }
    public void setPrixBase(BigDecimal prixBase) { this.prixBase = prixBase; }

    public Lieu getLieu() { return lieu; }
    public void setLieu(Lieu lieu) { this.lieu = lieu; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}

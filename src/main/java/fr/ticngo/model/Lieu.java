package fr.ticngo.model;

public class Lieu {

    private Integer id;
    private String nom;
    private String adresse;
    private String ville;
    private int capacite;

    public Lieu() {}

    public Lieu(Integer id, String nom, String adresse, String ville, int capacite) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return nom + (ville != null ? " – " + ville : "");
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
}

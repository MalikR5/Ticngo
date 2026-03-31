package fr.ticngo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Billet {

    public enum StatutBillet { VALIDE, ANNULE, REMBOURSE, UTILISE }

    private Integer id;
    private String numeroBillet;
    private Client client;
    private Seance seance;
    private BigDecimal prix;
    private StatutBillet statut;
    private LocalDateTime dateAchat;

    public Billet() {}

    public Billet(Integer id, String numeroBillet, Client client, Seance seance,
                  BigDecimal prix, StatutBillet statut, LocalDateTime dateAchat) {
        this.id = id;
        this.numeroBillet = numeroBillet;
        this.client = client;
        this.seance = seance;
        this.prix = prix;
        this.statut = statut;
        this.dateAchat = dateAchat;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumeroBillet() { return numeroBillet; }
    public void setNumeroBillet(String numeroBillet) { this.numeroBillet = numeroBillet; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Seance getSeance() { return seance; }
    public void setSeance(Seance seance) { this.seance = seance; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public StatutBillet getStatut() { return statut; }
    public void setStatut(StatutBillet statut) { this.statut = statut; }

    public LocalDateTime getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDateTime dateAchat) { this.dateAchat = dateAchat; }
}

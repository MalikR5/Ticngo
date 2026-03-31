package fr.ticngo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Seance {

    private Integer id;
    private Spectacle spectacle;
    private LocalDateTime dateHeure;
    private int placesTotales;
    private int placesDisponibles;

    public Seance() {}

    public Seance(Integer id, Spectacle spectacle, LocalDateTime dateHeure,
                  int placesTotales, int placesDisponibles) {
        this.id = id;
        this.spectacle = spectacle;
        this.dateHeure = dateHeure;
        this.placesTotales = placesTotales;
        this.placesDisponibles = placesDisponibles;
    }

    @Override
    public String toString() {
        String titre = spectacle != null ? spectacle.getTitre() : "?";
        String date = dateHeure != null
                ? dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "?";
        return titre + " — " + date + " (" + placesDisponibles + " places)";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Spectacle getSpectacle() { return spectacle; }
    public void setSpectacle(Spectacle spectacle) { this.spectacle = spectacle; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public int getPlacesTotales() { return placesTotales; }
    public void setPlacesTotales(int placesTotales) { this.placesTotales = placesTotales; }

    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }
}

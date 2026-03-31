package fr.ticngo.repository;

import fr.ticngo.config.DatabaseConnection;
import fr.ticngo.model.Lieu;
import fr.ticngo.model.Seance;
import fr.ticngo.model.Spectacle;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeanceRepository {

    private static final String SELECT_BASE =
        "SELECT se.id, se.date_heure, se.places_totales, se.places_disponibles, " +
        "       sp.id sp_id, sp.titre sp_titre, sp.categorie sp_cat, sp.description sp_desc, " +
        "       sp.prix_base sp_prix, sp.date_creation sp_dc, " +
        "       l.id l_id, l.nom l_nom, l.adresse l_adr, l.ville l_vil, l.capacite l_cap " +
        "FROM seance se " +
        "JOIN spectacle sp ON se.spectacle_id = sp.id " +
        "LEFT JOIN lieu l ON sp.lieu_id = l.id";

    public List<Seance> findAll() {
        List<Seance> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + " ORDER BY se.date_heure")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Optional<Seance> findById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE se.id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public List<Seance> findBySpectacleId(int spectacleId) {
        List<Seance> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE se.spectacle_id=? ORDER BY se.date_heure")) {
            ps.setInt(1, spectacleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Seance save(Seance s) {
        if (s.getId() == null) {
            String sql = "INSERT INTO seance (spectacle_id, date_heure, places_totales, places_disponibles) VALUES (?,?,?,?)";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, s.getSpectacle().getId());
                ps.setObject(2, s.getDateHeure());
                ps.setInt(3, s.getPlacesTotales());
                ps.setInt(4, s.getPlacesDisponibles());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) s.setId(keys.getInt(1));
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "UPDATE seance SET spectacle_id=?, date_heure=?, places_totales=?, places_disponibles=? WHERE id=?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, s.getSpectacle().getId());
                ps.setObject(2, s.getDateHeure());
                ps.setInt(3, s.getPlacesTotales());
                ps.setInt(4, s.getPlacesDisponibles());
                ps.setInt(5, s.getId());
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return s;
    }

    public void deleteById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM seance WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Seance map(ResultSet rs) throws SQLException {
        Spectacle sp = new Spectacle();
        sp.setId(rs.getInt("sp_id"));
        sp.setTitre(rs.getString("sp_titre"));
        sp.setCategorie(rs.getString("sp_cat"));
        sp.setDescription(rs.getString("sp_desc"));
        sp.setPrixBase(rs.getBigDecimal("sp_prix"));
        Timestamp spDc = rs.getTimestamp("sp_dc");
        if (spDc != null) sp.setDateCreation(spDc.toLocalDateTime());
        int lieuId = rs.getInt("l_id");
        if (!rs.wasNull()) {
            sp.setLieu(new Lieu(lieuId, rs.getString("l_nom"), rs.getString("l_adr"),
                                rs.getString("l_vil"), rs.getInt("l_cap")));
        }
        Seance se = new Seance();
        se.setId(rs.getInt("id"));
        se.setSpectacle(sp);
        Timestamp dh = rs.getTimestamp("date_heure");
        if (dh != null) se.setDateHeure(dh.toLocalDateTime());
        se.setPlacesTotales(rs.getInt("places_totales"));
        se.setPlacesDisponibles(rs.getInt("places_disponibles"));
        return se;
    }
}

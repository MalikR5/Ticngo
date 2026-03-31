package fr.ticngo.repository;

import fr.ticngo.config.DatabaseConnection;
import fr.ticngo.model.*;
import fr.ticngo.model.Billet.StatutBillet;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BilletRepository {

    private static final String SELECT_BASE =
        "SELECT b.id, b.numero_billet, b.prix, b.statut, b.date_achat, " +
        "       c.id c_id, c.nom c_nom, c.prenom c_prenom, c.email c_email, c.telephone c_tel, c.adresse c_adr, c.date_inscription c_di, " +
        "       se.id se_id, se.date_heure se_dh, se.places_totales se_pt, se.places_disponibles se_pd, " +
        "       sp.id sp_id, sp.titre sp_titre, sp.categorie sp_cat, sp.description sp_desc, sp.prix_base sp_prix, sp.date_creation sp_dc, " +
        "       l.id l_id, l.nom l_nom, l.adresse l_adr, l.ville l_vil, l.capacite l_cap " +
        "FROM billet b " +
        "JOIN client c ON b.client_id = c.id " +
        "JOIN seance se ON b.seance_id = se.id " +
        "JOIN spectacle sp ON se.spectacle_id = sp.id " +
        "LEFT JOIN lieu l ON sp.lieu_id = l.id";

    public List<Billet> findAll() {
        List<Billet> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + " ORDER BY b.date_achat DESC")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Optional<Billet> findById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE b.id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public List<Billet> searchBillets(String search, StatutBillet statut) {
        List<Billet> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> params = new ArrayList<>();

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasStatut = statut != null;

        if (hasSearch || hasStatut) sql.append(" WHERE ");

        if (hasSearch) {
            sql.append("(b.numero_billet LIKE ? OR LOWER(c.nom) LIKE ? OR LOWER(c.prenom) LIKE ?)");
            String p = "%" + search.toLowerCase() + "%";
            params.add(p); params.add(p); params.add(p);
        }
        if (hasSearch && hasStatut) sql.append(" AND ");
        if (hasStatut) {
            sql.append("b.statut = ?");
            params.add(statut.name());
        }
        sql.append(" ORDER BY b.date_achat DESC");

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public BigDecimal sumRevenu() {
        String sql = "SELECT COALESCE(SUM(prix), 0) FROM billet WHERE statut IN ('VALIDE','UTILISE')";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    public long countByStatut(StatutBillet statut) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM billet WHERE statut=?")) {
            ps.setString(1, statut.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public long count() {
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM billet")) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Billet save(Billet b) {
        if (b.getId() == null) {
            String sql = "INSERT INTO billet (numero_billet, client_id, seance_id, prix, statut, date_achat) VALUES (?,?,?,?,?,?)";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, b.getNumeroBillet());
                ps.setInt(2, b.getClient().getId());
                ps.setInt(3, b.getSeance().getId());
                ps.setBigDecimal(4, b.getPrix());
                ps.setString(5, b.getStatut().name());
                ps.setObject(6, b.getDateAchat());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) b.setId(keys.getInt(1));
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "UPDATE billet SET numero_billet=?, client_id=?, seance_id=?, prix=?, statut=?, date_achat=? WHERE id=?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, b.getNumeroBillet());
                ps.setInt(2, b.getClient().getId());
                ps.setInt(3, b.getSeance().getId());
                ps.setBigDecimal(4, b.getPrix());
                ps.setString(5, b.getStatut().name());
                ps.setObject(6, b.getDateAchat());
                ps.setInt(7, b.getId());
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return b;
    }

    public void deleteById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM billet WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Billet map(ResultSet rs) throws SQLException {
        // Client
        Client client = new Client();
        client.setId(rs.getInt("c_id"));
        client.setNom(rs.getString("c_nom"));
        client.setPrenom(rs.getString("c_prenom"));
        client.setEmail(rs.getString("c_email"));
        client.setTelephone(rs.getString("c_tel"));
        client.setAdresse(rs.getString("c_adr"));
        Timestamp cDi = rs.getTimestamp("c_di");
        if (cDi != null) client.setDateInscription(cDi.toLocalDateTime());

        // Lieu
        Lieu lieu = null;
        int lieuId = rs.getInt("l_id");
        if (!rs.wasNull()) {
            lieu = new Lieu(lieuId, rs.getString("l_nom"), rs.getString("l_adr"),
                            rs.getString("l_vil"), rs.getInt("l_cap"));
        }

        // Spectacle
        Spectacle sp = new Spectacle();
        sp.setId(rs.getInt("sp_id"));
        sp.setTitre(rs.getString("sp_titre"));
        sp.setCategorie(rs.getString("sp_cat"));
        sp.setDescription(rs.getString("sp_desc"));
        sp.setPrixBase(rs.getBigDecimal("sp_prix"));
        sp.setLieu(lieu);
        Timestamp spDc = rs.getTimestamp("sp_dc");
        if (spDc != null) sp.setDateCreation(spDc.toLocalDateTime());

        // Seance
        Seance seance = new Seance();
        seance.setId(rs.getInt("se_id"));
        seance.setSpectacle(sp);
        Timestamp dh = rs.getTimestamp("se_dh");
        if (dh != null) seance.setDateHeure(dh.toLocalDateTime());
        seance.setPlacesTotales(rs.getInt("se_pt"));
        seance.setPlacesDisponibles(rs.getInt("se_pd"));

        // Billet
        Billet b = new Billet();
        b.setId(rs.getInt("id"));
        b.setNumeroBillet(rs.getString("numero_billet"));
        b.setClient(client);
        b.setSeance(seance);
        b.setPrix(rs.getBigDecimal("prix"));
        b.setStatut(StatutBillet.valueOf(rs.getString("statut")));
        Timestamp da = rs.getTimestamp("date_achat");
        if (da != null) b.setDateAchat(da.toLocalDateTime());
        return b;
    }
}

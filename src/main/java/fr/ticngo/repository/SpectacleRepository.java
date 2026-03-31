package fr.ticngo.repository;

import fr.ticngo.config.DatabaseConnection;
import fr.ticngo.model.Lieu;
import fr.ticngo.model.Spectacle;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpectacleRepository {

    private final LieuRepository lieuRepo;

    public SpectacleRepository(LieuRepository lieuRepo) {
        this.lieuRepo = lieuRepo;
    }

    private static final String SELECT_BASE =
        "SELECT s.id, s.titre, s.categorie, s.description, s.prix_base, s.date_creation, " +
        "       l.id l_id, l.nom l_nom, l.adresse l_adr, l.ville l_vil, l.capacite l_cap " +
        "FROM spectacle s LEFT JOIN lieu l ON s.lieu_id = l.id";

    public List<Spectacle> findAll() {
        List<Spectacle> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + " ORDER BY s.titre")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Optional<Spectacle> findById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE s.id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public List<Spectacle> findByTitreContainingIgnoreCase(String titre) {
        List<Spectacle> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE LOWER(s.titre) LIKE ?")) {
            ps.setString(1, "%" + titre.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Spectacle save(Spectacle s) {
        if (s.getId() == null) {
            String sql = "INSERT INTO spectacle (titre, categorie, description, prix_base, lieu_id, date_creation) VALUES (?,?,?,?,?,?)";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getTitre());
                ps.setString(2, s.getCategorie());
                ps.setString(3, s.getDescription());
                ps.setBigDecimal(4, s.getPrixBase());
                if (s.getLieu() != null) ps.setInt(5, s.getLieu().getId());
                else ps.setNull(5, Types.INTEGER);
                ps.setObject(6, s.getDateCreation());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) s.setId(keys.getInt(1));
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "UPDATE spectacle SET titre=?, categorie=?, description=?, prix_base=?, lieu_id=?, date_creation=? WHERE id=?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, s.getTitre());
                ps.setString(2, s.getCategorie());
                ps.setString(3, s.getDescription());
                ps.setBigDecimal(4, s.getPrixBase());
                if (s.getLieu() != null) ps.setInt(5, s.getLieu().getId());
                else ps.setNull(5, Types.INTEGER);
                ps.setObject(6, s.getDateCreation());
                ps.setInt(7, s.getId());
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return s;
    }

    public void deleteById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM spectacle WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public long count() {
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM spectacle")) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Spectacle map(ResultSet rs) throws SQLException {
        Spectacle s = new Spectacle();
        s.setId(rs.getInt("id"));
        s.setTitre(rs.getString("titre"));
        s.setCategorie(rs.getString("categorie"));
        s.setDescription(rs.getString("description"));
        BigDecimal prix = rs.getBigDecimal("prix_base");
        s.setPrixBase(prix);
        Timestamp dc = rs.getTimestamp("date_creation");
        if (dc != null) s.setDateCreation(dc.toLocalDateTime());
        int lieuId = rs.getInt("l_id");
        if (!rs.wasNull()) {
            Lieu lieu = new Lieu(lieuId, rs.getString("l_nom"), rs.getString("l_adr"),
                                 rs.getString("l_vil"), rs.getInt("l_cap"));
            s.setLieu(lieu);
        }
        return s;
    }
}

package fr.ticngo.repository;

import fr.ticngo.config.DatabaseConnection;
import fr.ticngo.model.Administrateur;

import java.sql.*;
import java.util.Optional;

public class AdministrateurRepository {

    public Optional<Administrateur> findByEmail(String email) {
        String sql = "SELECT id, email, mot_de_passe, nom, prenom FROM administrateur WHERE email = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Administrateur save(Administrateur a) {
        if (a.getId() == null) {
            String sql = "INSERT INTO administrateur (email, mot_de_passe, nom, prenom) VALUES (?,?,?,?)";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, a.getEmail());
                ps.setString(2, a.getMotDePasse());
                ps.setString(3, a.getNom());
                ps.setString(4, a.getPrenom());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) a.setId(keys.getInt(1));
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "UPDATE administrateur SET email=?, mot_de_passe=?, nom=?, prenom=? WHERE id=?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, a.getEmail());
                ps.setString(2, a.getMotDePasse());
                ps.setString(3, a.getNom());
                ps.setString(4, a.getPrenom());
                ps.setInt(5, a.getId());
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return a;
    }

    public long count() {
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM administrateur")) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Administrateur map(ResultSet rs) throws SQLException {
        return new Administrateur(
            rs.getInt("id"),
            rs.getString("email"),
            rs.getString("mot_de_passe"),
            rs.getString("nom"),
            rs.getString("prenom")
        );
    }
}

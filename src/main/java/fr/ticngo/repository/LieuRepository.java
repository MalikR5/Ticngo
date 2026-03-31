package fr.ticngo.repository;

import fr.ticngo.config.DatabaseConnection;
import fr.ticngo.model.Lieu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LieuRepository {

    public List<Lieu> findAll() {
        List<Lieu> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nom, adresse, ville, capacite FROM lieu")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Optional<Lieu> findById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, nom, adresse, ville, capacite FROM lieu WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public Lieu save(Lieu l) {
        if (l.getId() == null) {
            String sql = "INSERT INTO lieu (nom, adresse, ville, capacite) VALUES (?,?,?,?)";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, l.getNom());
                ps.setString(2, l.getAdresse());
                ps.setString(3, l.getVille());
                ps.setInt(4, l.getCapacite());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) l.setId(keys.getInt(1));
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "UPDATE lieu SET nom=?, adresse=?, ville=?, capacite=? WHERE id=?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, l.getNom());
                ps.setString(2, l.getAdresse());
                ps.setString(3, l.getVille());
                ps.setInt(4, l.getCapacite());
                ps.setInt(5, l.getId());
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return l;
    }

    public Lieu map(ResultSet rs) throws SQLException {
        return new Lieu(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("adresse"),
            rs.getString("ville"),
            rs.getInt("capacite")
        );
    }
}

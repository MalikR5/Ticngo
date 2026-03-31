package fr.ticngo.repository;

import fr.ticngo.config.DatabaseConnection;
import fr.ticngo.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository {

    public List<Client> findAll() {
        List<Client> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT id, nom, prenom, email, telephone, adresse, date_inscription FROM client ORDER BY nom, prenom")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Optional<Client> findById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, nom, prenom, email, telephone, adresse, date_inscription FROM client WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public Optional<Client> findByEmail(String email) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, nom, prenom, email, telephone, adresse, date_inscription FROM client WHERE email=?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public List<Client> search(String q) {
        List<Client> list = new ArrayList<>();
        String pattern = "%" + q.toLowerCase() + "%";
        String sql = "SELECT id, nom, prenom, email, telephone, adresse, date_inscription FROM client " +
                     "WHERE LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(email) LIKE ? ORDER BY nom, prenom";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Client save(Client cl) {
        if (cl.getId() == null) {
            String sql = "INSERT INTO client (nom, prenom, email, telephone, adresse, date_inscription) VALUES (?,?,?,?,?,?)";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cl.getNom());
                ps.setString(2, cl.getPrenom());
                ps.setString(3, cl.getEmail());
                ps.setString(4, cl.getTelephone());
                ps.setString(5, cl.getAdresse());
                ps.setObject(6, cl.getDateInscription());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) cl.setId(keys.getInt(1));
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            String sql = "UPDATE client SET nom=?, prenom=?, email=?, telephone=?, adresse=?, date_inscription=? WHERE id=?";
            try (Connection c = DatabaseConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, cl.getNom());
                ps.setString(2, cl.getPrenom());
                ps.setString(3, cl.getEmail());
                ps.setString(4, cl.getTelephone());
                ps.setString(5, cl.getAdresse());
                ps.setObject(6, cl.getDateInscription());
                ps.setInt(7, cl.getId());
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return cl;
    }

    public void deleteById(int id) {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM client WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public long count() {
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM client")) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Client map(ResultSet rs) throws SQLException {
        Client cl = new Client();
        cl.setId(rs.getInt("id"));
        cl.setNom(rs.getString("nom"));
        cl.setPrenom(rs.getString("prenom"));
        cl.setEmail(rs.getString("email"));
        cl.setTelephone(rs.getString("telephone"));
        cl.setAdresse(rs.getString("adresse"));
        Timestamp di = rs.getTimestamp("date_inscription");
        if (di != null) cl.setDateInscription(di.toLocalDateTime());
        return cl;
    }
}

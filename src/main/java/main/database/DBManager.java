package main.database;

import main.exceptions.InvalidDataException;
import main.managers.KeyManager;
import main.model.*;

import main.utils.IDGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.TreeMap;


public class DBManager {
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(DBManager.class);

    public DBManager() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", "s466366", "Nifs7jxVOz4anOD2");
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public boolean registerUser(String username, String passwordMd5) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordMd5);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("Ошибка при регистрации пользователя: " + e.getMessage());
        }
        return false;
    }

    public boolean authenticateUser(String username, String passwordMd5) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (!storedHash.equals(passwordMd5)) {
                        throw new InvalidDataException("Неверный логин или пароль.");
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при аутентификации: неверный логин или " + e.getMessage());
        } catch (InvalidDataException e) {
            logger.error("Ошибка при аутентификации: " + e);
        }
        return false;
    }

    public String getUserByLogin(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении пользователя: " + e.getMessage());
        }
        return null;
    }

    private HashMap<String, String> getUsers() {
        HashMap<String, String> users = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                users.put(resultSet.getString("username"), resultSet.getString("password"));
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } finally {
            return users;
        }
    }

    public TreeMap<Integer, Organization> getOrganizations() {
        String sql = "SELECT * FROM Organizations";
        TreeMap<Integer, Organization> organizations = new TreeMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int key = resultSet.getInt("key");
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Double coordinate_x = resultSet.getDouble("coordinates_x");
                long coordinate_y = resultSet.getLong("coordinates_y");
                Coordinates coordinates = new Coordinates(coordinate_x, coordinate_y);
                LocalDate creation_date = LocalDate.ofInstant(resultSet.getTimestamp("creation_date").toInstant(), ZoneId.systemDefault());
                long annual_turnover = resultSet.getLong("annual_turnover");
                OrganizationType organization_type = null;
                if (resultSet.getString("organization_type") != null) {
                    organization_type = OrganizationType.valueOf(resultSet.getString("organization_type"));
                }
                String address_street = resultSet.getString("address_street");
                float town_x = resultSet.getFloat("town_x");
                double town_y = resultSet.getDouble("town_y");
                Long town_z = resultSet.getLong("town_z");
                String username = resultSet.getString("username");
                Location location = new Location(town_x, town_y, town_z);
                Address address = new Address(address_street, location);

                Organization organization = new Organization(id, name, creation_date, coordinates, annual_turnover, organization_type, address, username);
                organizations.put(key, organization);

                KeyManager.registerKey(key);
                IDGenerator.registerID(id);
            }

            return organizations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public void insertOrganizations(int key, Organization organization) {
        try {
            connection.setAutoCommit(false);

            String selectSql = "SELECT key FROM Organizations WHERE key >= ? ORDER BY key DESC FOR UPDATE";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setInt(1, key);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    String updateSql = "UPDATE Organizations SET key = ? WHERE key = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        while (rs.next()) {
                            int currentKey = rs.getInt("key");
                            updateStmt.setInt(1, currentKey + 1);
                            updateStmt.setInt(2, currentKey);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }

            String sql = "INSERT INTO Organizations (key, id, name, coordinates_x, coordinates_y, " +
                    "creation_date, annual_turnover, organization_type, address_street, " +
                    "town_x, town_y, town_z, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            fillAndExecuteSqlStatementForInsert(key, organization, sql);

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public void updateOrganization(int key, Organization organization) {
        String sql = "UPDATE Organizations SET id = ?, name = ?, coordinates_x = ?, coordinates_y = ?, " +
                "creation_date = ?, annual_turnover = ?, organization_type = ?, address_street = ?, " +
                "town_x = ?, town_y = ?, town_z = ?, username = ? WHERE key = ?";
        fillAndExecuteSqlStatementForUpdate(key, organization, sql);
    }

    public void deleteOrganization(int key) {
        String sql = "DELETE FROM Organizations WHERE key = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, key);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public void deleteAllOrganizations(String login) {
        String sql = "DELETE FROM Organizations WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private void fillAndExecuteSqlStatementForInsert(int key, Organization organization, String sql) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, key);
            pstmt.setInt(2, organization.getID());
            pstmt.setString(3, organization.getName());
            pstmt.setDouble(4, organization.getCoordinates().getX());
            pstmt.setDouble(5, organization.getCoordinates().getY());
            pstmt.setDate(6, Date.valueOf(organization.getCreationDate()));
            pstmt.setLong(7, organization.getAnnualTurnover());

            if (organization.getType() != null) {
                pstmt.setString(8, organization.getType().toString());
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }

            if (organization.getOfficialAddress() != null) {
                pstmt.setString(9, organization.getOfficialAddress().getStreet());
                if (organization.getOfficialAddress().getTown() != null) {
                    pstmt.setFloat(10, organization.getOfficialAddress().getTown().getX());
                    pstmt.setDouble(11, organization.getOfficialAddress().getTown().getY());
                    pstmt.setLong(12, organization.getOfficialAddress().getTown().getZ());
                } else {
                    pstmt.setNull(10, Types.FLOAT);
                    pstmt.setNull(11, Types.DOUBLE);
                    pstmt.setNull(12, Types.BIGINT);
                }
            } else {
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.FLOAT);
                pstmt.setNull(11, Types.DOUBLE);
                pstmt.setNull(12, Types.BIGINT);
            }

            pstmt.setString(13, organization.getUsername());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private void fillAndExecuteSqlStatementForUpdate(int key, Organization organization, String sql) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(13, key);
            pstmt.setInt(1, organization.getID());
            pstmt.setString(2, organization.getName());
            pstmt.setDouble(3, organization.getCoordinates().getX());
            pstmt.setDouble(4, organization.getCoordinates().getY());
            pstmt.setDate(5, Date.valueOf(organization.getCreationDate()));
            pstmt.setLong(6, organization.getAnnualTurnover());

            if (organization.getType() != null) {
                pstmt.setString(7, organization.getType().toString());
            } else {
                pstmt.setNull(7, java.sql.Types.VARCHAR);
            }

            if (organization.getOfficialAddress() != null) {
                pstmt.setString(8, organization.getOfficialAddress().getStreet());
                if (organization.getOfficialAddress().getTown() != null) {
                    pstmt.setFloat(9, organization.getOfficialAddress().getTown().getX());
                    pstmt.setDouble(10, organization.getOfficialAddress().getTown().getY());
                    pstmt.setLong(11, organization.getOfficialAddress().getTown().getZ());
                } else {
                    pstmt.setNull(9, Types.FLOAT);
                    pstmt.setNull(10, Types.DOUBLE);
                    pstmt.setNull(11, Types.BIGINT);
                }
            } else {
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.FLOAT);
                pstmt.setNull(10, Types.DOUBLE);
                pstmt.setNull(11, Types.BIGINT);
            }

            pstmt.setString(12, organization.getUsername());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
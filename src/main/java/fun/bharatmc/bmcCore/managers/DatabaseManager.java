package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.models.PlayerData;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {
    private final BMCCore plugin;
    private Connection connection;
    private final String dbUrl;

    public DatabaseManager(BMCCore plugin) {
        this.plugin = plugin;
        this.dbUrl = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/data/core.db";
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            // Ensure data directory exists
            plugin.getDataFolder().mkdirs();
            new File(plugin.getDataFolder(), "data").mkdirs();

            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Create database connection
            connection = DriverManager.getConnection(dbUrl);

            // Create tables
            createTables();

            plugin.getLogger().info("SQLite database initialized successfully!");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database", e);
        }
    }

    private void createTables() {
        String createPlayersTable = """
            CREATE TABLE IF NOT EXISTS players (
                player_uuid TEXT PRIMARY KEY,
                player_name TEXT NOT NULL,
                is_vanished BOOLEAN DEFAULT 0,
                is_flying BOOLEAN DEFAULT 0,
                is_god_mode BOOLEAN DEFAULT 0,
                fly_speed REAL DEFAULT 0.1,
                first_join BIGINT,
                last_login BIGINT,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayersTable);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create database tables", e);
        }
    }

    public void savePlayerData(PlayerData playerData) {
        String sql = """
                    INSERT OR REPLACE INTO players 
                    (player_uuid, player_name, is_vanished, is_flying, is_god_mode, is_frozen, fly_speed, first_join, last_login) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerData.getPlayerUUID().toString());
            pstmt.setString(2, playerData.getPlayerName());
            pstmt.setBoolean(3, playerData.isVanished());
            pstmt.setBoolean(4, playerData.isFlying());
            pstmt.setBoolean(5, playerData.isGodMode());
            pstmt.setBoolean(6, playerData.isFrozen());
            pstmt.setFloat(7, playerData.getFlySpeed());
            pstmt.setLong(8, playerData.getFirstJoin());
            pstmt.setLong(9, playerData.getLastLogin());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data for: " + playerData.getPlayerName(), e);
        }
    }

    public PlayerData loadPlayerData(UUID playerUUID) {
        String sql = "SELECT * FROM players WHERE player_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlayerData playerData = new PlayerData(playerUUID, rs.getString("player_name"));
                playerData.setVanished(rs.getBoolean("is_vanished"));
                playerData.setFlying(rs.getBoolean("is_flying"));
                playerData.setGodMode(rs.getBoolean("is_god_mode"));
                playerData.setFlySpeed(rs.getFloat("fly_speed"));
                playerData.setFirstJoin(rs.getLong("first_join"));
                playerData.setLastLogin(rs.getLong("last_login"));
                return playerData;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load player data for: " + playerUUID, e);
        }

        return null;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(dbUrl);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get database connection", e);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to close database connection", e);
        }
    }

    public File getDatabaseFile() {
        return new File(plugin.getDataFolder(), "data/core.db");
    }
}
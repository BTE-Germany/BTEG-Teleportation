package de.btegermany.teleportation.TeleportationBungee.util;

import de.btegermany.teleportation.TeleportationBungee.TeleportationBungee;

import java.io.File;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class Database {

    private Connection connection;
    private final TeleportationBungee plugin;

    public Database(TeleportationBungee plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        File dir = plugin.getDataFolder();
        if(!dir.exists()) dir.mkdir();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/" + dir.getName() + "/BTEGTeleportationBungee.db");

            try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS warps ('id' INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT, 'name' TEXT NOT NULL, 'city' TEXT NOT NULL, 'state' TEXT NOT NULL, 'latitude' TEXT NOT NULL, 'longitude' TEXT NOT NULL, 'head_id' TEXT NULL DEFAULT NULL, 'yaw' REAL NOT NULL DEFAULT 0 , 'pitch' REAL NOT NULL DEFAULT 0, 'height' REAL NOT NULL)")) {
                executeUpdateSync(preparedStatement);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> executeUpdateAsync(PreparedStatement preparedStatement) {
        return CompletableFuture.runAsync(() -> {
            try {
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void executeUpdateSync(PreparedStatement preparedStatement) {
        try {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ResultSet> executeQueryAsync(PreparedStatement preparedStatement) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return preparedStatement.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ResultSet executeQuerySync(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

}

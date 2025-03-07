package de.btegermany.teleportation.TeleportationBungee.data;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Database {

    private Connection connection;
    private final ConfigReader configReader;

    public Database(ConfigReader configReader) {
        this.configReader = configReader;
    }

    // connect to the database and create the warps table if it doesn't exist yet
    public void connect() {
        List<String> configData = this.configReader.readDataConfig();
        try {
            this.connection = DriverManager.getConnection(configData.get(0), configData.get(1), configData.get(2));

            try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS warps (id INT NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT, name VARCHAR(256) NOT NULL, city VARCHAR(256) NOT NULL, state VARCHAR(128) NOT NULL, latitude VARCHAR(128) NOT NULL, longitude VARCHAR(128) NOT NULL, head_id VARCHAR(256) NULL DEFAULT NULL, yaw FLOAT NOT NULL DEFAULT 0 , pitch FLOAT NOT NULL DEFAULT 0, height DOUBLE NOT NULL)")) {
                this.executeUpdateSync(preparedStatement);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // disconnect from the database
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

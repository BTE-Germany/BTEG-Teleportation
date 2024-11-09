package de.btegermany.teleportation.TeleportationVelocity.data;

import org.slf4j.Logger;

import java.io.File;
import java.sql.*;

public class DatabaseConverter {

    private final Logger logger;
    private final Database database;
    private final File dbFile;

    public DatabaseConverter(Logger logger, Database database, File dbFile) {
        this.logger = logger;
        this.database = database;
        this.dbFile = dbFile;
    }

    // converts the warps table from sqlite to mysql
    public void convertDbFileToDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM warps");
                ResultSet resultSet = preparedStatement.executeQuery()) {
                int i = 0;
                while(resultSet.next()) {
                    try(PreparedStatement preparedStatement1 = this.database.getConnection().prepareStatement("INSERT INTO warps (id, name, city, state, latitude, longitude, head_id, yaw, pitch, height) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        preparedStatement1.setInt(1, resultSet.getInt("id"));
                        preparedStatement1.setString(2, resultSet.getString("name"));
                        preparedStatement1.setString(3, resultSet.getString("city"));
                        preparedStatement1.setString(4, resultSet.getString("state"));
                        preparedStatement1.setString(5, resultSet.getString("latitude"));
                        preparedStatement1.setString(6, resultSet.getString("longitude"));
                        preparedStatement1.setString(7, resultSet.getString("head_id"));
                        preparedStatement1.setFloat(8, resultSet.getFloat("yaw"));
                        preparedStatement1.setFloat(9, resultSet.getFloat("pitch"));
                        preparedStatement1.setDouble(10, resultSet.getDouble("height"));
                        preparedStatement1.executeUpdate();
                    }
                    i++;
                }
                this.logger.info("Conversion successfull: Convertet {} entries!", i);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

}

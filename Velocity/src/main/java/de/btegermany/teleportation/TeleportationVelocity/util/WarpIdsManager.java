package de.btegermany.teleportation.TeleportationVelocity.util;

import de.btegermany.teleportation.TeleportationVelocity.data.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WarpIdsManager {

    private final Database database;
    private final List<Integer> idsAssigned;
    private int idNext;

    public WarpIdsManager(Database database) {
        this.database = database;
        this.idsAssigned = new ArrayList<>();
        this.loadIds();
        this.setIdNextSync();
    }

    private synchronized void loadIds() {
        this.idsAssigned.clear();
        try (PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("SELECT id FROM warps ORDER BY id")) {
            ResultSet resultSet = this.database.executeQuerySync(preparedStatement);
            while (resultSet.next()) {
                int warpId = resultSet.getInt("id");
                while (this.idsAssigned.size() < warpId) {
                    this.idsAssigned.add(-1);
                }
                this.idsAssigned.add(warpId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Integer> getAndClaimNextIdAsync() {
        return CompletableFuture.supplyAsync(this::getAndClaimNextIdSync);
    }

    private synchronized int getAndClaimNextIdSync() {
        int id = this.idNext;
        while (this.idsAssigned.size() <= id) {
            this.idsAssigned.add(-1);
        }
        this.idsAssigned.set(id, id);

        this.setIdNextSync();
        return id;
    }

    public CompletableFuture<Void> releaseIdAsync(int id) {
        return CompletableFuture.runAsync(() -> this.releaseIdSync(id));
    }

    public synchronized void releaseIdSync(int id) {
        this.idsAssigned.set(id, -1);

        this.setIdNextSync();
    }

    private synchronized void setIdNextSync() {
        for(int i = 0; i < this.idsAssigned.size(); i++) {
            int idAtIndex = this.idsAssigned.get(i);
            if(idAtIndex == -1) {
                this.idNext = i;
                return;
            }
        }

        this.idNext = this.idsAssigned.size();
    }

}

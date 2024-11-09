package de.btegermany.teleportation.TeleportationBukkit.util;

import de.btegermany.teleportation.TeleportationAPI.FederalState;
import org.bukkit.entity.Player;

public class WarpInCreation {

    private String name;
    private String city;
    private FederalState state;
    private String headId;
    private final Player player;

    public WarpInCreation(Player player) {
        this.player = player;
    }

    // Setters, Getters

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(FederalState state) {
        this.state = state;
    }

    public void setHeadId(String headId) {
        this.headId = headId;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public FederalState getState() {
        return state;
    }

    public String getHeadId() {
        return headId;
    }

    public Player getPlayer() {
        return player;
    }

}

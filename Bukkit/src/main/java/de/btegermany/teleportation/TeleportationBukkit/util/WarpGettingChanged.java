package de.btegermany.teleportation.TeleportationBukkit.util;

public class WarpGettingChanged {

    private final int id;
    private final String column;
    private String value;

    public WarpGettingChanged(int id, String column) {
        this.id = id;
        this.column = column;
    }

    // Setters, Getters

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }
}

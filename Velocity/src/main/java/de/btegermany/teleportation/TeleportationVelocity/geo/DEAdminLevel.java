package de.btegermany.teleportation.TeleportationVelocity.geo;

import com.alpsbte.alpslib.geo.AdminLevel;

public enum DEAdminLevel implements AdminLevel {

    STATE(4, "state"),
    CITY(6, "city");

    private final int level;
    private final String nominatimKey;

    DEAdminLevel(int level, String nominatimKey) {
        this.level = level;
        this.nominatimKey = nominatimKey;
    }

    @Override
    public Integer getLevel() {
        return level;
    }

    @Override
    public String getNominatimKey() {
        return nominatimKey;
    }

    @Override
    public String getPhotonKey() {
        return null;
    }

}

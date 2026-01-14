package de.btegermany.teleportation.TeleportationVelocity.geo;

import lombok.Builder;

// represents a real life location and holds its data
@Builder
public record GeoLocation(double latitude, double longitude, String country, String state, String city) {}
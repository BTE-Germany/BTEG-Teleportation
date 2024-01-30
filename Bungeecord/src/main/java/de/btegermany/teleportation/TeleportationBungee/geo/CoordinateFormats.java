package de.btegermany.teleportation.TeleportationBungee.geo;

import java.util.regex.Pattern;

public class CoordinateFormats {

    public static boolean isDegrees(String input) {
        return Pattern.compile("\\d+((\\.\\d+)*°*,*)$").matcher(input).find();
    }

    public static boolean isDegreesMinutesSeconds(String input) {
        return Pattern.compile("(\\d+)°").matcher(input).find() && Pattern.compile("(\\d+(\\.\\d+)?['|′])").matcher(input).find() && Pattern.compile("(\\d+(\\.\\d+)?(''|′′|″|\"))").matcher(input).find();
    }

    public static boolean isDegreesMinutes(String input) {
        return Pattern.compile("(\\d+)°").matcher(input).find() && Pattern.compile(".*(\\d+(\\.\\d+)?['|′]).*").matcher(input).find();
    }

}

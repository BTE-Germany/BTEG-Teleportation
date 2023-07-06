package de.btegermany.teleportation.TeleportationBungee.geo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateFormatConverter {

    /**
     * Possible formats:
     * 52.513949 13.378661
     * 52.513949, 13.378661
     * 52°30'50.2"N 13°22'43.2"E
     * 52°30'50.2" 13°22'43.2" (will still be N and E)
     */
    public static double[] toDegrees(String inputLatLon) {
        String[] args = inputLatLon.split(" ");
        String regexDouble = "\\d+((\\.\\d+)*°*,*)$";
        if (args.length == 2 && args[0].matches(regexDouble) && args[1].matches(regexDouble)) {
            args[0] = args[0].replace("°", "").replace(",", "");
            args[1] = args[1].replace("°", "").replace(",", "");
            return new double[] {Double.parseDouble(args[0]), Double.parseDouble(args[1])};
        }

        inputLatLon = inputLatLon.replace("N", "").replace("E", "").replace(" ", "").replace(",", ".");
        double[] degrees = new double[2];
        String regexEndDegrees = "°";
        String regexEndMinutes = "['|′]";
        String regexEndSeconds = "(''|′′|″|\")";
        Matcher matcherDegrees = Pattern.compile("(\\d+)" + regexEndDegrees).matcher(inputLatLon);
        Matcher matcherMinutes = Pattern.compile("\\d+(,\\d+)*" + regexEndMinutes).matcher(inputLatLon);
        Matcher matcherSeconds = Pattern.compile("\\d+(,\\d+)*" + regexEndSeconds).matcher(inputLatLon);
        List<Integer> degreesFound = new ArrayList<>();
        List<Double> minutesFound = new ArrayList<>();
        List<Double> secondsFound = new ArrayList<>();
        while (matcherDegrees.find()) {
            degreesFound.add(Integer.parseInt(matcherDegrees.group().replaceAll(regexEndDegrees, "")));
        }
        while (matcherMinutes.find()) {
            minutesFound.add(Double.parseDouble(matcherMinutes.group().replaceAll(regexEndMinutes, "")));
        }
        while (matcherSeconds.find()) {
            secondsFound.add(Double.parseDouble(matcherSeconds.group().replaceAll(regexEndSeconds, "")));
        }

        if (degreesFound.size() < 2 || minutesFound.size() < 2) {
            return null;
        }
        if(secondsFound.size() < 2) {
            degrees[0] = toDegrees(degreesFound.get(0), minutesFound.get(0));
            degrees[1] = toDegrees(degreesFound.get(1), minutesFound.get(1));
        } else {
            degrees[0] = toDegrees(degreesFound.get(0), minutesFound.get(0).intValue(), secondsFound.get(0));
            degrees[1] = toDegrees(degreesFound.get(1), minutesFound.get(1).intValue(), secondsFound.get(1));
        }
        return degrees;
    }

    public static double toDegrees(int degree, int minutes, double seconds) {
        return (((seconds / 60) + minutes) / 60) + degree;
    }

    public static double toDegrees(int degree, double minutes) {
        return (minutes / 60) + degree;
    }

}

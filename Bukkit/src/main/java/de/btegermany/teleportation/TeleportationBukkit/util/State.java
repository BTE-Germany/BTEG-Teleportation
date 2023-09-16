package de.btegermany.teleportation.TeleportationBukkit.util;

public enum State {

    BADEN_WUERTTEMBERG ("Baden-Württemberg", "BW"),
    BAYERN ("Bayern", "BY"),
    BERLIN ("Berlin", "BE"),
    BRANDENBURG ("Brandenburg", "BB"),
    BREMEN ("Bremen", "HB"),
    HAMBURG ("Hamburg", "HH"),
    HESSEN ("Hessen", "HE"),
    MECKLENBURG_VORPOMMERN ("Mecklenburg-Vorpommern", "MV"),
    NIEDERSACHSEN ("Niedersachsen", "NI"),
    NORDRHEIN_WESTFALEN ("Nordrhein-Westfalen", "NW"),
    RHEINLAND_PFALZ ("Rheinland-Pfalz", "RP"),
    SAARLAND ("Saarland", "SL"),
    SACHSEN ("Sachsen", "SN"),
    SACHSEN_ANHALT ("Sachsen-Anhalt", "ST"),
    SCHLESWIG_HOLSTEIN ("Schleswig-Holstein", "SH"),
    THUERINGEN ("Thüringen", "TH");

    public final String displayName;
    public final String abbreviation;

    State(String displayName, String abbreviation) {
        this.displayName = displayName;
        this.abbreviation = abbreviation;
    }
}

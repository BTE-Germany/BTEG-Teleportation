package de.btegermany.teleportation.TeleportationAPI;

public enum FederalState {

    BADEN_WUERTTEMBERG ("Baden-Württemberg", "BW", "Terra-3"),
    BAYERN ("Bayern", "BY", "Terra-3"),
    BERLIN ("Berlin", "BE", "Terra-1"),
    BRANDENBURG ("Brandenburg", "BB", "Terra-1"),
    BREMEN ("Bremen", "HB", "Terra-2"),
    HAMBURG ("Hamburg", "HH", "Terra-1"),
    HESSEN ("Hessen", "HE", "Terra-2"),
    MECKLENBURG_VORPOMMERN ("Mecklenburg-Vorpommern", "MV", "Terra-1"),
    NIEDERSACHSEN ("Niedersachsen", "NI", "Terra-2"),
    NORDRHEIN_WESTFALEN ("Nordrhein-Westfalen", "NW", "Terra-2"),
    RHEINLAND_PFALZ ("Rheinland-Pfalz", "RP", "Terra-2"),
    SAARLAND ("Saarland", "SL", "Terra-2"),
    SACHSEN ("Sachsen", "SN", "Terra-3"),
    SACHSEN_ANHALT ("Sachsen-Anhalt", "ST", "Terra-3"),
    SCHLESWIG_HOLSTEIN ("Schleswig-Holstein", "SH", "Terra-1"),
    THUERINGEN ("Thüringen", "TH", "Terra-2");

    public final String displayName;
    public final String abbreviation;
    public final String server;

    FederalState(String displayName, String abbreviation, String server) {
        this.displayName = displayName;
        this.abbreviation = abbreviation;
        this.server = server;
    }

    public static FederalState getStateFromInput(String input) {
        for(FederalState state : FederalState.values()) {
            if(state.displayName.equalsIgnoreCase(input) || state.abbreviation.equalsIgnoreCase(input)) {
                return state;
            }
        }
        return null;
    }
}

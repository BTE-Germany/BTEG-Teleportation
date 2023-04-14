package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import java.util.ArrayList;
import java.util.List;

public class BlueprintRange {

    private final BlueprintItem item;
    List<Integer> positions = new ArrayList<>();

    public BlueprintRange(int position, BlueprintItem item) {
        this.item = item;
        positions.add(position);
    }

    public BlueprintRange(int startPosition, int endPosition, BlueprintItem item) {
        this.item = item;
        for(int i = startPosition; i <= endPosition; i++) {
            positions.add(i);
        }
    }

    public BlueprintItem getItem() {
        return item;
    }

}

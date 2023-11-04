package de.btegermany.teleportation.TeleportationBukkit.util;

import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public interface TabExecutorEnhanced extends TabExecutor {

    // returns all suggestions that start with the input (case-insensitive)
    default List<String> getValidSuggestions(String input, String... suggestions) {
        List<String> validSuggestions = new ArrayList<>();
        for(String suggestion : suggestions) {
            if(!suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                continue;
            }
            validSuggestions.add(suggestion);
        }
        return validSuggestions;
    }

}

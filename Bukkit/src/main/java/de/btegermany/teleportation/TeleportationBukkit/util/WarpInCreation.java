package de.btegermany.teleportation.TeleportationBukkit.util;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.entity.Player;

public class WarpInCreation {

    private String name;
    private String city;
    private State state;
    private String headId;
    private final Player player;
    private int currentQuestionIndex;
    private final String[] questions = new String[] {"Name eingeben:", "Stadt eingeben:", "Bundesland eingeben:", "HeadId eingeben (optional, zum Überspringen \"skip\" eingeben):"};

    public WarpInCreation(Player player) {
        this.player = player;
        this.currentQuestionIndex = 0;
    }

    public void sendCurrentQuestion() {
        player.sendMessage(TeleportationBukkit.getFormattedMessage(questions[currentQuestionIndex]));
    }

    public void processInput(String input) {
        switch (currentQuestionIndex) {
            case 0:
                setName(input);
                break;
            case 1:
                setCity(input);
                break;
            case 2:
                for(State state : State.values()) {
                    if(state.displayName.equalsIgnoreCase(input) || state.abbreviation.equalsIgnoreCase(input)) {
                        setState(state);
                        currentQuestionIndex++;
                        return;
                    }
                }
                player.sendMessage(TeleportationBukkit.getFormattedMessage(String.format("§9\"%s\" §6ist weder Name noch eine gültige Abkürzung eines Bundeslandes. Bitte überprüfe deine Eingabe.", input)));
                return;
            case 3:
                setHeadId(input.equals("skip") ? null : input);
                break;
        }
        currentQuestionIndex++;
    }

    public boolean isComplete() {
        return currentQuestionIndex == questions.length;
    }

    // Setters, Getters

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(State state) {
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

    public State getState() {
        return state;
    }

    public String getHeadId() {
        return headId;
    }

    public Player getPlayer() {
        return player;
    }

}

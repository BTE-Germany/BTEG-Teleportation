package de.btegermany.teleportation.TeleportationBukkit.util;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import org.bukkit.entity.Player;

public class WarpInCreation {

    private String name;
    private String city;
    private String state;
    private double latitude;
    private double longitude;
    private String headId;
    private final Player player;
    private int currentQuestionIndex;
    private final String[] questions = new String[] {"Name eingeben:", "Stadt eingeben:", "Bundesland eingeben:", "Koordinaten eingeben:", "HeadId eingeben (optional, zum Überspringen \"skip\" eingeben):"};

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
                setState(input);
                break;
            case 3:
                setLatitude(Double.parseDouble(input.split(" ")[0].replace(",", "")));
                setLongitude(Double.parseDouble(input.split(" ")[1]));
                break;
            case 4:
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

    public void setState(String state) {
        this.state = state;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public String getState() {
        return state;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getHeadId() {
        return headId;
    }

    public Player getPlayer() {
        return player;
    }
}

package de.jaskerx.btegteleportation.bukkit.main;

import java.util.Calendar;

import org.bukkit.entity.Player;

public class PendingTpPlayer {
	
	Player player;
	Calendar cCreation;
	
	public PendingTpPlayer(Player player) {
		this.player = player;
		cCreation = Calendar.getInstance();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isValid() {
		Calendar cNow = Calendar.getInstance();
		cCreation.add(Calendar.MINUTE, 1);
		if(cNow.before(cCreation)) {
			return true;
		}
		
		return false;
	}
}

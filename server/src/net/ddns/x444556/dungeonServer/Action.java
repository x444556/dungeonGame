package net.ddns.x444556.dungeonServer;

public class Action {
	
	public PlayerInfo PlayerInfo;
	public String Command;

	public Action(PlayerInfo player, String cmd) {
		PlayerInfo = player;
		Command = cmd;
	}

}

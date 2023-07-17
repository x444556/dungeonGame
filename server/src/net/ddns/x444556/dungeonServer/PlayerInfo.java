package net.ddns.x444556.dungeonServer;

import io.netty.channel.ChannelHandlerContext;

public class PlayerInfo {
	
	public double X, Y, Z;
	public double Xrot, Yrot, Zrot;
	public boolean isWalking = false;
	public boolean isSneaking = false;
	public String Nickname = "Player";
	
	public ChannelHandlerContext ctx;
	
	public static PlayerInfo[] EmptyArray = new PlayerInfo[0];

	public PlayerInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public String GetPos() {
		return "" + X + " " + Y + " " + Z;
	}
	public String GetRot() {
		return "" + Xrot + " " + Yrot + " " + Zrot;
	}
}

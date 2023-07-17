package net.ddns.x444556.dungeon;

import javax.media.opengl.GL2;

public class StarterSword extends Item {

	public StarterSword(double x, double y, double z, double rx, double ry, double rz) {
		super("textures\\red_sword.png", x, y, z, rx, ry, rz);
	}
	
	@Override
	public void Render(GL2 gl) {
		super.Render(gl);
	}

}

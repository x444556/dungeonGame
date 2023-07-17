package net.ddns.x444556.dungeon;

import javax.media.opengl.GL2;

public class SapphireSword extends Item {

	public SapphireSword(double x, double y, double z, double rx, double ry, double rz, boolean smooth) {
		super((smooth ? "textures\\cool_sword_1.png" : "textures\\cool_sword_0.png"), x, y, z, rx, ry, rz);
	}
	
	@Override
	public void Render(GL2 gl) {
		super.Render(gl);
	}

}

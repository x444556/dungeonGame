package net.ddns.x444556.dungeon;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Humanoid extends Entity {
	private long walkingSince = 0;
	public double walkSpeed = 700; // millis per step
	public double walkAngle = Math.PI/2;
	
	public Item RightHand;
	
	public boolean isVisible = true;
	public boolean isSneaking = false;
	
	private String skin;
	private Texture skintexture = null;

	public Humanoid(double x, double y, double z, String skin) {
		super();
		X = x;
		Y = y;
		Z = z;
		this.skin = skin;
	}
	
	public Humanoid SetWalking(boolean isWalking) {
		if(isWalking && walkingSince == 0) {
			walkingSince = System.currentTimeMillis();
		}
		else if(!isWalking && walkingSince > 0) {
			walkingSince = 0;
		}
		return this;
	}
	public boolean GetWalking() {
		return walkingSince > 0;
	}
	
	private void glVertexRotX(GL2 gl, double rotX, double x, double y, double z) {
		gl.glVertex3d(x, z*Math.sin(rotX) + (y-0.12875)*Math.cos(rotX) + 0.12875, z*Math.cos(rotX) - (y-0.12875)*Math.sin(rotX));
	}
	private void glVertexRotX(GL2 gl, double rotX, double x, double y, double z, double yo) {
		gl.glVertex3d(x, z*Math.sin(rotX)+(y-0.12875+yo)*Math.cos(rotX)+0.12875-yo, z*Math.cos(rotX)-(y-0.12875+yo)*Math.sin(rotX));
	}

	@Override
	public void Render(GL2 gl) {
		super.Render(gl);
		
		double walkingAngle = ((System.currentTimeMillis() - walkingSince + walkSpeed/2.0) % walkSpeed) / walkSpeed * walkAngle;
		if((System.currentTimeMillis() - walkingSince + walkSpeed/2) % (2*walkSpeed) >= walkSpeed) walkingAngle = walkAngle - walkingAngle;
		walkingAngle -= Math.PI/4;
		
		if(walkingSince == 0) walkingAngle = 0;
		
		if(skintexture == null) {
			try {
				skintexture = TextureIO.newTexture(new File(skin), true);
				skintexture.bind(gl);
				skintexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
				skintexture.enable(gl);
			} catch (GLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		gl.glTranslated(X, Y, Z);
		gl.glRotated(Yrot * 180.0 / Math.PI, 0, 1, 0);
		gl.glTranslated(0, 0.0375, 0);
		
		if(isVisible) {
			skintexture.bind(gl);
			
			if(isSneaking) {
				gl.glTranslated(0, -0.1575*Math.sin(Math.PI/8), 0.1575*Math.cos(Math.PI/8));
				gl.glRotated(45.0, 1, 0, 0);
			}
			
			// Head
			gl.glTranslated(0, 0.08, 0);
			gl.glTranslated(0, -0.09, 0);
			gl.glRotated(Xrot * 180.0 / Math.PI, 1, 0, 0);
			if(isSneaking) gl.glRotated(-45.0, 1, 0, 0);
			gl.glTranslated(0, 0.09, 0);
			gl.glBegin( GL2.GL_QUADS );   
			// back
			gl.glColor3f(0.85f, 0.85f, 0.85f);
			gl.glTexCoord2d(3/8.0, 2/8.0);
			gl.glVertex3d(-0.09, -0.09, -0.09);
			gl.glTexCoord2d(3/8.0, 1/8.0);
			gl.glVertex3d(-0.09, 0.09, -0.09);
			gl.glTexCoord2d(4/8.0, 1/8.0);
			gl.glVertex3d(0.09, 0.09, -0.09);
			gl.glTexCoord2d(4/8.0, 2/8.0);
			gl.glVertex3d(0.09, -0.09, -0.09);
			// front
			gl.glColor3f(0.95f, 0.95f, 0.95f);
			gl.glTexCoord2d(1/8.0, 2/8.0);
			gl.glVertex3d(0.09, -0.09, 0.09);
			gl.glTexCoord2d(1/8.0, 1/8.0);
			gl.glVertex3d(0.09, 0.09, 0.09);
			gl.glTexCoord2d(2/8.0, 1/8.0);
			gl.glVertex3d(-0.09, 0.09, 0.09);
			gl.glTexCoord2d(2/8.0, 2/8.0);
			gl.glVertex3d(-0.09, -0.09, 0.09);
			// left
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(2/8.0, 2/8.0);
			gl.glVertex3d(-0.09, -0.09, 0.09);
			gl.glTexCoord2d(2/8.0, 1/8.0);
			gl.glVertex3d(-0.09, 0.09, 0.09);
			gl.glTexCoord2d(3/8.0, 1/8.0);
			gl.glVertex3d(-0.09, 0.09, -0.09);
			gl.glTexCoord2d(3/8.0, 2/8.0);
			gl.glVertex3d(-0.09, -0.09, -0.09);
			// right
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(0/8.0, 2/8.0);
			gl.glVertex3d(0.09, -0.09, -0.09);
			gl.glTexCoord2d(0/8.0, 1/8.0);
			gl.glVertex3d(0.09, 0.09, -0.09);
			gl.glTexCoord2d(1/8.0, 1/8.0);
			gl.glVertex3d(0.09, 0.09, 0.09);
			gl.glTexCoord2d(1/8.0, 2/8.0);
			gl.glVertex3d(0.09, -0.09, 0.09);
			// top
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glTexCoord2d(1/8.0, 0/8.0);
			gl.glVertex3d(-0.09, 0.09, -0.09);
			gl.glTexCoord2d(1/8.0, 1/8.0);
			gl.glVertex3d(-0.09, 0.09, 0.09);
			gl.glTexCoord2d(2/8.0, 1/8.0);
			gl.glVertex3d(0.09, 0.09, 0.09);
			gl.glTexCoord2d(2/8.0, 0/8.0);
			gl.glVertex3d(0.09, 0.09, -0.09);
			// bottom
			gl.glColor3f(0.8f, 0.8f, 0.8f);
			gl.glTexCoord2d(2/8.0, 0/8.0);
			gl.glVertex3d(-0.09, -0.09, 0.09);
			gl.glTexCoord2d(2/8.0, 1/8.0);
			gl.glVertex3d(-0.09, -0.09, -0.09);
			gl.glTexCoord2d(3/8.0, 1/8.0);
			gl.glVertex3d(0.09, -0.09, -0.09);
			gl.glTexCoord2d(3/8.0, 0/8.0);
			gl.glVertex3d(0.09, -0.09, 0.09);
			gl.glEnd();
			gl.glTranslated(0, -0.09, 0);
			if(isSneaking)gl.glRotated(45.0, 1, 0, 0);
			gl.glRotated(-Xrot * 180.0 / Math.PI, 1, 0, 0);
			gl.glTranslated(0, 0.09, 0);
			gl.glTranslated(0, -0.08, 0);
			
			// Body
			gl.glTranslated(0, -0.1675, 0);
			gl.glBegin( GL2.GL_QUADS );   
			// back
			gl.glColor3f(0.85f, 0.85f, 0.85f);
			gl.glTexCoord2d(1/2.0, 1/2.0);
			gl.glVertex3d(-0.1075, -0.1075, -0.05);
			gl.glTexCoord2d(1/2.0, 5/16.0);
			gl.glVertex3d(-0.1075, 0.1575, -0.05);
			gl.glTexCoord2d(5/8.0, 5/16.0);
			gl.glVertex3d(0.1075, 0.1575, -0.05);
			gl.glTexCoord2d(5/8.0, 1/2.0);
			gl.glVertex3d(0.1075, -0.1075, -0.05);
			// front
			gl.glColor3f(0.95f, 0.95f, 0.95f);
			gl.glTexCoord2d(5/16.0, 5/16.0);
			gl.glVertex3d(-0.1075, 0.1575, 0.05);
			gl.glTexCoord2d(5/16.0, 8/16.0);
			gl.glVertex3d(-0.1075, -0.1075, 0.05);
			gl.glTexCoord2d(7/16.0, 8/16.0);
			gl.glVertex3d(0.1075, -0.1075, 0.05);
			gl.glTexCoord2d(7/16.0, 5/16.0);
			gl.glVertex3d(0.1075, 0.1575, 0.05);
			// left
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(8/16.0, 1/2.0);
			gl.glVertex3d(-0.1075, -0.1075, 0.05);
			gl.glTexCoord2d(8/16.0, 5/16.0);
			gl.glVertex3d(-0.1075, 0.1575, 0.05);
			gl.glTexCoord2d(7/16.0, 5/16.0);
			gl.glVertex3d(-0.1075, 0.1575, -0.05);
			gl.glTexCoord2d(7/16.0, 1/2.0);
			gl.glVertex3d(-0.1075, -0.1075, -0.05);
			// right
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(4/16.0, 1/2.0);
			gl.glVertex3d(0.1075, -0.1075, -0.05);
			gl.glTexCoord2d(4/16.0, 5/16.0);
			gl.glVertex3d(0.1075, 0.1575, -0.05);
			gl.glTexCoord2d(5/16.0, 5/16.0);
			gl.glVertex3d(0.1075, 0.1575, 0.05);
			gl.glTexCoord2d(5/16.0, 1/2.0);
			gl.glVertex3d(0.1075, -0.1075, 0.05);
			// top
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glTexCoord2d(5/16.0, 1/4.0);
			gl.glVertex3d(-0.1075, 0.1575, -0.05);
			gl.glTexCoord2d(5/16.0, 5/16.0);
			gl.glVertex3d(-0.1075, 0.1575, 0.05);
			gl.glTexCoord2d(7/16.0, 5/16.0);
			gl.glVertex3d(0.1075, 0.1575, 0.05);
			gl.glTexCoord2d(7/16.0, 1/4.0);
			gl.glVertex3d(0.1075, 0.1575, -0.05);
			// bottom
			gl.glColor3f(0.8f, 0.8f, 0.8f);
			gl.glTexCoord2d(7/16.0, 1/4.0);
			gl.glVertex3d(-0.1075, -0.1075, 0.05);
			gl.glTexCoord2d(7/16.0, 5/16.0);
			gl.glVertex3d(-0.1075, -0.1075, -0.05);
			gl.glTexCoord2d(9/16.0, 5/16.0);
			gl.glVertex3d(0.1075, -0.1075, -0.05);
			gl.glTexCoord2d(9/16.0, 1/4.0);
			gl.glVertex3d(0.1075, -0.1075, 0.05);
			gl.glEnd();
			gl.glTranslated(0, 0.1675, 0);
			
			// Arm(left)
			gl.glTranslated(0, -0.1175, 0);
			gl.glBegin( GL2.GL_QUADS );   
			// back
			gl.glColor3f(0.85f, 0.85f, 0.85f);
			gl.glTexCoord2d(48/64.0, 1);
			glVertexRotX(gl, -walkingAngle, -0.2, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(48/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 1);
			glVertexRotX(gl, -walkingAngle, -0.1075, -0.1575, -0.05, 0.05);
			// front
			gl.glColor3f(0.95f, 0.95f, 0.95f);
			gl.glTexCoord2d(36/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(36/64.0, 64/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 64/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, 0.1075, 0.05, 0.05);
			// left
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(36/64.0, 1);
			glVertexRotX(gl, -walkingAngle, -0.2, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(36/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(32/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(32/64.0, 1);
			glVertexRotX(gl, -walkingAngle, -0.2, -0.1575, -0.05, 0.05);
			// right
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(44/64.0, 1);
			glVertexRotX(gl, -walkingAngle, -0.1075, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 56/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 1);
			glVertexRotX(gl, -walkingAngle, -0.1075, -0.1575, 0.05, 0.05);
			// top
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glTexCoord2d(36/64.0, 52/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(36/64.0, 48/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 48/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 52/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, 0.1075, -0.05, 0.05);
			// bottom
			gl.glColor3f(0.8f, 0.8f, 0.8f);
			gl.glTexCoord2d(40/64.0, 52/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 48/64.0);
			glVertexRotX(gl, -walkingAngle, -0.2, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 48/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 52/64.0);
			glVertexRotX(gl, -walkingAngle, -0.1075, -0.1575, 0.05, 0.05);
			gl.glEnd();
			gl.glTranslated(0, 0.1175, 0);
			
			// Arm(right)
			gl.glTranslated(0, -0.1175, 0);
			gl.glBegin( GL2.GL_QUADS );   
			// back
			gl.glColor3f(0.85f, 0.85f, 0.85f);
			gl.glTexCoord2d(52/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(52/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(56/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(56/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, -0.1575, -0.05, 0.05);
			// front
			gl.glColor3f(0.95f, 0.95f, 0.95f);
			gl.glTexCoord2d(52/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(52/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(56/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(56/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, 0.1075, 0.05, 0.05);
			// left
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(48/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(48/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(52/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(52/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, -0.1575, -0.05, 0.05);
			// right
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(40/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(40/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 24/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 32/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, -0.1575, 0.05, 0.05);
			// top
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glTexCoord2d(44/64.0, 20/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, 0.1075, -0.05, 0.05);
			gl.glTexCoord2d(44/64.0, 16/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(48/64.0, 16/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, 0.1075, 0.05, 0.05);
			gl.glTexCoord2d(48/64.0, 20/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, 0.1075, -0.05, 0.05);
			// bottom
			gl.glColor3f(0.8f, 0.8f, 0.8f);
			gl.glTexCoord2d(52/64.0, 20/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, -0.1575, 0.05, 0.05);
			gl.glTexCoord2d(52/64.0, 16/64.0);
			glVertexRotX(gl, walkingAngle, 0.1075, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(48/64.0, 16/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, -0.1575, -0.05, 0.05);
			gl.glTexCoord2d(48/64.0, 20/64.0);
			glVertexRotX(gl, walkingAngle, 0.2, -0.1575, 0.05, 0.05);
			gl.glEnd();
			gl.glTranslated(0, 0.1175, 0);
			
			if(isSneaking) {
				gl.glRotated(-45.0, 1, 0, 0);
				gl.glTranslated(0, 0.1575*Math.sin(Math.PI/8), -0.1575*Math.cos(Math.PI/8));
			}
			
			// Leg(left)
			gl.glTranslated(0, -0.38, 0);
			gl.glBegin( GL2.GL_QUADS );   
			// back
			gl.glColor3f(0.85f, 0.85f, 0.85f);
			gl.glTexCoord2d(3/16.0, 1/2.0);
			glVertexRotX(gl, walkingAngle, -0.1075, -0.1575, -0.05);
			gl.glTexCoord2d(3/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, 0.1075, -0.05);
			gl.glTexCoord2d(1/4.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, 0, 0.1075, -0.05);
			gl.glTexCoord2d(1/4.0, 1/2.0);
			glVertexRotX(gl, walkingAngle, 0, -0.1575, -0.05);
			// front
			gl.glColor3f(0.95f, 0.95f, 0.95f);
			gl.glTexCoord2d(1/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, 0.1075, 0.05);
			gl.glTexCoord2d(1/16.0, 8/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, -0.1575, 0.05);
			gl.glTexCoord2d(1/8.0, 8/16.0);
			glVertexRotX(gl, walkingAngle, 0, -0.1575, 0.05);
			gl.glTexCoord2d(1/8.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, 0, 0.1075, 0.05);
			// left
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(0, 5/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, 0.1075, -0.05);
			gl.glTexCoord2d(0, 8/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, -0.1575, -0.05);
			gl.glTexCoord2d(1/16.0, 8/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, -0.1575, 0.05);
			gl.glTexCoord2d(1/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, 0.1075, 0.05);
			// right
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(3/16.0, 1/2.0);
			glVertexRotX(gl, walkingAngle, 0, -0.1575, -0.05);
			gl.glTexCoord2d(3/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, 0, 0.1075, -0.05);
			gl.glTexCoord2d(2/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, 0, 0.1075, 0.05);
			gl.glTexCoord2d(2/16.0, 1/2.0);
			glVertexRotX(gl, walkingAngle, 0, -0.1575, 0.05);
			// top
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glTexCoord2d(1/16.0, 1/4.0);
			glVertexRotX(gl, walkingAngle, -0.1075, 0.1075, -0.05);
			gl.glTexCoord2d(1/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, 0.1075, 0.05);
			gl.glTexCoord2d(2/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, 0, 0.1075, 0.05);
			gl.glTexCoord2d(2/16.0, 1/4.0);
			glVertexRotX(gl, walkingAngle, 0, 0.1075, -0.05);
			// bottom
			gl.glColor3f(0.8f, 0.8f, 0.8f);
			gl.glTexCoord2d(2/16.0, 1/4.0);
			glVertexRotX(gl, walkingAngle, -0.1075, -0.1575, -0.05);
			gl.glTexCoord2d(2/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, -0.1075, -0.1575, 0.05);
			gl.glTexCoord2d(3/16.0, 5/16.0);
			glVertexRotX(gl, walkingAngle, 0, -0.1575, 0.05);
			gl.glTexCoord2d(3/16.0, 1/4.0);
			glVertexRotX(gl, walkingAngle, 0, -0.1575, -0.05);
			gl.glEnd();
			gl.glTranslated(0, 0.38, 0);
						
			// Leg(right)
			gl.glTranslated(0, -0.38, 0);
			gl.glBegin( GL2.GL_QUADS );   
			// back
			gl.glColor3f(0.85f, 0.85f, 0.85f);
			gl.glTexCoord2d(7/16.0, 2/2.0);
			glVertexRotX(gl, -walkingAngle, 0, -0.1575, -0.05);
			gl.glTexCoord2d(7/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0, 0.1075, -0.05);
			gl.glTexCoord2d(2/4.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, 0.1075, -0.05);
			gl.glTexCoord2d(2/4.0, 2/2.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, -0.1575, -0.05);
			// front
			gl.glColor3f(0.95f, 0.95f, 0.95f);
			gl.glTexCoord2d(5/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0, 0.1075, 0.05);
			gl.glTexCoord2d(5/16.0, 16/16.0);
			glVertexRotX(gl, -walkingAngle, 0, -0.1575, 0.05);
			gl.glTexCoord2d(3/8.0, 16/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, -0.1575, 0.05);
			gl.glTexCoord2d(3/8.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, 0.1075, 0.05);
			// left
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(4/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0, 0.1075, -0.05);
			gl.glTexCoord2d(4/16.0, 16/16.0);
			glVertexRotX(gl, -walkingAngle, 0, -0.1575, -0.05);
			gl.glTexCoord2d(5/16.0, 16/16.0);
			glVertexRotX(gl, -walkingAngle, 0, -0.1575, 0.05);
			gl.glTexCoord2d(5/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0, 0.1075, 0.05);
			// right
			gl.glColor3f(0.9f, 0.9f, 0.9f);
			gl.glTexCoord2d(7/16.0, 1);
			glVertexRotX(gl, -walkingAngle, 0.1075, -0.1575, -0.05);
			gl.glTexCoord2d(7/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, 0.1075, -0.05);
			gl.glTexCoord2d(6/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, 0.1075, 0.05);
			gl.glTexCoord2d(6/16.0, 1);
			glVertexRotX(gl, -walkingAngle, 0.1075, -0.1575, 0.05);
			// top
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glTexCoord2d(5/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0, 0.1075, -0.05);
			gl.glTexCoord2d(5/16.0, 12/16.0);
			glVertexRotX(gl, -walkingAngle, 0, 0.1075, 0.05);
			gl.glTexCoord2d(6/16.0, 12/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, 0.1075, 0.05);
			gl.glTexCoord2d(6/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, 0.1075, -0.05);
			// bottom
			gl.glColor3f(0.8f, 0.8f, 0.8f);
			gl.glTexCoord2d(6/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0, -0.1575, 0.05);
			gl.glTexCoord2d(6/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0, -0.1575, -0.05);
			gl.glTexCoord2d(7/16.0, 12/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, -0.1575, -0.05);
			gl.glTexCoord2d(7/16.0, 13/16.0);
			glVertexRotX(gl, -walkingAngle, 0.1075, -0.1575, 0.05);
			gl.glEnd();
			gl.glTranslated(0, 0.38, 0);
		}
		
		if(RightHand != null) {
			if(isSneaking) {
				gl.glTranslated(0, -0.1575*Math.sin(Math.PI/8), 0.1575*Math.cos(Math.PI/8));
				gl.glRotated(45.0, 1, 0, 0);
			}
			gl.glTranslated(0,  -0.1175, 0);
			RightHand.X = -0.15375;
			RightHand.Y = (-0.15375)*Math.cos(walkingAngle)-0.15375;
			RightHand.Z = (-0.15375-0.1075)*Math.sin(walkingAngle)-0.15375;
			RightHand.Xrot = Math.PI;
			RightHand.Yrot = -Math.PI/2;
			RightHand.Zrot = -walkingAngle+Math.PI/4*3;
			RightHand.Render(gl);
			gl.glTranslated(0,  0.1175, 0);
			if(isSneaking) {
				gl.glRotated(-45.0, 1, 0, 0);
				gl.glTranslated(0, 0.1575*Math.sin(Math.PI/8), -0.1575*Math.cos(Math.PI/8));
			}
		}

		gl.glTranslated(0, -0.0375, 0);
		gl.glRotated(-Yrot * 180.0 / Math.PI, 0, 1, 0);
		gl.glTranslated(-X, -Y, -Z);
	}
}

package net.ddns.x444556.dungeon;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.image.BufferedImage;

public class Item extends Entity {

	private long droppedSince=0;
	
	private BufferedImage texture;
	private String texturepath;
	private Texture itemtexture;
	private int[] texturepixelsalpha;
	
	private boolean noThick = false;
	
	public Item(String texture, double x, double y, double z, double rx, double ry, double rz) {
		super();
		
		X = x;
		Y = y;
		Z = z;
		Xrot = rx;
		Yrot = ry;
		Zrot = rz;
		texturepath = texture;
		try {
			if(texture != null) this.texture = ImageIO.read(new File(texture));
			if(this.texture != null) {
				texturepixelsalpha = new int[this.texture.getHeight()*this.texture.getWidth()];
				for(int ty=0; ty<this.texture.getHeight(); ty++) {
					for(int tx=0; tx<this.texture.getWidth(); tx++) {
						texturepixelsalpha[ty*this.texture.getWidth()+tx] = this.texture.getRGB(tx, ty)>>24&0xFF;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Item SetDropped(boolean isDropped) {
		if(isDropped) droppedSince = System.currentTimeMillis();
		else droppedSince = 0;
		
		return this;
	}
	public boolean GetDropped() {
		return droppedSince > 0;
	}
	public Item SetNoThick(boolean noThick) {
		this.noThick = noThick;
		return this;
	}
	public boolean GetNoThick() {
		return noThick;
	}
	
	@Override
	public void Render(GL2 gl) {
		super.Render(gl);
		
		if(texture == null) {
			gl.glTranslated(X, Y+(droppedSince > 0 ? 0.03*Math.sin((System.currentTimeMillis() - droppedSince) / 500.0) : 0), Z);
			gl.glRotated(Xrot*180.0/Math.PI, 1, 0, 0);
			gl.glRotated(Yrot*180.0/Math.PI + 
					(droppedSince > 0 ? (System.currentTimeMillis() - droppedSince)%7000.0/7000.0*360.0 : 0), 0, 1, 0);
			gl.glRotated(Zrot*180.0/Math.PI, 0, 0, 1);
			gl.glBegin( GL2.GL_QUADS );
			gl.glColor3f(0.0f, 0.0f, 1.0f);
			gl.glVertex3f(-0.15f, -0.15f, 0.0f);
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glVertex3f(0.15f, -0.15f, 0.0f);
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			gl.glVertex3f(0.15f, 0.15f, 0.0f);
			gl.glColor3f(1.0f, 1.0f, 0.0f);
			gl.glVertex3f(-0.15f, 0.15f, 0.0f);
			gl.glEnd();
			gl.glRotated(-Xrot*180.0/Math.PI, 1, 0, 0);
			gl.glRotated(-Yrot*180.0/Math.PI - 
					(droppedSince > 0 ? (System.currentTimeMillis() - droppedSince)%7000.0/7000.0*360.0 : 0), 0, 1, 0);
			gl.glRotated(-Zrot*180.0/Math.PI, 0, 0, 1);
			gl.glTranslated(-X, (droppedSince > 0 ? -0.03*Math.sin((System.currentTimeMillis() - droppedSince) / 500.0) : 0) - Y, -Z);
		}
		else {
			if(itemtexture == null) {
				try {
					itemtexture = TextureIO.newTexture(new File(texturepath), true);
					itemtexture.bind(gl);
					itemtexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
					itemtexture.enable(gl);
				} catch (GLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			itemtexture.bind(gl);
			
			gl.glTranslated(X, Y+(droppedSince > 0 ? 0.03*Math.sin((System.currentTimeMillis() - droppedSince) / 500.0) : 0), Z);
			gl.glRotated(Xrot*180.0/Math.PI, 1, 0, 0);
			gl.glRotated(Yrot*180.0/Math.PI + 
					(droppedSince > 0 ? (System.currentTimeMillis() - droppedSince)%7000.0/7000.0*360.0 : 0), 0, 1, 0);
			gl.glTranslated(-0.15, -0.15, 0);
			gl.glRotated(Zrot*180.0/Math.PI, 0, 0, 1);
			gl.glTranslated(0.15, 0.15, 0);
			
			gl.glTranslated(-0.15, 0.15, 0);
			gl.glBegin( GL2.GL_QUADS );
			
			int height = texture.getHeight();
			int width = texture.getWidth();
			int alphalimit = 100;
			for(int y=0; y<height; y++) {
				for(int x=0; x<width; x++) {
					if(texturepixelsalpha[y*width+x] > alphalimit) {
						gl.glColor3f(1.0f, 1.0f, 1.0f);
						
						if(noThick) {
							gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
							gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, 0);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
							gl.glVertex3f(x*0.3f/width, -y*0.3f/height, 0);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
							gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, 0);
							gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
							gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, 0);
							
							gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
							gl.glVertex3f(x*0.3f/width, -y*0.3f/height, 0);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
							gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, 0);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
							gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, 0);
							gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
							gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, 0);
						}
						else {
							// back
							gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
							gl.glVertex3f(x*0.3f/width, -y*0.3f/height, -0.01f);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
							gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, -0.01f);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
							gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, -0.01f);
							gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
							gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, -0.01f);
							
							// front
							gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
							gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, 0.01f);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
							gl.glVertex3f(x*0.3f/width, -y*0.3f/height, 0.01f);
							gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
							gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, 0.01f);
							gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
							gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, 0.01f);

							// top
							if(y==0 || texturepixelsalpha[(y-1)*width+x] <= alphalimit) {
								gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
								gl.glVertex3f(x*0.3f/width, -y*0.3f/height, 0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
								gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, 0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
								gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, -0.01f);
								gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
								gl.glVertex3f(x*0.3f/width, -y*0.3f/height, -0.01f);
							}
							
							// bottom
							if(y==width-1 || texturepixelsalpha[(y+1)*width+x] <= alphalimit){
								gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
								gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, 0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
								gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, 0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
								gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, -0.01f);
								gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
								gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, -0.01f);
							}
							
							// left
							if(x==0 || texturepixelsalpha[y*width+(x-1)] <= alphalimit){
								gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
								gl.glVertex3f(x*0.3f/width, -y*0.3f/height, -0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
								gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, -0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
								gl.glVertex3f(x*0.3f/width, -(y+1)*0.3f/height, 0.01f);
								gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
								gl.glVertex3f(x*0.3f/width, -y*0.3f/height, 0.01f);
							}
							
							// right
							if(x==width-1 || texturepixelsalpha[y*width+(x+1)] <= alphalimit){
								gl.glTexCoord2d(1.0*x/width, 1.0*y/height);
								gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, 0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*y/height);
								gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, 0.01f);
								gl.glTexCoord2d(1.0*(x+1)/width, 1.0*(y+1)/height);
								gl.glVertex3f((x+1)*0.3f/width, -(y+1)*0.3f/height, -0.01f);
								gl.glTexCoord2d(1.0*x/width, 1.0*(y+1)/height);
								gl.glVertex3f((x+1)*0.3f/width, -y*0.3f/height, -0.01f);
							}
						}
					}
				}
			}
			gl.glEnd();
			gl.glTranslated(0.15, -0.15, 0);

			gl.glTranslated(-0.15, -0.15, 0);
			gl.glRotated(-Zrot*180.0/Math.PI, 0, 0, 1);
			gl.glTranslated(0.15, 0.15, 0);
			gl.glRotated(-Yrot*180.0/Math.PI - 
					(droppedSince > 0 ? (System.currentTimeMillis() - droppedSince)%7000.0/7000.0*360.0 : 0), 0, 1, 0);
			gl.glRotated(-Xrot*180.0/Math.PI, 1, 0, 0);
			gl.glTranslated(-X, -Y-(droppedSince > 0 ? 0.03*Math.sin((System.currentTimeMillis() - droppedSince) / 500.0) : 0), -Z);
		}
	}
}

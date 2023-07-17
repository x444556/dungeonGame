package net.ddns.x444556.dungeon;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Program implements GLEventListener, java.awt.event.KeyListener, java.awt.event.MouseListener, 
		java.awt.event.MouseMotionListener {
	private GLU glu = new GLU(); 
	private final Set<Integer> pressedKeys = new HashSet<>();
	
	public double playerX=0.0, playerY=0.0, playerZ=0.0;
	public double playerXlast=0.0, playerZlast=0.0;
	public double playerRotY=0.0, playerRotX=0.0; // In Radian
	public double playerRotYlast=0.0, playerRotXlast=0.0;
	public double playerSpeed=1.0;
	public double playerRotSpeed=1.5;
	
	private long playerLastMove = System.currentTimeMillis();
	
	public ArrayList<Entity> Entities = new ArrayList<Entity>();
	public ArrayList<Item> DroppedItems = new ArrayList<Item>();
	public ArrayList<PlayerInfo> Players = new ArrayList<PlayerInfo>();
	
	public float fov = 80.0f;
	public float fovWalk = 80.0f;
	public float fovSprint = 85.0f;
	
	private int lastMouseX=0, lastMouseY=0, mouseX=0, mouseY=0;
	private boolean lockMouse = true;
	
	private int width=1, height=1;
	private Frame frame;
	
	private int mapX = 15;
	private int mapZ = 15;
	private boolean[][] map = new boolean[][] {
		{true, false, false, false, false, false, false, true, false, false, false, false, false, false, true,},
		{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false,},
		{true, false, false, false, false, false, false, true, false, false, false, false, false, false, true,},
		{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false,},
		{true, false, false, false, false, false, false, true, false, false, false, false, false, false, true,},
		{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,},
		{true, false, false, false, false, false, true, false, true, false, false, false, false, false, true,},
		{false, false, true, true, false, false, false, (false), false, false, false, false, false, false, false,},
		{true, false, false, false, false, false, true, false, true, false, false, false, false, false, true,},
		{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,},
		{true, false, false, false, false, false, false, true, false, false, false, false, false, false, true,},
		{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false,},
		{true, false, false, false, false, false, false, true, false, false, false, false, false, false, true,},
		{false, false, false, false, false, false, false, true, false, false, false, false, false, false, false,},
		{true, false, false, false, false, false, false, true, false, false, false, false, false, false, true,}
	};
	
	private Texture walltexture = null;
	private Texture floortexture = null;
	private Texture ceilingtexture = null;
	
	private boolean playerMovedLastUpdate = false;
	private boolean playerSneakedLastUpdate = false;
	
	public Humanoid Player;
	
	public Client Client;
	public boolean disableOnline = true;
	
	public Program() {
		
	}
	
	public void start() {
		GLProfile gp = GLProfile.get(GLProfile.GL2);
		GLCapabilities cap = new GLCapabilities(gp);
		cap.setDepthBits(64);
		GLCanvas gc = new GLCanvas(cap);
		gc.addGLEventListener(this);
		gc.setSize(350, 350);
		
		frame = new Frame("AWT Frame");
		frame.add(gc);
		frame.setSize(500, 400);  
		frame.addKeyListener(this);
		gc.addKeyListener(this);
		gc.addMouseMotionListener(this);
		gc.addMouseListener(this);
		frame.addMouseListener(this);
		frame.setVisible(true);
		
		if(lockMouse) {
			int[] pixels = new int[16 * 16];
			Image image = Toolkit.getDefaultToolkit().createImage(
			        new MemoryImageSource(16, 16, pixels, 0, 16));
			Cursor transparentCursor =
			        Toolkit.getDefaultToolkit().createCustomCursor
			             (image, new Point(0, 0), "invisibleCursor");
			frame.setCursor(transparentCursor);
		}

		if(!disableOnline) {
			Client = new Client();
			try {
				Client.run("127.0.0.1", 8088);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Client.Send("JOIN Player_"+(System.currentTimeMillis()&0xFFF));
		}
		
		Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
		    @Override
		    public void uncaughtException(Thread th, Throwable ex) {
		        System.out.println("Uncaught exception: " + ex + ": " + ex.getMessage());
		        ex.printStackTrace();
		    }
		};
		Thread t = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	long lastDisplay = System.currentTimeMillis();
		        while(true) {
		        	float deltaTime = (System.currentTimeMillis() - lastDisplay) / 1000.0f;
		    		lastDisplay = System.currentTimeMillis();
		    		
		    		if(!disableOnline) GetChangesFromServer();
		    		update(deltaTime);
		    		if(!disableOnline) SendChangesToServer();
		        	gc.display();
		        	
		        	try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    }
		});
		t.setUncaughtExceptionHandler(h);
		t.start();
	}

	public static void main(String[] args) {
		Program p = new Program();
		
		p.Player = new Humanoid(0, 0, 0, "textures\\skin_empty.png");
		p.Player.isVisible = false;
		p.Player.walkAngle/=2.0;
		
		p.Entities.add(new Humanoid(0, 0.0, -1, "textures\\skin0.png").SetWalking(false));
		p.Entities.add(new Humanoid(4, 0.0, -1, "textures\\skin1.png").SetWalking(true));
		((Humanoid)(p.Entities.get(1))).RightHand = new Item("textures\\cool_sword_0.png", 3, -0.28, 1, 0, 0, 0);
		((Humanoid)(p.Entities.get(0))).RightHand = new StarterSword(3, -0.28, 1, 0, 0, 0);
		p.DroppedItems.add(new StarterSword(3, -0.28, -1, 0, 0, 0).SetDropped(true));
		p.DroppedItems.add(new StarterSword(2.5, -0.28, -1, 0, 0, 0).SetDropped(true).SetNoThick(true));
		p.DroppedItems.add(new SapphireSword(3, -0.28, 1, 0, 0, 0, false).SetDropped(true));
		p.DroppedItems.add(new SapphireSword(2.5, -0.28, 1, 0, 0, 0, false).SetDropped(true).SetNoThick(true));
		p.DroppedItems.add(new SapphireSword(3, -0.28, 2, 0, 0, 0, true).SetDropped(true));
		p.DroppedItems.add(new SapphireSword(2.5, -0.28, 2, 0, 0, 0, true).SetDropped(true).SetNoThick(true));
		
		p.start();
	}
	
	public static double Distance(double x0, double y0, double z0, double x1, double y1, double z1) {
		return Math.sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0) + (z1-z0)*(z1-z0));
	}
	
	private void SendChangesToServer() {
		if(Player.GetWalking()) {
			if(!playerMovedLastUpdate) Client.Send("WALK true");
			Client.Send("MOV " + -playerX + " " + -playerY + " " + -playerZ);
			playerMovedLastUpdate = true;
		}
		else if(playerMovedLastUpdate) {
			Client.Send("WALK false");
			playerMovedLastUpdate = false;
		}
		
		if(playerRotX != playerRotXlast || playerRotX != playerRotXlast) {
			Client.Send("ROT " + playerRotX + " " + (-playerRotY + 2*Math.PI/2) + " 0.0");
		}
		
		if(Player.isSneaking && !playerSneakedLastUpdate) {
			Client.Send("SNEAK true");
			playerSneakedLastUpdate = true;
		}
		else if(!Player.isSneaking && playerSneakedLastUpdate) {
			Client.Send("SNEAK false");
			playerSneakedLastUpdate = false;
		}
	}
	private void GetChangesFromServer() {
		while(Client.ActionQueue.peek() != null) {
			Action action = null;
			try {
	    		action = Client.ActionQueue.poll();
	    		if(action != null) {
	    			//System.out.println(action.Command);
	    			
	        		String[] splits = StringUtil.split(StringUtil.split(action.Command, '\n')[0], ' ');
	            	//for(String str : splits) System.out.println(str);
	        		
	        		PlayerInfo p = null;
	        		for(PlayerInfo pi : Players) {
	        			if(pi.Nickname.equals(splits[1])) {
	        				p = pi;
	        				break;
	        			}
	        		}
	        		if(p == null) {
	        			p = new PlayerInfo();
	        			p.Nickname = splits[1];
	        			Players.add(p);
	        			p.Humanoid = new Humanoid(0, 0, 0, "textures\\skin2.png");
	        			Entities.add(p.Humanoid);
	        		}
	            	
	            	if(action.Command.startsWith("JOIN")) {
	            		// Do nothing, already done in "if(p == null)"
	            	}
	            	else if(splits[0].equalsIgnoreCase("MOV")) {
	            		p.X = Double.parseDouble(splits[2]);
	            		p.Y = Double.parseDouble(splits[3]);
	            		p.Z = Double.parseDouble(splits[4]);
	            	}
	            	else if(splits[0].equalsIgnoreCase("ROT")) {
	            		p.Xrot = Double.parseDouble(splits[2]);
	            		p.Yrot = Double.parseDouble(splits[3]);
	            		p.Zrot = Double.parseDouble(splits[4]);
	            	}
	            	else if(splits[0].equalsIgnoreCase("WALK")) {
	            		p.isWalking = (splits[2].equalsIgnoreCase("true") ? true : false);
	            	}
	            	else if(splits[0].equalsIgnoreCase("SNEAK")) {
	            		p.isSneaking = (splits[2].equalsIgnoreCase("true") ? true : false);
	            	}
	    		}
	    		else {
	    			System.out.println("Action==null");
	    		}
			}
			catch(Exception e) {
				e.printStackTrace();
				System.out.println(	"[ERR->ACTION_INF:print] Action is " + (action == null ? "" : "NOT ") + "null");
				System.out.println(	"[ERR->ACTION_INF:print] Palyer is " + (action.PlayerInfo == null ? "" : "NOT ") + "null");
				if(action.PlayerInfo != null) {
					System.out.println(	"[ERR->ACTION_INF:print] Action by \"" + action.PlayerInfo.Nickname + "\"");
					System.out.println(	"[ERR->ACTION_INF:print] Action at " + action.PlayerInfo.X + " " + action.PlayerInfo.Y + " " + 
							action.PlayerInfo.Z + " ");
				}
				System.out.println(	"[ERR->ACTION_INF:print] Action == \"" + action.Command + "\"");
			}
		}
		for(PlayerInfo pi : Players) {
			pi.Humanoid.X = pi.X;
			pi.Humanoid.Y = pi.Y;
			pi.Humanoid.Z = pi.Z;
			pi.Humanoid.Xrot = pi.Xrot;
			pi.Humanoid.Yrot = pi.Yrot;
			pi.Humanoid.Zrot = pi.Zrot;
			pi.Humanoid.isSneaking = pi.isSneaking;
			pi.Humanoid.SetWalking(pi.isWalking);
		}
	}
	private void update(float deltaTime) {
		playerRotXlast = playerRotX;
		playerRotYlast = playerRotY;
		
		playerRotY = (playerRotY + ((lastMouseX - mouseX) / (double)width * (fov / 180.0 * Math.PI))) % (2*Math.PI);
		playerRotX = (playerRotX + ((lastMouseY - mouseY) / (double)height * (fov * (width/height) / 180.0 * Math.PI))) % (2*Math.PI);
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		
		playerXlast = playerX;
		playerZlast = playerZ;
		
		boolean playerMoved = false;
		boolean playerMovedBackwards = false;
		
		if(pressedKeys.contains(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
		if(pressedKeys.contains(KeyEvent.VK_F1)) {
			if(lockMouse) frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lockMouse = !lockMouse;
			pressedKeys.remove(KeyEvent.VK_F1);
		}
		
		if(pressedKeys.contains(KeyEvent.VK_SHIFT)) fov = fovSprint;
		else fov = fovWalk;

		double speedMult = 1.0;
		if(pressedKeys.contains(KeyEvent.VK_SHIFT)) speedMult *= 2;
		if(pressedKeys.contains(KeyEvent.VK_CONTROL)) speedMult *= 0.5;
		
		if(pressedKeys.contains(KeyEvent.VK_W)) {
			playerX += Math.cos((playerRotY + Math.PI/2.0) % (2*Math.PI)) * playerSpeed * speedMult * deltaTime;
			playerZ += Math.sin((playerRotY + Math.PI/2.0) % (2*Math.PI)) * playerSpeed * speedMult * deltaTime;
			playerMoved = true;
		}
		if(pressedKeys.contains(KeyEvent.VK_S)) {
			playerX -= Math.cos((playerRotY + Math.PI/2.0) % (2*Math.PI)) * playerSpeed * speedMult * deltaTime;
			playerZ -= Math.sin((playerRotY + Math.PI/2.0) % (2*Math.PI)) * playerSpeed * speedMult * deltaTime;
			playerMoved = true;
			playerMovedBackwards = true;
		}
		if(pressedKeys.contains(KeyEvent.VK_A)) {
			playerX += Math.cos(playerRotY) * playerSpeed * speedMult * deltaTime;
			playerZ += Math.sin(playerRotY) * playerSpeed * speedMult * deltaTime;
			playerMoved = true;
		}
		if(pressedKeys.contains(KeyEvent.VK_D)) {
			playerX -= Math.cos(playerRotY) * playerSpeed * speedMult * deltaTime;
			playerZ -= Math.sin(playerRotY) * playerSpeed * speedMult * deltaTime;
			playerMoved = true;
		}
		
		if(pressedKeys.contains(KeyEvent.VK_G)) {
			pressedKeys.remove(KeyEvent.VK_G);
			Item dropped = null;
			if(Player.RightHand != null) {
				dropped = Player.RightHand;
				Player.RightHand = null;
				dropped.X = -playerX;
				dropped.Y = -0.28;
				dropped.Z = -playerZ;
				dropped.Xrot = 0;
				dropped.Yrot = 0;
				dropped.Zrot = 0;
				dropped.SetDropped(true);
				DroppedItems.add(dropped);
			}
			for(int i=0; i<DroppedItems.size(); i++) {
				Item I = DroppedItems.get(i);
				if(I != dropped && Distance(-playerX, 0, -playerZ, I.X, 0, I.Z) <= 0.5) { 
					DroppedItems.remove(I);
					I.SetDropped(false);
					Player.RightHand = I;
					i--;
					break;
				}
			}
		}
		
		if(pressedKeys.contains(KeyEvent.VK_LEFT)) {
			playerRotY = (playerRotY - playerRotSpeed * deltaTime) % (2*Math.PI);
		}
		if(pressedKeys.contains(KeyEvent.VK_RIGHT)) {
			playerRotY = (playerRotY + playerRotSpeed * deltaTime) % (2*Math.PI);
		}
		
		while(playerRotY < 0) playerRotY += 2*Math.PI;
		while(playerRotY >= 2*Math.PI) playerRotY -= 2*Math.PI;
		
		if(playerRotX < -Math.PI/2) playerRotX = -Math.PI/2;
		if(playerRotX > Math.PI/2) playerRotX = Math.PI/2;
		
		if(playerX != playerXlast || playerZ != playerZlast) {
			playerLastMove = System.currentTimeMillis();
		}
		//if(System.currentTimeMillis() - playerLastMove <= 100) Player.SetWalking(true);
		//else Player.SetWalking(false);
		playerMoved = playerMoved && (playerX != playerXlast || playerZ != playerZlast);
		Player.SetWalking(playerMoved);
		((Humanoid)Entities.get(0)).SetWalking(playerMoved);
		
		Player.X = -playerX;
		Player.Y = 0.0;
		Player.Z = -playerZ;
		Player.Yrot = -playerRotY + Math.PI;
		Player.Xrot = playerRotX;
		Player.isSneaking = pressedKeys.contains(KeyEvent.VK_CONTROL);
		((Humanoid)Entities.get(0)).Xrot = playerRotX;
		//((Humanoid)Entities.get(0)).Yrot = -playerRotY + Math.PI;
		((Humanoid)Entities.get(0)).isSneaking = pressedKeys.contains(KeyEvent.VK_CONTROL);
	}

	@Override
	public void display(GLAutoDrawable drawable) {		
		final GL2 gl = drawable.getGL().getGL2();
		
		if(walltexture == null) {
			try {
				walltexture = TextureIO.newTexture(new File("textures\\floor0.png"), true);
				walltexture.enable(gl);
			} catch (GLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(floortexture == null) {
			try {
				floortexture = TextureIO.newTexture(new File("textures\\floor0.png"), true);
				floortexture.bind(gl);
				floortexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
				floortexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
				floortexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
				floortexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
				floortexture.enable(gl);
			} catch (GLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(ceilingtexture == null) {
			try {
				ceilingtexture = TextureIO.newTexture(new File("textures\\floor0.png"), true);
				ceilingtexture.bind(gl);
				ceilingtexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
				ceilingtexture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
				ceilingtexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
				ceilingtexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
				ceilingtexture.enable(gl);
			} catch (GLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		final float h = (float) width / (float) height;  
		gl.glViewport(0, 0, width, height);  
		gl.glMatrixMode(GL2.GL_PROJECTION);  
		gl.glLoadIdentity();  
		glu.gluPerspective(fov, h, 0.1, 50.0);  
		gl.glMatrixMode(GL2.GL_MODELVIEW);  
		gl.glLoadIdentity();
		
		gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
	    //gl.glEnable(GL2.GL_LIGHT0);
		//gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glLoadIdentity();

		if(pressedKeys.contains(KeyEvent.VK_CONTROL)) gl.glTranslated(0, 0.1, 0);
		gl.glRotated(playerRotX * 180.0 / Math.PI, 1, 0, 0);
		gl.glRotated(playerRotY * 180.0 / Math.PI, 0, 1, 0);
		gl.glTranslated(playerX, playerY-0.07 + 
				(false && System.currentTimeMillis() - playerLastMove >= 5000 ? 
				0.02*Math.sin(0.0005*(System.currentTimeMillis() - playerLastMove - 5000)) : 0), playerZ);
		
		/*float[] amb = { 0.1f, 0.1f, 0.1f, 1.0f };
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, amb, 0);
		float[] pos = { 1.0f, 1.0f, 1.0f, 1.0f };
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
		float[] dif = { 1.0f, 1.0f, 1.0f, 1.0f };
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, dif, 0);*/

		floortexture.bind(gl);
		gl.glTranslated(-0.5, 0, -0.5);
		gl.glBegin( GL2.GL_QUADS );
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		gl.glTexCoord2d(0.0, 0.0);
		gl.glVertex3f(-mapX/2, -0.5f, mapZ/2+1);
		gl.glTexCoord2d(mapX, 0.0);
		gl.glVertex3f(mapX/2+1, -0.5f, mapZ/2+1);
		gl.glTexCoord2d(mapX, mapZ);
		gl.glVertex3f(mapX/2+1, -0.5f, -mapZ/2);
		gl.glTexCoord2d(0.0, mapZ);
		gl.glVertex3f(-mapX/2, -0.5f,-mapZ/2);
		gl.glEnd();
		ceilingtexture.bind(gl);
		gl.glBegin( GL2.GL_QUADS );
		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glTexCoord2d(0.0, 0.0);
		gl.glVertex3f(-mapX/2, 0.5f, -mapZ/2);
		gl.glTexCoord2d(mapX, 0.0);
		gl.glVertex3f(mapX/2+1, 0.5f, -mapZ/2);
		gl.glTexCoord2d(mapX, mapZ);
		gl.glVertex3f(mapX/2+1, 0.5f, mapZ/2+1);
		gl.glTexCoord2d(0.0, mapZ);
		gl.glVertex3f(-mapX/2, 0.5f, mapZ/2+1);
		gl.glEnd();
		gl.glTranslated(0.5, 0, 0.5);

		walltexture.bind(gl);
		gl.glBegin(GL2.GL_QUADS);
		for(int z=0; z<mapZ; z++) {
			for(int x=0; x<mapX; x++) {
				if(map[z][x]) {
					gl.glColor3f(0.85f, 0.85f, 0.85f);
					gl.glTexCoord2d(1.0, 0.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), -0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(1.0, 1.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), 0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(0.0, 1.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), 0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(0.0, 0.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), -0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glColor3f(0.95f, 0.95f, 0.95f);
					gl.glTexCoord2d(0.0, 1.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), 0.5, 0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(0.0, 0.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), -0.5, 0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(1.0, 0.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), -0.5, 0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(1.0, 1.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), 0.5, 0.5+(z-(int)(mapZ/2)));
					gl.glColor3f(0.9f, 0.9f, 0.9f);
					gl.glTexCoord2d(0.0, 1.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), 0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(0.0, 0.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), -0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(1.0, 0.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), -0.5, 0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(1.0, 1.0);
					gl.glVertex3d(-0.5+(x-(int)(mapX/2)), 0.5, 0.5+(z-(int)(mapZ/2)));
					gl.glColor3f(0.9f, 0.9f, 0.9f);
					gl.glTexCoord2d(1.0, 0.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), -0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(1.0, 1.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), 0.5, -0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(0.0, 1.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), 0.5, 0.5+(z-(int)(mapZ/2)));
					gl.glTexCoord2d(0.0, 0.0);
					gl.glVertex3d(0.5+(x-(int)(mapX/2)), -0.5, 0.5+(z-(int)(mapZ/2)));
				}
			}
		}
		gl.glEnd();

		    
		for(Entity e : Entities) {
			e.Render(gl);
		}
		for(Item i : DroppedItems) {
			i.Render(gl);
		}
		//gl.glDisable(GL2.GL_DEPTH_TEST);
		Player.Render(gl);
		//gl.glEnable(GL2.GL_DEPTH_TEST);
		
		gl.glFlush(); 
	}
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();  
		if(height <= 0) height = 1;  
		              
		final float h = (float) width / (float) height;  
		gl.glViewport(0, 0, width, height);  
		gl.glMatrixMode(GL2.GL_PROJECTION);  
		gl.glLoadIdentity();  
		          
		glu.gluPerspective(fov, h, 0.1, 50.0);  
		gl.glMatrixMode(GL2.GL_MODELVIEW);  
		gl.glLoadIdentity();  
		
		this.width = width;
		this.height = height;
	}


	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		pressedKeys.add(e.getKeyCode());
	}
	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		pressedKeys.remove(e.getKeyCode());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		if(lockMouse) {
			mouseX += width/2 - MouseInfo.getPointerInfo().getLocation().getX() + frame.getX();
			mouseY += height/2 - MouseInfo.getPointerInfo().getLocation().getY() + frame.getY();
			
			try {
				new Robot().mouseMove( frame.getX() + width/2, frame.getY() + height/2 );
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(lockMouse && !lockMouseLast) {
			int[] pixels = new int[16 * 16];
			Image image = Toolkit.getDefaultToolkit().createImage(
			        new MemoryImageSource(16, 16, pixels, 0, 16));
			Cursor transparentCursor =
			        Toolkit.getDefaultToolkit().createCustomCursor
			             (image, new Point(0, 0), "invisibleCursor");
			frame.setCursor(transparentCursor);
		}
		lockMouseLast = lockMouse;
	}
	private boolean lockMouseLast = lockMouse;

}

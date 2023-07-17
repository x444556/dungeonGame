package net.ddns.x444556.dungeonServer;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.StringUtil;

public class Server {
    
    private int port;
    
    private long nextChannedID = 0;
    
    public ConcurrentLinkedQueue<PlayerInfo> Players = new ConcurrentLinkedQueue<PlayerInfo>();
    public ConcurrentLinkedQueue<Action> ActionQueue = new ConcurrentLinkedQueue<Action>();
    
    public Server(int port) {
        this.port = port;
    }
    
    public void RunActionQueue() throws Exception {
    	while(true) {
    		try {
    			while(ActionQueue.peek() == null) Thread.sleep(1);
        		
        		Action action = ActionQueue.poll();
        		if(action != null) {
        			System.out.println((action.PlayerInfo == null ? "[*]" : "[" + action.PlayerInfo.Nickname + "] ") + action.Command);
        			
        			ByteBuf buffer = action.PlayerInfo.ctx.alloc().buffer(2048);
            		
            		String[] splits = StringUtil.split(StringUtil.split(action.Command, '\n')[0], ' ');
                	//for(String str : splits) System.out.println(str);
                	
                	if(action.Command.startsWith("JOIN")) {
                		PlayerInfo[] players = Players.toArray(PlayerInfo.EmptyArray);
                		for(int i=0; i<players.length; i++) {
                			buffer.writeBytes(("JOIN " + action.PlayerInfo.Nickname).getBytes());
                			buffer.writeByte('\n');
            				players[i].ctx.writeAndFlush(buffer.retain()).sync();
            				buffer.clear();
            				
            				buffer.writeBytes(("JOIN " + players[i].Nickname).getBytes());
                			buffer.writeByte('\n');
            				action.PlayerInfo.ctx.writeAndFlush(buffer.retain()).sync();
            				buffer.clear();
            				
            				buffer.writeBytes(("MOV " + players[i].Nickname + " " + players[i].GetPos()).getBytes());
                			buffer.writeByte('\n');
            				action.PlayerInfo.ctx.writeAndFlush(buffer.retain()).sync();
            				buffer.clear();

            				buffer.writeBytes(("ROT " + players[i].Nickname + " " + players[i].GetRot()).getBytes());
                			buffer.writeByte('\n');
            				action.PlayerInfo.ctx.writeAndFlush(buffer.retain()).sync();
            				buffer.clear();

            				buffer.writeBytes(("WALK " + players[i].Nickname + " " + (players[i].isWalking ? "true" : "false")).getBytes());
                			buffer.writeByte('\n');
            				action.PlayerInfo.ctx.writeAndFlush(buffer.retain()).sync();
            				buffer.clear();

            				buffer.writeBytes(("SNEAK " + players[i].Nickname + " " + (players[i].isSneaking ? "true" : "false")).getBytes());
                			buffer.writeByte('\n');
            				action.PlayerInfo.ctx.writeAndFlush(buffer.retain()).sync();
            				buffer.clear();
            			}
            			Players.add(action.PlayerInfo);
                	}
                	else if(splits[0].equalsIgnoreCase("REMOVE")) {
                		//System.out.println("Remove player " + action.PlayerInfo.Nickname);
                		Players.remove(action.PlayerInfo);
                	}
                	else if(splits[0].equalsIgnoreCase("MOV")) {
                		action.PlayerInfo.X = Double.parseDouble(splits[1]);
                		action.PlayerInfo.Y = Double.parseDouble(splits[2]);
                		action.PlayerInfo.Z = Double.parseDouble(splits[3]);
                		
                		PlayerInfo[] players = Players.toArray(PlayerInfo.EmptyArray);
                		for(int i=0; i<players.length; i++) {
                			if(players[i] != action.PlayerInfo) {
                				buffer.writeBytes(("MOV " + action.PlayerInfo.Nickname + " " + action.PlayerInfo.GetPos()).getBytes());
                    			buffer.writeByte('\n');
                				players[i].ctx.writeAndFlush(buffer.retain()).sync();
                				buffer.clear();
                			}
            			}
                	}
                	else if(splits[0].equalsIgnoreCase("ROT")) {
                		action.PlayerInfo.Xrot = Double.parseDouble(splits[1]);
                		action.PlayerInfo.Yrot = Double.parseDouble(splits[2]);
                		action.PlayerInfo.Zrot = Double.parseDouble(splits[3]);
                		
                		PlayerInfo[] players = Players.toArray(PlayerInfo.EmptyArray);
                		for(int i=0; i<players.length; i++) {
                			if(players[i] != action.PlayerInfo) {
                				buffer.writeBytes(("ROT " + action.PlayerInfo.Nickname + " " + action.PlayerInfo.GetRot()).getBytes());
                    			buffer.writeByte('\n');
                				players[i].ctx.writeAndFlush(buffer.retain()).sync();
                				buffer.clear();
                			}
            			}
                	}
                	else if(splits[0].equalsIgnoreCase("WALK")) {
                		action.PlayerInfo.isWalking = (splits[1].equalsIgnoreCase("true") ? true : false);

                		PlayerInfo[] players = Players.toArray(PlayerInfo.EmptyArray);
                		for(int i=0; i<players.length; i++) {
                			if(players[i] != action.PlayerInfo) {
                				buffer.writeBytes(("WALK " + action.PlayerInfo.Nickname + " " + splits[1]).getBytes());
                    			buffer.writeByte('\n');
                				players[i].ctx.writeAndFlush(buffer.retain()).sync();
                				buffer.clear();
                			}
            			}
                	}
                	else if(splits[0].equalsIgnoreCase("SNEAK")) {
                		action.PlayerInfo.isSneaking = (splits[1].equalsIgnoreCase("true") ? true : false);

                		PlayerInfo[] players = Players.toArray(PlayerInfo.EmptyArray);
                		for(int i=0; i<players.length; i++) {
                			if(players[i] != action.PlayerInfo) {
                				buffer.writeBytes(("SNEAK " + action.PlayerInfo.Nickname + " " + splits[1]).getBytes());
                    			buffer.writeByte('\n');
                				players[i].ctx.writeAndFlush(buffer.retain()).sync();
                				buffer.clear();
                			}
            			}
                	}
                	else {
                		buffer.writeBytes("ELSE2".getBytes());
            			buffer.writeByte(0x00);
                		action.PlayerInfo.ctx.writeAndFlush(buffer.retain());
                        buffer.clear();
                        action.PlayerInfo.ctx.close();
                	}
            		
                	buffer.release();
        		}
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public void run() throws Exception {
    	Server s = this;
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					RunActionQueue();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	}).start();
    	
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 ServerHandler handler = new ServerHandler();
                	 handler.Server = s;
                	 handler.ChannelID = nextChannedID;
                	 nextChannedID++;
                     ch.pipeline().addLast(handler);
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
    
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
    
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
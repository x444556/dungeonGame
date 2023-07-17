package net.ddns.x444556.dungeon;

import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	
	public ConcurrentLinkedQueue<Action> ActionQueue = new ConcurrentLinkedQueue<Action>();
	public ChannelHandlerContext ctx;
	public ByteBuf Buffer;

	public Client() {
		// TODO Auto-generated constructor stub
	}
	
	public void Send(String command) {
		//System.out.print("[SEND] \"" + command + "\" ... ");
		try {
			Buffer.writeBytes((command).getBytes());
			Buffer.writeByte('\n');
			ctx.writeAndFlush(Buffer.retain(2)).sync();
			Buffer.clear();
			//System.out.println("OK");
		}
		catch(Exception ex) {
			//System.out.println("ERROR");
			ex.printStackTrace();
		}
	}

	public void run(String host, int port) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        Client client = this;
        
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                	ClientHandler c = new ClientHandler();
                	c.client = client;
                    ch.pipeline().addLast(c);
                }
            });
            
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						f.channel().closeFuture().sync();
			            workerGroup.shutdownGracefully();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
            }).start();
        } catch(Exception ex) {
            workerGroup.shutdownGracefully();
            ex.printStackTrace();
        }
	}
}

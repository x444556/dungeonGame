package net.ddns.x444556.dungeon;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.StringUtil;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelInboundHandlerAdapter { // (1)
	private ByteBuf buf;
	private ByteBuf out;
	
	public long ChannelID = 0;
	
	public Client client;
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("handlerAdded");
        
    	try {
    		client.ctx = ctx;
        	client.Buffer = ctx.alloc().buffer(2048);
        	
            buf = ctx.alloc().buffer(2048); // (1)
            out = ctx.alloc().buffer(2048);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    	System.out.println("handlerRemoved");
    	
        try {
        	buf.release(); // (1)
            buf = null;
            out.release();
            out = null;
            
            client.ActionQueue.add(new Action(null, "REMOVE"));
            
            client.Buffer.release();
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
        	ByteBuf m = (ByteBuf) msg;
        	byte b = m.readByte();
        	while(b != '\n' && m.isReadable()) {
        		buf.writeByte(b);
        		b = m.readByte();
        	}
            
        	byte[] sb = new byte[buf.writerIndex() - buf.readerIndex()];
        	buf.readBytes(sb);
        	buf.clear();
        	String s = new String(sb).trim();
        	//System.out.println(s);
        	
        	String[] splits = StringUtil.split(StringUtil.split(s, '\n')[0], ' ');
        	//for(String str : splits) System.out.println(str);
        	
        	if(s.startsWith("CLOSE")) {
        		out.writeBytes("BYE".getBytes());
                ctx.writeAndFlush(out.retain());
                out.clear();
                ctx.close();
        	}
        	else {
        		client.ActionQueue.add(new Action(null, s));
        	}
            
            buf.clear();
            buf.writeBytes(m);
            m.release();
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
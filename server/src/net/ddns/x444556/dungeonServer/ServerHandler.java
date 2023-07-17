package net.ddns.x444556.dungeonServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.StringUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter { // (1)
	private ByteBuf buf;
	private ByteBuf out;
	
	public long ChannelID = 0;
	
	public boolean isLoggedIn = false;
	public PlayerInfo info;
	
	public Server Server;
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(2048); // (1)
        out = ctx.alloc().buffer(2048);
    }
    
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release(); // (1)
        buf = null;
        out.release();
        out = null;
        
        if(info != null) Server.ActionQueue.add(new Action(info, "REMOVE"));
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
			out.writeByte(0x00);
            ctx.writeAndFlush(out.retain());
            out.clear();
            ctx.close();
    	}
    	else if(s.startsWith("JOIN")) {
    		// JOIN <Name>
    		if(splits.length == 2) {
    			info = new PlayerInfo();
    			info.Nickname = splits[1];
    			info.ctx = ctx;
    			Server.ActionQueue.add(new Action(info, s));
    		}
    		else {
    			out.writeBytes(("ERR ARGS CNT " + (splits.length - 2)).getBytes());
    			out.writeByte(0x00);
                ctx.writeAndFlush(out.retain());
                out.clear();
                ctx.close();
    		}
    	}
    	else {
    		Server.ActionQueue.add(new Action(info, s));
    	}

        buf.clear();
        buf.writeBytes(m);
        m.release();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //cause.printStackTrace();
        ctx.close();
    }
}
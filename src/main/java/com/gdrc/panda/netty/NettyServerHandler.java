package com.gdrc.panda.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.CorePeer;
import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.command.Command;
import com.gdrc.panda.dispatcher.InDispatcher;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

@Sharable
public  class NettyServerHandler extends SimpleChannelInboundHandler<Command>{

	protected Map<String, SocketChannel> channel = new ConcurrentHashMap<>();;//follower channel
	
	
	InDispatcher dispatcher ;
	
	final static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
	
	
	public NettyServerHandler(InDispatcher dispatcher){
		this.dispatcher = dispatcher;
	
		
	}
	
	/**
	 * send to one client
	 * */
	public void send(Command cmd, String client) throws PandaException {

		
	 
		channel.forEach((x, y) -> {

			logger.info("netty server x :{},client:{}",x,client);
			if (x.equals(client)) {
				y.writeAndFlush(cmd);
				return;
			}

		});

	}
	
	
	
	

	public void send(Command cmd) throws PandaException {

	  
	  
		channel.forEach((x, y) -> {
			
			y.writeAndFlush(cmd);

		});

	}
	
	
	private String getRomateAddress(ChannelHandlerContext ctx){
		return ctx.channel().remoteAddress().toString();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String channelId = getRomateAddress(ctx);
		
		logger.info("{} recieve client {} connect   ",ctx.channel().localAddress(),ctx.channel().remoteAddress());
		
		channel.put(channelId, (SocketChannel) ctx.channel());
		
		
		
		
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable t){
	  
	  
	  
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		String uuid = getRomateAddress(ctx);
		
		logger.info("channel unregister:{}",uuid);
		
		channel.remove(uuid);
		
		
		
		
	}





	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
		
		
		if(Command.Type.PEER_CONNECT == cmd.getType()){
			
			logger.info(" receive PEER_CONNECT from {}",ctx.channel().remoteAddress());
			
		}else{
			//logger.info(" receive Command.{} from {}",cmd.getType(),ctx.channel().remoteAddress());
		
			this.dispatcher.dispatch(cmd);
		}
		
	}
	
	
	
	
}

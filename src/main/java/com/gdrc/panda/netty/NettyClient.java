package com.gdrc.panda.netty;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stoppable;
import com.gdrc.panda.command.Command;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient implements Stoppable {

	final  Logger logger = LoggerFactory.getLogger(NettyClient.class);

	
	
	protected NettyClientHandler handler;

	

	/**
	 * store all the open channel
	 */
	protected List<Channel> channelMap;

	protected List<EventLoopGroup> eventGroups;

	
	MemberPeer peer ;
	
	

	public NettyClient(MemberPeer member) {

		

		this.handler = new NettyClientHandler(member);
		peer = member;
		
		this.channelMap = new ArrayList();
		this.eventGroups = new ArrayList();
		

	}

	protected void startup() throws PandaException {

		// start a netty client,init connections of all the peers.
		
		startNettyClient(peer.getHostIp(),peer.getSvrPort());

	}

	public void send(Command cmd) throws PandaException{
		
	  
		
		channelMap.forEach(y -> {
			y.writeAndFlush(cmd);
			
		});
	}
	
	

	private void startNettyClient(String hostId, int port) throws PandaException {

		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.group(eventLoopGroup);
		
		bootstrap.remoteAddress(hostId, port);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
				socketChannel.pipeline().addLast(new ObjectEncoder());
				//http://blog.csdn.net/jiangtao_st/article/details/38118085
				socketChannel.pipeline().addLast(new ObjectDecoder(1024,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));  
				socketChannel.pipeline().addLast(handler);
			}
		});
		ChannelFuture future = null;
		try {
			future = bootstrap.connect(hostId, port).sync();

			if (future.isSuccess()) {

				channelMap.add(future.channel());
				
				
				
				//logger.info("connect to {}:{} client:{}",future.channel().remoteAddress());
				eventGroups.add(eventLoopGroup);
			}else{
				
				logger.info("connect to server[{}:{}] failure", hostId, port);
			}

			// Wait until the connection is closed.
			// future.channel().closeFuture().syncUninterruptibly();

		} catch (Exception e) {

			eventLoopGroup.shutdownGracefully();

			throw new PandaException(e);
		}
	}

	public boolean stop() throws PandaException {

		channelMap.forEach(y -> {
			y.close();

		});

		eventGroups.forEach(l -> l.shutdownGracefully());

		channelMap.forEach(l -> {

			logger.info("shutdown client connection: [?][?] success", l.remoteAddress().toString());

		});

		channelMap.clear();
		return true;
	}

	@Override
	public void start() throws PandaException {

		
		startup();

	}

	


}

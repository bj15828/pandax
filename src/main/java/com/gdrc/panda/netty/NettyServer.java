package com.gdrc.panda.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.PandaException;
import com.gdrc.panda.Stoppable;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public  class NettyServer implements Stoppable{

	final static Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
	protected int port;
	
	

	NettyServerHandler handler;

	ChannelFuture f;

	EventLoopGroup boss;
	EventLoopGroup worker;
	
	
	


	public NettyServer(int port,NettyServerHandler handler) {

		this.port = port;
		this.handler = handler;
		
		
	}

	protected void startup() throws PandaException {
		
		if( null != worker && !worker.isShutdown()){//if Channel is open ,then return ;
			
			
			return ;
			
		}
		

		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup(10);
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 100);
		// no delay
		bootstrap.option(ChannelOption.TCP_NODELAY, true);

		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				ChannelPipeline p = socketChannel.pipeline();
				//添加对象解码器 负责对序列化POJO对象进行解码 设置对象序列化最大长度为1M 防止内存溢出
                //设置线程安全的WeakReferenceMap对类加载器进行缓存 支持多线程并发访问  防止内存溢出 
				//http://blog.csdn.net/jiangtao_st/article/details/38118085
                p.addLast(new ObjectDecoder(1024*1024,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
                p.addLast(new ObjectEncoder());
				p.addLast(handler);
			}
		});
		ChannelFuture f;
		try {
			f = bootstrap.bind(port).sync();

			if (f.isSuccess()) {
			
				logger.info("server [{}] start-------",this.port);
			}

		} catch (InterruptedException e) {

			e.printStackTrace();

			throw new PandaException(e);
		}

	}
	
	
	public boolean stop()  throws PandaException{
		
		
		f.channel().closeFuture();
		worker.shutdownGracefully();
		boss.shutdownGracefully();
		
		return true;
		
		
		
		
		
	}
	
	

	@Override
	public void start() throws PandaException {
		startup();
		
	}

	
	
	
	

}

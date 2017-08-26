package com.gdrc.panda.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.MemberPeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.command.Command;
import com.gdrc.panda.command.PeerConnect;
import com.gdrc.panda.util.UUID;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<Command> {



  protected Map<String, SocketChannel> channel = new ConcurrentHashMap<>();;// follower channel

  MemberPeer peer;

  public NettyClientHandler(MemberPeer peer) {

    this.peer = peer;

  }


  final static Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

  public void send(Command cmd) throws PandaException {



    channel.forEach((x, y) -> {

      y.writeAndFlush(cmd);

    });

  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    String uuid = ctx.channel().id().asLongText();



    channel.put(uuid, (SocketChannel) ctx.channel());

    // send peer connection cmd

    logger.info("{} {} connect to server {} success!", this.peer.getPeerName(),
        ctx.channel().localAddress(), ctx.channel().remoteAddress());


    ctx.channel().writeAndFlush(new PeerConnect(UUID.newUUID()).setPn(peer.getPeerName()));


  }



  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
    
    
    
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Command msg) throws Exception {


  }



}

import akka.actor.*;
import akka.pattern.AskableActorRef;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.Bootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.ChannelPipeline;

import java.net.InetAddress;
//import akka.actor.AbstractActor;
import java.util.ArrayList;

/**
 * Discards any incoming data.
 */
public class UdpServer {

    private int port;
    AskableActorRef  serverActor = null;

    public UdpServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3 * 1000)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                @Override
                public void initChannel(final NioDatagramChannel ch) throws Exception {

                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new IncommingPacketHandler("Actor"));
                }
            });

            // Bind and start to accept incoming connections.
            Integer pPort = port;
            InetAddress address  = InetAddress.getLocalHost();
            System.out.printf("waiting for message %s %s",String.format(pPort.toString()),String.format( address.toString()));
            b.bind(address,port).sync().channel().closeFuture().await();
            //ChannelFuture future = b.connect(server.getHost(), server.getPort()).sync();
        } finally {
        	System.out.println("\nIn Server Finally");
        }
    }
    /*
    public void start() throws InterruptedException
    {
       EventLoopGroup group = new NioEventLoopGroup();
       try
       {
          ArrayList<ChannelFuture> channelFutures = null;
          for (MyServerOject server : servers)
          {
             Bootstrap b = new Bootstrap();
             b.group(group)
                      .channel(NioSocketChannel.class)
                      .option(ChannelOption.TCP_NODELAY, true)
                      .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception
                         {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IncommingPacketHandler("Actor"));
                         }
                      });

             channelFutures = new ArrayList<>();
             ChannelFuture future = b.connect(server.getHost(), server.getPort()).sync();
             channelFutures.add(future);
          }

          for (ChannelFuture channelFuture : channelFutures)
          {
             channelFuture.channel().closeFuture().sync();
          }
       }
       finally
       {
          group.shutdownGracefully();
       }
    }
    */
    public static void main(String[] args) throws Exception {
        int port =9956;
        new UdpServer(port).run();
    }
}

/**
 * Created by mahesh.govind on 2/5/16.
 */
import java.io.IOException;
import java.net.*;

import akka.pattern.AskableActorRef;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UdpClient {
    private int port;
    AskableActorRef  serverActor = null;

    public UdpClient(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        final Bootstrap b = new Bootstrap();
        try {
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                @Override
                public void initChannel(final NioDatagramChannel ch) throws Exception {

                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new IncommingPacketHandlerClient("Actor"));
                }
            });

            // Bind and start to accept incoming connections.
            Integer pPort = port;
            InetAddress address  = InetAddress.getLocalHost();
            System.out.printf("waiting for message %s %s",String.format(pPort.toString()),String.format( address.toString()));
            b.bind(address,port).sync().channel().closeFuture().await();
            
        } finally {
        	System.out.println("\nIn Client Finally");
        	b.bind(port).channel().disconnect();
        }
    }
    
    public static void main(String args[]){

        byte[] buf = new byte[256];
        DatagramSocket socket = null;
        System.out.println("dddd");
        
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            System.out.println(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String msgStr = "{" + 
        		"  messageType: client_location," + 
        		"  clientId: 0," + 
        		"  longitude: 22.1234," + 
        		"  latitude: 17.5544" + 
        		"}";
        buf= msgStr.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 9956);
        try {
            socket.send(packet);
            socket.receive(packet);
            System.out.println(new String(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
        	int port =9955;
			new UdpClient(port).run();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} */                  
     
    }
}

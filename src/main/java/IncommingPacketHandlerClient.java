/**
 * Created by mahesh.govind on 2/5/16.
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import akka.actor.ActorRef;
import java.net.SocketException;
import java.net.UnknownHostException;

import akka.pattern.AskableActorRef;


public class IncommingPacketHandlerClient extends  SimpleChannelInboundHandler<DatagramPacket> {

    IncommingPacketHandlerClient( String  parserServer){

    }

    protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        final InetAddress srcAddr = packet.sender().getAddress();
        final ByteBuf buf = packet.content();
        final int rcvPktLength = buf.readableBytes();
        final byte[] rcvPktBuf = new byte[rcvPktLength];
        buf.readBytes(rcvPktBuf);
        //String msg = new String(packet., packet.getOffset(), packet.getLength());
        String lastStr = new String(rcvPktBuf);
        System.out.println(new String(rcvPktBuf));
        System.out.println("Inside incomming packet handler");
        
        channelHandlerContext.disconnect();
        //ChannelPromise cp;
        channelHandlerContext.close();
        //rcvPktProcessing(lastStr, rcvPktLength, srcAddr);
    }
    
    protected void rcvPktProcessing(String rcvPktBuf, int rcvPktLength, final InetAddress srcAddr){
    	byte[] buf = new byte[256];
    	java.net.DatagramSocket socket = null;
        System.out.println("dddd");
        try {
            socket = new java.net.DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String msgStart = "[";
        String msgStr1 = "{" + 
        		"  messageType: client_location," + 
        		"  clientId: 0," + 
        		"  longitude: 22.1234," + 
        		"  latitude: 17.5544" + 
        		"},";
        
        String msgStr2 = "{" + 
        		"  messageType: client_location," + 
        		"  clientId: 0," + 
        		"  longitude: 22.1234," + 
        		"  latitude: 17.5544" + 
        		"},";
        
        String msgStr3 = "{" + 
        		"  messageType: client_location," + 
        		"  clientId: 0," + 
        		"  longitude: 22.1234," + 
        		"  latitude: 17.5544" + 
        		"}";
        String msgEnd = "]";
        String msgStr = msgStart + msgStr1 + msgStr2 + msgStr3 + msgEnd;
        buf= msgStr.getBytes();
        java.net.DatagramPacket packet = new java.net.DatagramPacket(buf, buf.length, srcAddr, 9956);
        try {
            socket.send(packet);
            System.out.println(new String(buf));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, DatagramPacket arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}
}

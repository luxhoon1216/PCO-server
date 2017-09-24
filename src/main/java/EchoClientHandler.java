/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */


import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage;
    private ByteBuf sendMessage;

    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
        firstMessage = Unpooled.buffer(EchoClient.SIZE);
        JSONObject json=new JSONObject();
        json.put("messageType", "client_location");
        json.put("clientId", 0);
        json.put("longitude",44.12345);
        json.put("latitude",55.55443);

        firstMessage.writeBytes(json.toString().getBytes());
        /*for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) i);
        }*/
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	sendMessage = Unpooled.buffer(EchoClient.SIZE);
    	ByteBuf in = (ByteBuf)msg;
    	
		try {
	        JSONParser parser = new JSONParser();
	        JSONArray arrayObj;
			
			arrayObj = (JSONArray) parser.parse( in.toString(CharsetUtil.UTF_8) );
			//JSONArray jarray = (JSONArray) arrayObj.get("");
	        //float longitude = Float.parseFloat(arrayObj.get("longitude").toString());
	        //float latitude = Float.parseFloat(arrayObj.get("latitude").toString());
	        //int clientId = Integer.parseInt(arrayObj.get("clientId").toString());
	        //System.out.println("clientId : " + clientId + " logitude : " + longitude + ",  latitude : " + latitude );
		    Iterator<Object> iterator = arrayObj.iterator();
	        while (iterator.hasNext()) {
	        	JSONObject jobj = (JSONObject)iterator.next();
		        float longitude = Float.parseFloat(jobj.get("longitude").toString());
		        float latitude = Float.parseFloat(jobj.get("latitude").toString());
		        int clientId = Integer.parseInt(jobj.get("clientId").toString());
		        int objectId = Integer.parseInt(jobj.get("objectId").toString());
		        System.out.println("objectId : " + objectId + " clientId : " + clientId + " logitude : " + longitude + ",  latitude : " + latitude );
	        }	    	
	        
	        
	        JSONObject json=new JSONObject();
	        json.put("messageType", "client_location");
	        json.put("clientId", 0);
	        json.put("longitude",44.12345);
	        json.put("latitude",55.55443);
	        sendMessage.writeBytes(json.toString().getBytes());
	        
	    	//System.out.println( in.toString(CharsetUtil.UTF_8) + " Client");
	    	ctx.executor().schedule(() -> ctx.writeAndFlush(sendMessage), 3, TimeUnit.SECONDS);
	        
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}

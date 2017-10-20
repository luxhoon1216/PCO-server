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


import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	private ByteBuf firstMessage = null;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	firstMessage = Unpooled.buffer(EchoClient.SIZE);
    	ByteBuf in = (ByteBuf)msg;
    	//System.out.println( in.toString(CharsetUtil.UTF_8) + " Sever");

    	try {
	        JSONParser parser = new JSONParser();
	        JSONObject arrayObj = (JSONObject) parser.parse( in.toString(CharsetUtil.UTF_8) );
	        float longitude = Float.parseFloat(arrayObj.get("longitude").toString());
	        float latitude = Float.parseFloat(arrayObj.get("latitude").toString());
	        int clientId = Integer.parseInt(arrayObj.get("clientId").toString());
	        System.out.println("clientId : " + clientId + " logitude : " + longitude + ",  latitude : " + latitude );
	        
	        JSONArray root=new JSONArray();
	        JSONObject json1=new JSONObject();
	        json1.put("messageType", "object_location");
	        json1.put("clientId", clientId);
	        json1.put("objectId", 1);
	        json1.put("longitude", longitude + 10.12345);
	        json1.put("latitude", latitude + 10.12345);

	        JSONObject json2=new JSONObject();
	        json2.put("messageType", "object_location");
	        json2.put("clientId", clientId);
	        json2.put("objectId", 2);
	        json2.put("longitude",longitude + 88.1234);
	        json2.put("latitude", latitude + 66.5544);

	        JSONObject json3=new JSONObject();
	        json3.put("messageType", "object_location");
	        json3.put("clientId", clientId);
	        json3.put("objectId", 3);
	        json3.put("longitude",longitude + 44.12345);
	        json3.put("latitude", latitude + 55.55443);
	        
	        root.add(json1);
	        root.add(json2);
	        root.add(json3);
	        
	        firstMessage.writeBytes(root.toString().getBytes());
	        //buf= msgStr.getBytes();    	
	        //ctx.write(firstMessage);
	    	ctx.executor().schedule(() -> ctx.writeAndFlush(firstMessage), 3, TimeUnit.SECONDS);
	        
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

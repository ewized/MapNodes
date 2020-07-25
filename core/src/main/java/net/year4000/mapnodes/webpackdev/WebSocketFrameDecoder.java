package net.year4000.mapnodes.webpackdev;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.*;

import java.util.List;

public class WebSocketFrameDecoder extends MessageToMessageDecoder<WebSocketFrame> {
  private static final Gson gson = new Gson();

  @Override
  protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) {
    // Close the socket frame
    if (msg instanceof CloseWebSocketFrame) {
      ctx.close();
    }
    // Check the status of the connection
    else if (msg instanceof PingWebSocketFrame) {
      PingWebSocketFrame ping = (PingWebSocketFrame) msg;
      // todo if we dont get a ping in x time close the connection
      //System.out.println(ping);
      ctx.channel().writeAndFlush(new PongWebSocketFrame());
    }
    // Convert the json results to the socket packet
    else if (msg instanceof TextWebSocketFrame) {
      String textFrame = ((TextWebSocketFrame) msg).text();
      out.add(gson.fromJson(textFrame, SocketPacket.class));
    }
  }
}

package net.year4000.mapnodes.webpackdev;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.ReferenceCountUtil;

import java.net.URI;
import java.util.List;

public class WebSocketHandshakeHandler extends MessageToMessageDecoder<FullHttpResponse> {
  private static final int MAX_FRAME_PAYLOAD_LENGTH = 1280000;
  private static final WebSocketVersion WEB_SOCKET_VERSION = WebSocketVersion.V08;
  private final WebSocketClientHandshaker handshaker;

  protected WebSocketHandshakeHandler(URI websocket) {
    System.out.println("websocket: " + websocket);
    System.out.println("headers: " + EmptyHttpHeaders.INSTANCE);
    this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(
      websocket,
      WEB_SOCKET_VERSION,
      "null",
      true,
      EmptyHttpHeaders.INSTANCE,
      MAX_FRAME_PAYLOAD_LENGTH
    );
    System.out.println("handshaker: " + handshaker);

  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) {
    System.out.println("channelActive " + ctx);
    System.out.println("channelActive " + ctx.channel());
    System.out.println("handshaker " + handshaker);
    System.out.println("HttpHeaderValues.WEBSOCKET " + HttpHeaderValues.WEBSOCKET);
    System.out.println("HttpHeaderValues.UPGRADE " + HttpHeaderValues.UPGRADE);

    handshaker.handshake(ctx.channel());
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, FullHttpResponse msg, List<Object> out) {
    System.out.println("decode " + ctx);

    if (!handshaker.isHandshakeComplete()) {
      // web socket client connected
      handshaker.finishHandshake(ctx.channel(), msg);
    } else {
      out.add(ReferenceCountUtil.retain(msg));
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
  }
}

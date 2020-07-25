package net.year4000.mapnodes.webpackdev;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import net.year4000.utilities.Callback;
import net.year4000.utilities.Conditions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class WebpackDevInitializer extends ChannelInitializer<SocketChannel> {
  private static final int MAX_CONTENT_LENGTH = 512 * 1024;
  private URI uri;
  Callback<String> hotUpdateCallback;
  Callback<String> fullUpdateCallback;

  public WebpackDevInitializer(URI uri) {
    this.uri = Conditions.nonNull(uri, "uri must exist");
  }

  public WebpackDevInitializer(String address, Callback<String> hotUpdateCallback, Callback<String> fullUpdateCallback) throws URISyntaxException {
    this(new URI(Conditions.nonNull("http://" + address, "address must exist")));
    this.hotUpdateCallback = hotUpdateCallback;
    this.fullUpdateCallback = fullUpdateCallback;
  }

  @Override
  protected void initChannel(SocketChannel channel) throws Exception {
    channel.pipeline()
      .addLast("http-codec", new HttpClientCodec())
      .addLast("aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH))
      .addLast("handshake", new WebSocketHandshakeHandler(this.uri))
      .addLast("packet-decoder", new WebSocketFrameDecoder())
      .addLast("packet-handler", new ActionHandler(this, this.uri, "mapnodes.bundle.js"))
      ;
  }
}

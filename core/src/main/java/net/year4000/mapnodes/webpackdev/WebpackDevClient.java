package net.year4000.mapnodes.webpackdev;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.year4000.utilities.Callback;
import net.year4000.utilities.Conditions;

public class WebpackDevClient implements AutoCloseable {
  private final EventLoopGroup group = new NioEventLoopGroup();
  private final String host;
  private final int port;
  private ChannelFuture channel;

  public WebpackDevClient(String host, int port) {
    this.host = Conditions.nonNull(host, "host must exist");
    this.port = port;
  }

  public ChannelFuture open(Callback<String> hotUpdateCallback, Callback<String> fullUpdateCallback) throws Exception {
    Bootstrap bootstrap = new Bootstrap();

    bootstrap.group(group)
      .channel(NioSocketChannel.class)
      .handler(new WebpackDevInitializer(host + ":" + port, hotUpdateCallback, fullUpdateCallback));

    this.channel = bootstrap.connect(host, port);
    return this.channel;
  }

  @Override
  public void close() {
    if (this.channel != null) {
      this.channel.channel().close();
    }
  }
}

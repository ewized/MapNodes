package net.year4000.mapnodes.webpackdev;

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.URLBuilder;
import net.year4000.utilities.net.JsonHttpFetcher;
import net.year4000.utilities.net.RawHttpFetcher;
import net.year4000.utilities.value.Value;

import java.net.URI;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class ActionHandler extends SimpleChannelInboundHandler<SocketPacket> {
  private static final JsonHttpFetcher jsonFetcher = JsonHttpFetcher.builder().maxTries(1).build();
  private static final RawHttpFetcher httpFetcher = RawHttpFetcher.builder().maxTries(1).build();
  private final Map<String, Action> actions = ImmutableMap.<String, Action>builder()
    .put("hot", this::onHot)
    .put("liveReload", this::onLiveReload)
    .put("invalid", this::onInvalid)
    .put("still-ok", this::onStillOk)
    .put("errors", this::onErrors)
    .put("warnings", this::onWarnings)
    .put("hash", this::onHash)
    .put("ok", this::onOk)
    .build();
  private final Stack<String> hashes = new Stack<>();
  private final WebpackDevInitializer initializer;
  private final String address;
  private final String module;
  private boolean hot;
  private boolean liveReload;
  private boolean invalided;

  public ActionHandler(WebpackDevInitializer initializer, URI uri, String module) {
    this.initializer = Conditions.nonNull(initializer, "initializer must exist");
    this.address = Conditions.nonNull(uri, "address must exist").toASCIIString();
    this.module = Conditions.nonNullOrEmpty(module, "module must exist");
  }

  private void onHot(Value<Object> data) {
    System.out.println("hot reload is enabled");
    this.hot = true;
  }

  private void onLiveReload(Value<Object> data) {
    System.out.println("liveReload is enabled");
    this.liveReload = true;
  }

  private void onInvalid(Value<Object> data) {
    System.out.println("invalided");
    this.invalided = true;
  }

  private void onStillOk(Value<Object> data) {
    System.out.println("still-ok nope we are good no update needed");
    this.invalided = false;
  }

  private void onErrors(Value<Object> data) {
    // pop the hash we got from this update
    this.hashes.pop();
  }

  private void onWarnings(Value<Object> data) {
    System.out.println("warnings");
  }

  private void onHash(Value<Object> data) {
    System.out.println("new hash: " + data);
    this.hashes.push((String) data.getOrThrow("Hash was null"));
  }

  private void onOk(Value<Object> data) throws Exception {
    if (!this.invalided) {
      return;
    }
    String tmp = this.hashes.pop();
    String hash = this.hashes.pop();
    this.hashes.push(tmp);
    if (this.hot) {
      jsonFetcher.get(URLBuilder.builder(this.address).addPath(hash + ".hot-update.json").build(), HotUpdate.class)
        .hotChunks()
        .stream()
        .map((chunk) -> URLBuilder.builder(this.address).addPath(chunk + "." + hash + ".hot-update.js").build())
        .collect(Collectors.toList())
        .forEach((fileName) -> httpFetcher.get(fileName, String.class, this.initializer.hotUpdateCallback));
    } else { // full reload
      String moduleUrl = URLBuilder.builder(this.address).addPath(this.module).build();
      httpFetcher.get(moduleUrl, String.class, this.initializer.fullUpdateCallback);
    }
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, SocketPacket packet) {
    Action action = this.actions.get(packet.type());
    if (action != null) {
      ctx.channel().eventLoop().execute(() -> {
        try {
          action.accept(packet.data());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }

  /** Simple interface that defines an action */
  @FunctionalInterface
  private interface Action {
    /** accept the incomming packet data */
    void accept(Value<Object> data) throws Exception;
  }
}

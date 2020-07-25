package net.year4000.mapnodes;

import net.year4000.mapnodes.webpackdev.WebpackDevClient;

public class WebsocketTest {

  public static void main(String... args) throws Exception {
    System.out.println("web socket");
    new WebpackDevClient("localhost", 3001)
      .open((d, e) -> System.out.println(d), (d, e) -> {})
      .sync();
  }
}

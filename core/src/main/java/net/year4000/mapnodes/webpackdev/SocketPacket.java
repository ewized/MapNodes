package net.year4000.mapnodes.webpackdev;

import net.year4000.utilities.Utils;
import net.year4000.utilities.value.Value;

final class SocketPacket {
  private String type;
  private Object data;

  public String type() {
    return this.type;
  }

  public Value<Object> data() {
    return Value.of(this.data);
  }

  public String toString() {
    return Utils.toString(this);
  }
}

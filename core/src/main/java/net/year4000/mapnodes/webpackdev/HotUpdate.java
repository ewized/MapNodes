package net.year4000.mapnodes.webpackdev;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class HotUpdate {
  @SerializedName("h")
  private String hash;
  @SerializedName("c")
  private Set<String> chunks = new HashSet<>();

  /** Get the hash of the next update */
  public String hash() {
    return this.hash;
  }

  /** Get the set of chunks that needs to be updated */
  public Set<String> hotChunks() {
    return this.chunks;
  }
}

package net.year4000.mapnodes.webpackdev;

import com.google.common.io.CharStreams;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.net.AbstractHttpFetcher;
import net.year4000.utilities.net.ContentType;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ContentType("text; charset=utf8")
public class RawHttpFetcher extends AbstractHttpFetcher<String> {
  private RawHttpFetcher(int maxTries, ExecutorService executorService) {
    super(maxTries, executorService);
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  protected <T> T reader(Reader reader, Type type) {
    try {
      String targetString = CharStreams.toString(reader);
      return (T) targetString;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected <T> T reader(Reader reader, Class<T> type) {
    Conditions.nonNull(reader, "reader");
    Conditions.nonNull(type, "type");
    return reader(reader, (Type) type);
  }

  @Override
  protected String serialize(String object) {
    return object;
  }

  public static class Builder extends AbstractHttpFetcher.AbstractBuilder<RawHttpFetcher, Builder> {
    @Override
    public RawHttpFetcher build() {
      return new RawHttpFetcher(
        maxTries.getOrElse(3),
        executorService.getOrElse(Executors.newCachedThreadPool())
      );
    }
  }
}

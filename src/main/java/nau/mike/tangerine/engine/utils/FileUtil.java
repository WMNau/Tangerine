package nau.mike.tangerine.engine.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

  private FileUtil() {}

  public static String getSource(final String filePath) {
    final URL url = FileUtil.class.getResource(filePath);
    if (null == url) {
      throw new IllegalStateException("Could not find " + filePath);
    }
    try {
      return new String(Files.readAllBytes(Paths.get(url.toURI())));
    } catch (final IOException | URISyntaxException e) {
      throw new IllegalStateException("Could not read " + filePath, e);
    }
  }
}

package nau.mike.tangerine.engine.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

  public static String getSource(final String filePath) {
    try {
      final URL url = getUrl(filePath);
      return new String(Files.readAllBytes(Paths.get(url.toURI())));
    } catch (final IOException | URISyntaxException e) {
      throw new IllegalStateException("Could not read " + filePath, e);
    }
  }

  public static URL getUrl(final String filePath) {
    final URL url = FileUtil.class.getResource(filePath);
    if (null == url) {
      throw new IllegalStateException("Could not find " + filePath);
    }
    return url;
  }
}

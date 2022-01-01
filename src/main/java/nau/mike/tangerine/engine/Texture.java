package nau.mike.tangerine.engine;

import lombok.Getter;
import nau.mike.tangerine.engine.utils.FileUtil;
import org.lwjgl.system.MemoryStack;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

@SuppressWarnings("unused")
@Getter
public class Texture {

  private static final Map<String, Texture> textureMap = new HashMap<>();

  private final int id;
  private int width;
  private int height;

  public Texture(final String fileName, final String ext) {
    this(fileName, ext, true);
  }

  public Texture(final String fileName, final String ext, final boolean flipOnLoad) {
    this(String.format("/textures/%s.%s", fileName, ext), flipOnLoad);
  }

  public Texture(final String directory, final String fileName, final String ext) {
    this(directory, fileName, ext, true);
  }

  public Texture(
      final String directory, final String fileName, final String ext, final boolean flipOnLoad) {
    this(
        String.format(
            "/textures/%s%s.%s", ("".equals(directory) ? "" : directory + "/"), fileName, ext),
        flipOnLoad);
  }

  public Texture(final String filePath) {
    this(filePath, true);
  }

  public Texture(final String filePath, final boolean flipOnLoad) {
    if (textureMap.containsKey(filePath.toUpperCase())) {
      final Texture texture = textureMap.get(filePath.toUpperCase());
      this.id = texture.id;
      this.width = texture.width;
      this.height = texture.height;
    } else {
      this.id = createTexture(filePath, flipOnLoad);
      textureMap.put(filePath.toUpperCase(), this);
    }
  }

  private int createTexture(final String filePath, final boolean flipOnLoad) {
    final int texture = glGenTextures();
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer w = stack.mallocInt(1);
      final IntBuffer h = stack.mallocInt(1);
      final IntBuffer c = stack.mallocInt(1);
      final URL url = FileUtil.getUrl(filePath);
      stbi_set_flip_vertically_on_load(flipOnLoad);
      final ByteBuffer buffer = stbi_load(url.getFile(), w, h, c, 4);
      if (null == buffer) {
        throw new IllegalStateException("Could not find texture " + filePath);
      }

      this.width = w.get(0);
      this.height = h.get(0);
      final int nrChannel = c.get(0);

      glBindTexture(GL_TEXTURE_2D, texture);
      final int format = nrChannel == 3 || nrChannel == 4 ? GL_RGBA : GL_RGB;

      glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer);
      glGenerateMipmap(GL_TEXTURE_2D);

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      stbi_image_free(buffer);
      glBindTexture(GL_TEXTURE_2D, 0);
    }
    return texture;
  }
}

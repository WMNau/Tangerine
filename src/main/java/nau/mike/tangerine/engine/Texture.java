package nau.mike.tangerine.engine;

import nau.mike.tangerine.engine.utils.FileUtil;
import org.lwjgl.system.MemoryStack;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

@SuppressWarnings("unused")
public class Texture {

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

  private Texture(final String filePath) {
    this(filePath, true);
  }

  private Texture(final String filePath, final boolean flipOnLoad) {
    this.id = createTexture(filePath, flipOnLoad);
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

      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer);
      glGenerateMipmap(GL_TEXTURE_2D);
      stbi_image_free(buffer);
      glBindTexture(GL_TEXTURE_2D, 0);
    }
    return texture;
  }

  public int getId() {
    return id;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}

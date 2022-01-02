package nau.mike.tangerine.engine;

import lombok.Getter;
import nau.mike.tangerine.engine.utils.FileUtil;
import org.lwjgl.system.MemoryStack;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings("unused")
@Getter
public class Texture {

  private static final Map<String, Texture> textureMap = new HashMap<>();

  private final int id;
  private int width;
  private int height;
  private int nrChannel;

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

  public Texture(final int width, final int height) {
    this.width = width;
    this.height = height;
    this.id = createTexture(width, height);
  }

  public Texture(final List<String> textureFileNameList, final boolean flipOnLoad) {
    this.id = createTexture(textureFileNameList, flipOnLoad);
  }

  private int createTexture(final int width, final int height) {
    final int texture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
    setTextureParams(GL_TEXTURE_2D);
    return texture;
  }

  private int createTexture(final String filePath, final boolean flipOnLoad) {
    final int texture = glGenTextures();
    final ByteBuffer buffer = getImageBuffer(filePath, flipOnLoad);

    glBindTexture(GL_TEXTURE_2D, texture);
    final int format = nrChannel == 3 || nrChannel == 4 ? GL_RGBA : GL_RGB;

    glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer);
    setTextureParams(GL_TEXTURE_2D);

    stbi_image_free(buffer);
    glBindTexture(GL_TEXTURE_2D, 0);
    return texture;
  }

  private int createTexture(final List<String> textureFileNameList, final boolean flipOnLoad) {
    final int texture = glGenTextures();
    ByteBuffer buffer;
    int i = 0;
    for (final String fileName : textureFileNameList) {
      final String filePath = String.format("/textures/skybox/%s", fileName);
      buffer = getImageBuffer(filePath, flipOnLoad);

      glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
      glTexImage2D(
          GL_TEXTURE_CUBE_MAP_POSITIVE_X + i++,
          0,
          GL_RGBA,
          width,
          height,
          0,
          GL_RGBA,
          GL_UNSIGNED_BYTE,
          buffer);
      setTextureParams(GL_TEXTURE_CUBE_MAP);
      glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
      stbi_image_free(buffer);
    }
    return texture;
  }

  private ByteBuffer getImageBuffer(final String filePath, final boolean flipOnLoad) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final URL url = FileUtil.getUrl(filePath);
      final IntBuffer w = stack.mallocInt(1);
      final IntBuffer h = stack.mallocInt(1);
      final IntBuffer c = stack.mallocInt(1);
      stbi_set_flip_vertically_on_load(flipOnLoad);
      final ByteBuffer buffer = stbi_load(url.getFile(), w, h, c, 4);
      if (null == buffer) {
        throw new IllegalStateException("Could not find texture " + filePath);
      }

      this.width = w.get(0);
      this.height = h.get(0);
      this.nrChannel = c.get(0);
      return buffer;
    }
  }

  private void setTextureParams(final int type) {
    glGenerateMipmap(type);
    glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  }
}

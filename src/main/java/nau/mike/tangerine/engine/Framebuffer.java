package nau.mike.tangerine.engine;

import nau.mike.tangerine.engine.shaders.ScreenShader;
import nau.mike.tangerine.engine.shaders.Shader;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.opengl.GL30C.*;

public class Framebuffer {

  private static final float[] quadPositions =
      new float[] {
        -1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, -1.0f,
        1.0f, 1.0f
      };

  private static final float[] quadUvs =
      new float[] {
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
      };

  private final Mesh mesh;
  private final int fbo;
  private final int rbo;
  private final Texture texture;

  private final Shader screenShader;

  public Framebuffer() {
    this.screenShader = new ScreenShader();
    this.mesh = new Mesh(quadPositions, quadUvs);

    screenShader.start();
    screenShader.setUniform("textureSampler", 0);
    screenShader.end();

    this.fbo = glGenFramebuffers();
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);

    int width;
    int height;
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer w = stack.mallocInt(1);
      final IntBuffer h = stack.mallocInt(1);
      final long window = glfwGetCurrentContext();
      glfwGetFramebufferSize(window, w, h);
      width = w.get(0);
      height = h.get(0);
    }
    this.texture = new Texture(width, height);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getId(), 0);
    mesh.addTexture(texture);
    this.rbo = createRenderBuffers();
    glClearColor(0.01f, 0.01f, 0.01f, 1.0f);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
  }

  public void bind() {
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glEnable(GL_DEPTH_TEST);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void unbind() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glDisable(GL_DEPTH_TEST);
    glClear(GL_COLOR_BUFFER_BIT);

    screenShader.start();
    mesh.drawArrays(texture.getId());
    screenShader.end();
  }

  public void clean() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glDeleteRenderbuffers(rbo);
    glDeleteFramebuffers(fbo);
    mesh.clean();
  }

  private int createRenderBuffers() {
    final int id = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, id);
    glRenderbufferStorage(
        GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, Window.getWindowSize().x, Window.getWindowSize().y);
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, id);
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
      throw new IllegalStateException("ERROR::FRAMEBUFFER\nFramebuffer is not complete");
    }
    return id;
  }
}

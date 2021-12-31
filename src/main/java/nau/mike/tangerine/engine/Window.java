package nau.mike.tangerine.engine;

import nau.mike.tangerine.engine.input.Keyboard;
import nau.mike.tangerine.engine.input.MouseButton;
import nau.mike.tangerine.engine.input.MousePosition;
import nau.mike.tangerine.engine.input.MouseScroll;
import nau.mike.tangerine.engine.utils.ErrorHandler;
import nau.mike.tangerine.engine.utils.MathUtil;
import nau.mike.tangerine.engine.utils.TimerUtil;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings("unused")
public class Window {

  private static final boolean debug = true;

  private static Matrix4f projectionMatrix = new Matrix4f().identity();

  private long glfwWindow;
  private final GLFWErrorCallback errorCallback;
  private GLFWVidMode glfwVidMode;
  private long glfwCursor;

  private final String title;
  private int width;
  private int height;
  private final boolean fullscreen;
  private final boolean windowed;

  public Window(
      final String title,
      final int width,
      final int height,
      final boolean fullscreen,
      final boolean windowed) {
    this.title = title;
    this.width = width;
    this.height = height;
    this.fullscreen = fullscreen;
    this.windowed = windowed;
    this.errorCallback = GLFWErrorCallback.createPrint(System.err);

    Window.projectionMatrix =
        MathUtil.createProjectionMatrix(75.0f, (float) width / height, 0.1f, 1000.0f);

    glfwSetErrorCallback(errorCallback);

    if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

    setWindowHints();
    init();
  }

  public Window(final String title, final int width, final int height) {
    this(title, width, height, false, true);
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(glfwWindow);
  }

  public void close() {
    glfwSetWindowShouldClose(glfwWindow, true);
  }

  public void startFrame() {
    glfwPollEvents();
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void endFrame() {
    glfwSwapBuffers(glfwWindow);
    TimerUtil.render();
  }

  public void clean() {
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyCursor(glfwCursor);
    glfwDestroyWindow(glfwWindow);

    glfwTerminate();
    if (null != errorCallback) {
      errorCallback.free();
    }
  }

  public void setClearColor(final float r, final float g, final float b, final float a) {
    glClearColor(r, g, b, a);
  }

  public void setClearColor(final float r, final float g, final float b) {
    setClearColor(r, g, b, 1.0f);
  }

  public void setClearColor(final float rgb, final float a) {
    setClearColor(rgb, rgb, rgb, a);
  }

  public void setClearColor(final float rgb) {
    setClearColor(rgb, 1.0f);
  }

  private void init() {
    final long glfwMonitor = glfwGetPrimaryMonitor();
    glfwVidMode = glfwGetVideoMode(glfwMonitor);
    long monitor = NULL;
    if (null == glfwVidMode) throw new IllegalStateException("Failed to retrieve GPU");

    if (fullscreen && windowed) {
      width = glfwVidMode.width();
      height = glfwVidMode.height();
      setVidModeWindowHints();
    } else if (fullscreen) {
      monitor = glfwMonitor;
      setVidModeWindowHints();
    }
    glfwWindow = glfwCreateWindow(width, height, title, monitor, NULL);
    if (glfwWindow == NULL) throw new IllegalStateException("Failed to create the GLFW window");
    try (final MemoryStack stack = stackPush()) {
      final IntBuffer pWidth = stack.mallocInt(1);
      final IntBuffer pHeight = stack.mallocInt(1);

      glfwGetWindowSize(glfwWindow, pWidth, pHeight);
      glfwSetWindowPos(
          glfwWindow,
          (glfwVidMode.width() - pWidth.get(0)) / 2,
          (glfwVidMode.height() - pHeight.get(0)) / 2);
    }
    glfwCursor = glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
    glfwSetCursor(glfwWindow, glfwCursor);
    glfwMakeContextCurrent(glfwWindow);
    glfwSwapInterval(1);

    glfwShowWindow(glfwWindow);
    GL.createCapabilities();

    glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    setCallbacks();
  }

  private void setWindowHints() {
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
    glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_TRUE);

    if (debug) {
      glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
    }
  }

  private void setVidModeWindowHints() {
    glfwWindowHint(GLFW_RED_BITS, glfwVidMode.redBits());
    glfwWindowHint(GLFW_GREEN_BITS, glfwVidMode.greenBits());
    glfwWindowHint(GLFW_BLUE_BITS, glfwVidMode.blueBits());
    glfwWindowHint(GLFW_REFRESH_RATE, glfwVidMode.refreshRate());
  }

  private void setCallbacks() {
    new Keyboard(glfwWindow);
    new MouseButton(glfwWindow);
    new MousePosition(glfwWindow);
    new MouseScroll(glfwWindow);

    glfwSetErrorCallback(this::errorCallback);
    glfwSetFramebufferSizeCallback(glfwWindow, this::frameBufferSizeCallback);
  }

  private void errorCallback(final int code, final long description) {
    if (GLFW_NO_ERROR != code) {
      ErrorHandler.getError();
    }
  }

  private void frameBufferSizeCallback(final long window, final int width, final int height) {
    this.width = width;
    this.height = height;

    final float aspectRatio = (float) width / height;
    Window.projectionMatrix = MathUtil.createProjectionMatrix(75.0f, aspectRatio, 0.1f, 1000.0f);

    glViewport(0, 0, width, height);
  }

  public void debugTitle(final String message) {
    if (debug) {
      glfwSetWindowTitle(glfwWindow, title + " | " + message);
    }
  }

  public static Matrix4f getProjectionMatrix() {
    return projectionMatrix;
  }
}

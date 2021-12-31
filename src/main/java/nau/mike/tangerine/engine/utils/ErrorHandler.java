package nau.mike.tangerine.engine.utils;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("unused")
public class ErrorHandler {

  private static final Map<Integer, String> errorMap = new HashMap<>();

  static {
    String message = "ERROR::";
    errorMap.put(
        GLFW_NOT_INITIALIZED,
        message
            + "GLFW_NOT_INITIALIZED\nApplication programmer error. Initialize GLFW before calling any function that requires initialization.");
    errorMap.put(
        GLFW_NO_CURRENT_CONTEXT,
        message
            + "GLFW_NO_CURRENT_CONTEXT\nApplication programmer error. Ensure a context is current before calling functions that require a current context.");
    errorMap.put(
        GLFW_INVALID_ENUM,
        message + "GLFW_INVALID_ENUM\nApplication programmer error. Fix the offending call.");

    errorMap.put(
        GLFW_INVALID_VALUE,
        message + "GLFW_INVALID_VALUE\nApplication programmer error. Fix the offending call.");

    errorMap.put(
        GLFW_OUT_OF_MEMORY,
        message + "GLFW_OUT_OF_MEMORY\nA bug in GLFW or the underlying operating system.");

    errorMap.put(
        GLFW_API_UNAVAILABLE,
        message
            + "GLFW_API_UNAVAILABLE\nThe installed graphics driver does not support the requested API, or does not support it via the chosen context creation backend. Below are a few examples.\n\nSome pre-installed Windows graphics drivers do not support OpenGL. AMD only supports OpenGL ES via EGL, while Nvidia and Intel only support it via a WGL or GLX extension. macOS does not provide OpenGL ES at all. The Mesa EGL, OpenGL and OpenGL ES libraries do not interface with the Nvidia binary driver. Older graphics drivers do not support Vulkan.");

    errorMap.put(
        GLFW_VERSION_UNAVAILABLE,
        message
            + "GLFW_VERSION_UNAVAILABLE\nThe machine does not support your requirements. If your application is sufficiently flexible, downgrade your requirements and try again. Otherwise, inform the user that their machine does not match your requirements.");

    errorMap.put(
        GLFW_PLATFORM_ERROR,
        message
            + "GLFW_PLATFORM_ERROR\nA bug or configuration error in GLFW, the underlying operating system or its drivers, or a lack of required resources.");

    errorMap.put(
        GLFW_FORMAT_UNAVAILABLE,
        message
            + "GLFW_FORMAT_UNAVAILABLE\nIf emitted during window creation, one or more hard constraints did not match any of the available pixel formats. If your application is sufficiently flexible, downgrade your requirements and try again. Otherwise, inform the user that their machine does not match your requirements.\n\n"
            + "If emitted when querying the clipboard, ignore the error or report it to the user, as appropriate. ");

    errorMap.put(
        GLFW_NO_WINDOW_CONTEXT,
        message + "GLFW_NO_WINDOW_CONTEXT\nApplication programmer error. Fix the offending call.");
  }

  private ErrorHandler() {}

  public static void getError() {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final PointerBuffer buffer = stack.mallocPointer(1);
      final int code = glfwGetError(buffer);
      if (buffer.get() != 0 && GLFW_NO_ERROR != code) {
        throw new IllegalStateException(errorMap.getOrDefault(code, "ERROR::UNKNOWN"));
      }
    }
  }
}

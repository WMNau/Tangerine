package nau.mike.tangerine.engine.input;

import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("unused")
public class MouseButton implements GLFWMouseButtonCallbackI {

  private static long window = -1;

  public MouseButton(long window) {
    MouseButton.window = window;
    glfwSetMouseButtonCallback(window, this);
  }

  @Override
  public void invoke(long window, int button, int action, int mods) {
    MouseButton.window = window;
  }

  public static boolean pressed(int button) {
    final boolean isPressed =
        windowExists()
            && (glfwGetMouseButton(window, button) == GLFW_PRESS
                || glfwGetMouseButton(window, button) == GLFW_REPEAT);
    if (isPressed) {
      glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    } else {
      glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }
    return isPressed;
  }

  public static boolean released(int button) {
    return windowExists() && (glfwGetMouseButton(window, button) == GLFW_RELEASE);
  }

  private static boolean windowExists() {
    return MouseButton.window >= 0;
  }
}

package nau.mike.tangerine.engine.input;

import lombok.Getter;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("unused")
public class Keyboard implements GLFWKeyCallbackI {

  @Getter private static Keyboard instance;

  private static long window = -1;

  public Keyboard(long window) {
    Keyboard.window = window;
    glfwSetKeyCallback(window, this);
    Keyboard.instance = this;
  }

  @Override
  public void invoke(long window, int key, int scancode, int action, int mods) {
    Keyboard.window = window;
  }

  public static boolean pressed(int key) {
    return windowExists() && glfwGetKey(window, key) == GLFW_PRESS
        || glfwGetKey(window, key) == GLFW_REPEAT;
  }

  public static boolean released(int key) {
    return windowExists() && glfwGetKey(window, key) == GLFW_RELEASE;
  }

  private static boolean windowExists() {
    return Keyboard.window >= 0;
  }
}

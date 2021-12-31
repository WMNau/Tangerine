package nau.mike.tangerine.engine.input;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

@SuppressWarnings("unused")
public class MouseScroll implements GLFWScrollCallbackI {

  private static final Vector2f offset = new Vector2f(0.0f);

  public MouseScroll(final long window) {
    glfwSetScrollCallback(window, this);
  }

  @Override
  public void invoke(long window, double xOffset, double yOffset) {
    offset.x = (float) xOffset;
    offset.y = (float) yOffset;
  }

  public static float getX() {
    final float x = offset.x;
    if (x != 0.0f) {
      offset.x = 0.0f;
    }
    return x;
  }

  public static float getY() {
    final float y = offset.y;
    if (y != 0.0f) {
      offset.y = 0.0f;
    }
    return y;
  }

  public static Vector2f getOffset() {
    return offset;
  }
}

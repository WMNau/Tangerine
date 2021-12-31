package nau.mike.tangerine.engine.input;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

@SuppressWarnings("unused")
public class MousePosition implements GLFWCursorPosCallbackI {

  private static final Vector2f position = new Vector2f(0.0f);
  private static final Vector2f lastPosition = new Vector2f(0.0f);

  private static boolean inWindow = false;

  public MousePosition(final long window) {
    glfwSetCursorPosCallback(window, this);
    glfwSetCursorEnterCallback(window, this::cursorEnterCallback);
  }

  private void cursorEnterCallback(final long window, final boolean entered) {
    inWindow = entered;
  }

  @Override
  public void invoke(long window, double xPos, double yPos) {
    lastPosition.x = position.x;
    lastPosition.y = position.y;
    position.x = (float) xPos;
    position.y = (float) yPos;
  }

  public static float getX() {
    return position.x;
  }

  public static float getY() {
    return position.y;
  }

  public static float getDx() {
    return position.x - lastPosition.x;
  }

  public static float getDy() {
    return position.y - lastPosition.y;
  }

  public static String positionString() {
    return String.format("Mouse Position: ( %f, %f)", getX(), getY());
  }

  public static String lastPositionString() {
    return String.format("Last Mouse Position( %f, %f)", getDx(), getDy());
  }

  public static Vector2f getPosition() {
    return position;
  }

  public static boolean isInWindow() {
    return inWindow;
  }
}

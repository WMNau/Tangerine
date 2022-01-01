package nau.mike.tangerine.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import nau.mike.tangerine.engine.input.Buttons;
import nau.mike.tangerine.engine.input.MouseButton;
import nau.mike.tangerine.engine.input.MousePosition;
import nau.mike.tangerine.engine.input.MouseScroll;
import nau.mike.tangerine.engine.utils.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unused")
@AllArgsConstructor
@Data
public class Camera {

  private static final float MOUSE_POSITION_SENSITIVITY = 0.02f;
  private static final float MOUSE_SCROLL_SENSITIVITY = 0.2f;

  private Vector3f position;
  private Vector3f rotation;

  public Camera() {
    this(new Vector3f(0.0f, 0.0f, 20.0f), new Vector3f(0.0f));
  }

  public void update() {
    final float mouseX = MousePosition.getDx();
    final float mouseY = MousePosition.getDy();

    if (MouseButton.pressed(Buttons.BUTTON_MIDDLE)) {
      position.x += mouseX * MOUSE_POSITION_SENSITIVITY;
      position.y -= mouseY * MOUSE_POSITION_SENSITIVITY;
    }
    if (MouseButton.pressed(Buttons.BUTTON_RIGHT)) {
      rotation.x += mouseY * MOUSE_POSITION_SENSITIVITY;
      rotation.y += mouseX * MOUSE_POSITION_SENSITIVITY;

      final float maxPitch = 89.0f;
      if (rotation.x > maxPitch) {
        rotation.x = maxPitch;
      } else if (rotation.x < -maxPitch) {
        rotation.x = -maxPitch;
      }
    }

    position.z += MouseScroll.getY() * MOUSE_SCROLL_SENSITIVITY;
  }

  public Matrix4f getViewMatrix() {
    return MathUtil.createViewMatrix(position, this.rotation);
  }
}

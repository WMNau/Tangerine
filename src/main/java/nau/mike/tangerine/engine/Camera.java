package nau.mike.tangerine.engine;

import imgui.ImGui;
import lombok.Data;
import lombok.Getter;
import nau.mike.tangerine.engine.input.Buttons;
import nau.mike.tangerine.engine.input.MouseButton;
import nau.mike.tangerine.engine.input.MousePosition;
import nau.mike.tangerine.engine.input.MouseScroll;
import nau.mike.tangerine.engine.utils.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unused")
@Data
public class Camera {

  private static final float MOUSE_POSITION_SENSITIVITY = 0.02f;
  private static final float MOUSE_SCROLL_SENSITIVITY = 0.2f;

  @Getter private static Vector3f position;
  @Getter private static Vector3f rotation;

  public Camera() {
    this(new Vector3f(0.0f, 0.0f, 20.0f), new Vector3f(0.0f));
  }

  public Camera(final Vector3f position, final Vector3f rotation) {
    Camera.position = position;
    Camera.rotation = rotation;
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
      rotation.x = MathUtil.clamp(rotation.x, -maxPitch, maxPitch);
    }

    position.z += MouseScroll.getY() * MOUSE_SCROLL_SENSITIVITY;
  }

  public void imGui() {
    ImGui.setNextWindowSize(300.0f, 150.0f);
    ImGui.setNextWindowPos(0.0f, 510.0f);
    if (ImGui.begin("Camera")) {
      final float[] pos = new float[] {position.x, position.y, position.z};
      if (ImGui.dragFloat3("Position", pos)) {
        position.x = pos[0];
        position.y = pos[1];
        position.z = pos[2];
      }
      final float[] rot = new float[] {rotation.x, rotation.y, rotation.z};
      if (ImGui.dragFloat3("Rotation", rot)) {
        setRotation(rot[0], rot[1], rot[2]);
      }
      ImGui.end();
    }
  }

  public void setRotation(final float x, final float y, final float z) {
    rotation.x = MathUtil.revolve(x, 0.0f, 360.0f);
    rotation.y = MathUtil.revolve(y, 0.0f, 360.0f);
    rotation.z = MathUtil.revolve(z, 0.0f, 360.0f);
  }

  public static Matrix4f getViewMatrix() {
    return MathUtil.createViewMatrix(position, rotation);
  }
}

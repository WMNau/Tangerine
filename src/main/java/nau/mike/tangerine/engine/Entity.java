package nau.mike.tangerine.engine;

import imgui.ImGui;
import lombok.AllArgsConstructor;
import lombok.Data;
import nau.mike.tangerine.engine.shaders.Shader;
import nau.mike.tangerine.engine.utils.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unused")
@AllArgsConstructor
@Data
public class Entity {

  private final Mesh mesh;
  private final Material material;
  private Vector3f position;
  private Vector3f rotation;
  private Vector3f scale;

  public Entity(final Mesh mesh, final Material material) {
    this(mesh, material, new Vector3f(0.0f));
  }

  public Entity(final Mesh mesh, final Material material, final Vector3f position) {
    this(mesh, material, position, new Vector3f(0.0f));
  }

  public Entity(
      final Mesh mesh, final Material material, final Vector3f position, final Vector3f rotation) {
    this(mesh, material, position, rotation, new Vector3f(1.0f));
  }

  public void imGui() {
    material.imGui();
    ImGui.setNextWindowSize(300.0f, 150.0f);
    ImGui.setNextWindowPos(0.0f, 300.0f);
    if (ImGui.begin("Entity")) {
      final float[] pos = new float[] {position.x, position.y, position.z};
      if (ImGui.dragFloat3("Position", pos)) {
        position.x = pos[0];
        position.y = pos[1];
        position.z = pos[2];
      }
      final float[] rot = new float[] {rotation.x, rotation.y, rotation.z};
      if (ImGui.dragFloat3("Rotation", rot)) {
        rotation.x = rot[0];
        rotation.y = rot[1];
        rotation.z = rot[2];

        if (rotation.y < 0.0f) {
          rotation.y = 360.0f;
        } else if (rotation.y > 360.0f) {
          rotation.y = 0.0f;
        }
      }
      ImGui.end();
    }
    mesh.imGui();
  }

  public void draw(final Shader shader) {
    shader.loadModelMatrix(getModelMatrix());
    shader.loadViewMatrix(Camera.getViewMatrix());
    shader.loadProjectionMatrix(Window.getProjectionMatrix());
    shader.loadMaterial(material);
    mesh.draw();
  }

  public void clean() {
    mesh.clean();
  }

  public Matrix4f getModelMatrix() {
    return MathUtil.createModelMatrix(position, rotation, scale);
  }
}

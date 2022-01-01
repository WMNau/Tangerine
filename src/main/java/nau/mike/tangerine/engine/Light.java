package nau.mike.tangerine.engine;

import imgui.ImGui;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joml.Vector3f;

@AllArgsConstructor
@Data
public class Light {

  private Vector3f position;
  private Vector3f direction;

  private Vector3f ambient;
  private Vector3f diffuse;
  private Vector3f specular;

  private Attenuation attenuation;

  private Float cutoff;
  private Float outerCutoff;

  public void imGui() {
    ImGui.setNextWindowSize(300.0f, 150.0f);
    ImGui.setNextWindowPos(0.0f, 0.0f);
    if (ImGui.begin("Light")) {
      final float[] amb = new float[] {ambient.x, ambient.y, ambient.z};
      if (ImGui.colorEdit3("Ambient", amb)) {
        ambient.x = amb[0];
        ambient.y = amb[1];
        ambient.z = amb[2];
      }
      final float[] diff = new float[] {diffuse.x, diffuse.y, diffuse.z};
      if (ImGui.colorEdit3("Diffuse", diff)) {
        diffuse.x = diff[0];
        diffuse.y = diff[1];
        diffuse.z = diff[2];
      }
      final float[] spec = new float[] {specular.x, specular.y, specular.z};
      if (ImGui.colorEdit3("Specular", spec)) {
        specular.x = spec[0];
        specular.y = spec[1];
        specular.z = spec[2];
      }
      final float[] pos = new float[] {position.x, position.y, position.z};
      if (ImGui.sliderFloat3("Position", pos, -100.0f, 100.0f)) {
        position.x = pos[0];
        position.y = pos[1];
        position.z = pos[2];
      }
      final float[] dir = new float[] {direction.x, direction.y, direction.z};
      if (ImGui.sliderFloat3("Direction", dir, -100.0f, 100.0f)) {
        direction.x = dir[0];
        direction.y = dir[1];
        direction.z = dir[2];
      }
      ImGui.end();
    }
  }
}

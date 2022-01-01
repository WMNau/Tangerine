package nau.mike.tangerine.engine;

import imgui.ImGui;
import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("unused")
@AllArgsConstructor
@Data
public class Material {

  private Texture diffuseTexture;
  private Texture specularTexture;
  private float shininess;

  public void imGui() {
    ImGui.setNextWindowSize(300.0f, 150.0f);
    ImGui.setNextWindowPos(0.0f, 150.0f);
    if (ImGui.begin("Material")) {
      final float[] shine = new float[] {shininess};
      if (ImGui.sliderFloat("Shininess", shine, 0.0f, 100.0f)) {
        shininess = shine[0];
      }
      ImGui.end();
    }
  }
}

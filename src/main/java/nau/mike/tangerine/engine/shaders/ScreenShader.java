package nau.mike.tangerine.engine.shaders;

public class ScreenShader extends Shader {
  public ScreenShader() {
    super("screen", "screen");
  }

  @Override
  protected void bindAllAttributes() {
    bindAttribute(0, "position");
    bindAttribute(1, "uvs");
  }
}

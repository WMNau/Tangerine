package nau.mike.tangerine.engine.shaders;

public class CubemapShader extends Shader {
  public CubemapShader() {
    super("cubemap", "cubemap");
  }

  @Override
  protected void bindAllAttributes() {
    bindAttribute(0, "position");
  }
}

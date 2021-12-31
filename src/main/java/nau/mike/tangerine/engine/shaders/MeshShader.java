package nau.mike.tangerine.engine.shaders;

public class MeshShader extends Shader {

  public MeshShader() {
    super("mesh");
  }

  @Override
  protected void bindAllAttributes() {
    super.bindAttribute(0, "position");
  }
}

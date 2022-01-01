package nau.mike.tangerine.engine.shaders;

public class MeshShader extends Shader {

  public MeshShader() {
    super("mesh", "mesh");
  }

  @Override
  protected void bindAllAttributes() {
    super.bindAttribute(0, "position");
    super.bindAttribute(1, "normals");
    super.bindAttribute(2, "uvs");
  }
}

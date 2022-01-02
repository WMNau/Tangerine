package nau.mike.tangerine.engine;

import nau.mike.tangerine.engine.shaders.CubemapShader;
import nau.mike.tangerine.engine.shaders.Shader;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP;

public class CubeMap {

  protected final Mesh mesh;
  protected final Shader cubemapShader;

  protected final Texture texture;

  public CubeMap(final List<String> textureFileNameList, final float[] vertices) {
    this.mesh = new Mesh(vertices);
    this.cubemapShader = new CubemapShader();
    final String errorMassage = "Cube maps must contain only six textures";
    if (textureFileNameList.size() != 6) {
      throw new IllegalStateException(errorMassage);
    }
    this.texture = new Texture(textureFileNameList, true);
    cubemapShader.start();
    cubemapShader.setUniform("uCubeSampler", 0);
    cubemapShader.end();
  }

  public void draw() {
    glDepthFunc(GL_LEQUAL);
    cubemapShader.start();
    cubemapShader.loadProjectionMatrix(Window.getProjectionMatrix());
    cubemapShader.loadViewMatrix(new Matrix4f(new Matrix3f(Camera.getViewMatrix())));
    mesh.drawArrays(texture.getId(), GL_TEXTURE_CUBE_MAP);
    cubemapShader.end();
    glDepthFunc(GL_LESS);
  }

  public void clean() {
    mesh.clean();
    cubemapShader.clean();
  }
}

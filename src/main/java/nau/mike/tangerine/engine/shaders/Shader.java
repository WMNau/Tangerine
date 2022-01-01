package nau.mike.tangerine.engine.shaders;

import nau.mike.tangerine.engine.Attenuation;
import nau.mike.tangerine.engine.Light;
import nau.mike.tangerine.engine.Material;
import nau.mike.tangerine.engine.utils.FileUtil;
import nau.mike.tangerine.engine.utils.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER;

@SuppressWarnings("unused")
public abstract class Shader {

  private final int program;
  private final int vertex;
  private final int fragment;
  private final int geometry;

  private final Map<String, Integer> uniformMap;

  protected Shader(final String directory, final String fileName) {
    this(directory, fileName, false);
  }

  protected Shader(final String directory, final String fileName, final boolean hasGeometry) {
    this(String.format("%s/%s", directory, fileName), hasGeometry);
  }

  protected Shader(final String filePath) {
    this(filePath, false);
  }

  protected Shader(final String filePath, final boolean hasGeometry) {
    this.uniformMap = new HashMap<>();
    this.program = glCreateProgram();
    this.vertex = createShader(filePath + ".vs", GL_VERTEX_SHADER);
    this.fragment = createShader(filePath + ".fs", GL_FRAGMENT_SHADER);
    if (hasGeometry) {
      this.geometry = createShader(filePath + ".gs", GL_GEOMETRY_SHADER);
    } else {
      this.geometry = -1;
    }
    bindAllAttributes();
    link();
  }

  protected abstract void bindAllAttributes();

  protected void bindAttribute(final int attribute, final String name) {
    glBindAttribLocation(program, attribute, name);
  }

  public void start() {
    glUseProgram(program);
  }

  public void end() {
    glUseProgram(0);
  }

  public void clean() {
    end();
    glDetachShader(program, vertex);
    glDetachShader(program, fragment);
    if (0 < geometry) {
      glDetachShader(program, geometry);
    }

    glDeleteProgram(program);
  }

  public void loadModelMatrix(final Matrix4f matrix) {
    setUniform("uModelMatrix", matrix);
  }

  public void loadViewMatrix(final Matrix4f matrix) {
    setUniform("uViewMatrix", matrix);
  }

  public void loadProjectionMatrix(final Matrix4f matrix) {
    setUniform("uProjectionMatrix", matrix);
  }

  public void loadMaterial(final Material material) {
    final String uniform = "uMaterial";
    setUniform(uniform + ".diffuse", 0);
    if (null != material.getDiffuseTexture()) {
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, material.getDiffuseTexture().getId());
    }
    setUniform(uniform + ".specular", 1);
    if (null != material.getSpecularTexture()) {
      glActiveTexture(GL_TEXTURE1);
      glBindTexture(GL_TEXTURE_2D, material.getSpecularTexture().getId());
    }
    setUniform(uniform + ".shininess", material.getShininess());
  }

  public void loadDirectionalLight(Light light, final Vector3f viewPosition) {
    final String uniform = "uDirectionalLight";
    loadLight(uniform, light);
    setUniform("uViewPosition", viewPosition);
  }

  public void loadSpotLight(final Light light) {
    final String uniform = "uSpotLight";
    loadLight(uniform, light);
  }

  public void loadPointLights(final List<Light> lightList) {
    final String uniform = "uPointLights[";
    int i = 0;
    for (final Light light : lightList) {
      loadLight(String.format("%s%d]", uniform, i++), light);
    }
  }

  private void loadLight(final String uniform, final Light light) {
    if (null != light.getPosition()) {
      setUniform(uniform + ".position", light.getPosition());
    }
    if (null != light.getDirection()) {
      setUniform(uniform + ".direction", light.getDirection());
    }
    setUniform(uniform + ".ambient", light.getAmbient());
    setUniform(uniform + ".diffuse", light.getDiffuse());
    setUniform(uniform + ".specular", light.getSpecular());
    if (null != light.getCutoff()) {
      setUniform(uniform + ".cutoff", light.getCutoff());
    }
    if (null != light.getOuterCutoff()) {
      setUniform(uniform + ".outerCutoff", light.getOuterCutoff());
    }
    if (null != light.getAttenuation()) {
      final Attenuation attenuation = light.getAttenuation();
      setUniform(uniform + "constant", attenuation.getConstant());
      setUniform(uniform + "linear", attenuation.getLinear());
      setUniform(uniform + ".quadratic", attenuation.getQuadratic());
    }
  }

  private void setUniform(final String name, final boolean value) {
    setUniform(name, value ? 1 : 0);
  }

  private void setUniform(final String name, final int x) {
    final int location = getUniformLocation(name);
    glUniform1i(location, x);
  }

  private void setUniform(final String name, final float x) {
    final int location = getUniformLocation(name);
    glUniform1f(location, x);
  }

  private void setUniform(final String name, final float x, final float y) {
    final int location = getUniformLocation(name);
    glUniform2f(location, x, y);
  }

  private void setUniform(final String name, final float x, final float y, final float z) {
    final int location = getUniformLocation(name);
    glUniform3f(location, x, y, z);
  }

  private void setUniform(
      final String name, final float x, final float y, final float z, final float w) {
    final int location = getUniformLocation(name);
    glUniform4f(location, x, y, z, w);
  }

  private void setUniform(final String name, final Vector3f vector) {
    setUniform(name, vector.x, vector.y, vector.z);
  }

  private void setUniform(final String name, final Matrix4f matrix) {
    final int location = getUniformLocation(name);
    final FloatBuffer buffer = MathUtil.buffer(matrix);
    glUniformMatrix4fv(location, false, buffer);
  }

  protected int getUniformLocation(final String name) {
    if (uniformMap.containsKey(name)) {
      return uniformMap.get(name);
    }
    final int location = glGetUniformLocation(program, name);
    uniformMap.put(name, location);
    return location;
  }

  private int createShader(final String filePath, final int type) {
    final int shader = glCreateShader(type);
    final String source = FileUtil.getSource(String.format("/shaders/%s.glsl", filePath));
    glShaderSource(shader, source);
    glCompileShader(shader);
    final String errorMessage =
        String.format("ERROR::SHADER::%s%nCould not compile shader.", getShaderStr(type));
    checkSuccess(glGetShaderi(shader, GL_COMPILE_STATUS), errorMessage, glGetShaderInfoLog(shader));
    glAttachShader(program, shader);
    return shader;
  }

  private void checkSuccess(final int success, final String errorMessage, final String infoLog) {
    if (GL_FALSE == success) {
      throw new IllegalStateException(errorMessage + "\n" + infoLog);
    }
  }

  private String getShaderStr(final int type) {
    String result;
    switch (type) {
      case GL_VERTEX_SHADER:
        result = "VERTEX";
        break;
      case GL_FRAGMENT_SHADER:
        result = "FRAGMENT";
        break;
      case GL_GEOMETRY_SHADER:
        result = "GEOMETRY";
        break;
      default:
        result = "UNKNOWN";
    }
    return result;
  }

  private void link() {
    glLinkProgram(program);
    checkSuccess(
        glGetProgrami(program, GL_LINK_STATUS),
        "Could not link program",
        glGetProgramInfoLog(program));
    glDeleteShader(vertex);
    glDeleteShader(fragment);
    if (0 < geometry) {
      glDeleteShader(geometry);
    }
  }
}

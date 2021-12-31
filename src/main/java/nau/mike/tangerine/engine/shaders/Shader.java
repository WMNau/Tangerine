package nau.mike.tangerine.engine.shaders;

import nau.mike.tangerine.engine.utils.FileUtil;

import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER;

@SuppressWarnings("unused")
public abstract class Shader {

  private final int program;
  private final int vertex;
  private final int fragment;
  private final int geometry;

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

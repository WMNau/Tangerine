package nau.mike.tangerine.engine;

import imgui.ImGui;
import lombok.Setter;
import nau.mike.tangerine.engine.utils.MathUtil;
import nau.mike.tangerine.engine.utils.Vertex;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.*;

@SuppressWarnings("unused")
public class Mesh {

  private final int vao;
  private final List<Integer> vbos;
  private final List<Integer> textureIdList;
  private int attributes;
  private final int count;
  @Setter private boolean enablePolygonMode;

  public Mesh(final Mesh mesh) {
    this.vao = mesh.vao;
    this.vbos = mesh.vbos;
    this.textureIdList = mesh.textureIdList;
    this.attributes = mesh.attributes;
    this.count = mesh.count;
    this.enablePolygonMode = mesh.enablePolygonMode;
  }

  public Mesh(final Vertex vertex, final int[] indices) {
    this(vertex, indices, null);
  }

  public Mesh(final Vertex vertex, final int[] indices, final List<Texture> textureList) {
    this.vbos = new ArrayList<>();
    this.textureIdList = new ArrayList<>();
    this.attributes = 0;
    this.vao = createVao();
    createIndicesBuffer(indices);
    storeDataInAttributeList(vertex.getPosition(), 3);
    storeDataInAttributeList(vertex.getNormals(), 3);
    storeDataInAttributeList(vertex.getUvs(), 2);
    glBindVertexArray(0);
    this.count = indices.length;
    setEnablePolygonMode(false);
    if (textureList != null) {
      textureList.forEach(this::addTexture);
    }
  }

  public void imGui() {
    ImGui.setNextWindowSize(300.0f, 60.0f);
    ImGui.setNextWindowPos(0.0f, 450.0f);
    if (ImGui.begin("Mesh")) {
      String label = "View mesh";
      if (enablePolygonMode) {
        label = "View model";
      }
      if (ImGui.checkbox(label, enablePolygonMode)) {
        enablePolygonMode = !enablePolygonMode;
      }
      ImGui.end();
    }
  }

  public void addTexture(final Texture texture) {
    textureIdList.add(texture.getId());
  }

  public void addTexture(
      final String directory, final String fileName, final String ext, final boolean flipOnLoad) {
    addTexture(new Texture(directory, fileName, ext, flipOnLoad));
  }

  public void addTexture(final String directory, final String fileName, final String ext) {
    addTexture(directory, fileName, ext, false);
  }

  public void addTexture(final String fileName, final String ext, final boolean flipOnLoad) {
    addTexture("", fileName, ext, flipOnLoad);
  }

  public void addTexture(final String fileName, final String ext) {
    addTexture("", fileName, ext);
  }

  public void addTexture(final String fileName, final boolean flipOnLoad) {
    addTexture(fileName, "png", flipOnLoad);
  }

  public void addTexture(final String fileName) {
    addTexture(fileName, "png", false);
  }

  public void draw() {
    int textureCount = 0;
    for (final int textureId : textureIdList) {
      glActiveTexture(GL_TEXTURE2 + textureCount++);
      glBindTexture(GL_TEXTURE_2D, textureId);
    }

    glBindVertexArray(vao);
    enableVertexAttribArray(true);

    if (enablePolygonMode) {
      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
    enableVertexAttribArray(false);
    glBindVertexArray(0);
    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
  }

  public void clean() {
    textureIdList.forEach(GL30::glDeleteTextures);
    vbos.forEach(GL30::glDeleteBuffers);
    glDeleteVertexArrays(vao);
  }

  private int createVao() {
    final int id = glGenVertexArrays();
    glBindVertexArray(id);
    return id;
  }

  private void createIndicesBuffer(int[] data) {
    final int id = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
    vbos.add(id);
    final IntBuffer buffer = MathUtil.buffer(data);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
  }

  private void storeDataInAttributeList(final float[] data, final int size) {
    final int id = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, id);
    vbos.add(id);
    final FloatBuffer buffer = MathUtil.buffer(data);
    glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    glVertexAttribPointer(attributes++, size, GL_FLOAT, false, 0, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  private void enableVertexAttribArray(final boolean enable) {
    for (int i = 0; i < attributes; i++) {
      if (enable) {
        glEnableVertexAttribArray(i);
      } else {
        glDisableVertexAttribArray(i);
      }
    }
  }
}

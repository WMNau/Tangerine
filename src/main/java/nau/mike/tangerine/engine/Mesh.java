package nau.mike.tangerine.engine;

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

public class Mesh {

  private final int vao;
  private final List<Integer> vbos;
  private int attributes;
  private final int count;
  private boolean enablePolygonMode;

  public Mesh(final Vertex vertex) {
    this.vbos = new ArrayList<>();
    this.attributes = 0;
    this.vao = createVao();
    createIndicesBuffer(vertex.getIndices());
    storeDataInAttributeList(vertex.getPosition(), 3);
    glBindVertexArray(0);
    this.count = vertex.getIndexCount();
    setEnablePolygonMode(false);
  }

  public void draw() {
    glBindVertexArray(vao);
    enableVertexAttribArray(true);
    if (enablePolygonMode) {
      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }
    glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
    enableVertexAttribArray(false);
    glBindVertexArray(0);
  }

  public void clean() {
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

  public void setEnablePolygonMode(final boolean enablePolygonMode) {
    this.enablePolygonMode = enablePolygonMode;
  }
}

package nau.mike.tangerine.engine.utils;

public class Vertex {

  private final float[] position;
  private final float[] uvs;
  private final int[] indices;

  public Vertex(final float[] position, float[] uvs, final int[] indices) {
    this.position = position;
    this.uvs = uvs;
    this.indices = indices;
  }

  public float[] getPosition() {
    return position;
  }

  public float[] getUvs() {
    return uvs;
  }

  public int[] getIndices() {
    return indices;
  }

  public int getIndexCount() {
    return indices.length;
  }
}

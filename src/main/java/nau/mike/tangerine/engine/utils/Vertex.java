package nau.mike.tangerine.engine.utils;

public class Vertex {

  private final float[] position;
  private final int[] indices;

  public Vertex(final float[] position, final int[] indices) {
    this.position = position;
    this.indices = indices;
  }

  public float[] getPosition() {
    return position;
  }

  public int[] getIndices() {
    return indices;
  }

  public int getIndexCount() {
    return indices.length;
  }
}

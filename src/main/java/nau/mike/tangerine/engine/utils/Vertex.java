package nau.mike.tangerine.engine.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Vertex {

  private final float[] position;
  private final float[] normals;
  private final float[] uvs;
}

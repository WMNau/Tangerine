package nau.mike.tangerine.models;

import nau.mike.tangerine.engine.Mesh;
import nau.mike.tangerine.engine.ObjLoader;

public class StallModel extends Mesh {

  public StallModel() {
    super(ObjLoader.load("stall"));
  }
}

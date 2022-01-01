package nau.mike.tangerine.entities;

import nau.mike.tangerine.engine.Entity;
import nau.mike.tangerine.engine.Material;
import nau.mike.tangerine.models.StallModel;
import nau.mike.tangerine.textures.StallTexture;
import org.joml.Vector3f;

public class Stall extends Entity {

  public Stall(final Vector3f position, final Vector3f rotation, final Vector3f scale) {
    super(
        new StallModel(), new Material(new StallTexture(), null, 20.0f), position, rotation, scale);
  }
}

package nau.mike.tangerine.engine;

import nau.mike.tangerine.engine.utils.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unused")
public class Entity {

  private final Mesh mesh;
  private Vector3f position;
  private Vector3f rotation;
  private Vector3f scale;

  private Matrix4f modelMatrix;

  public Entity(final Mesh mesh) {
    this(mesh, new Vector3f(0.0f));
  }

  public Entity(final Mesh mesh, final Vector3f position) {
    this(mesh, position, new Vector3f(0.0f));
  }

  public Entity(final Mesh mesh, final Vector3f position, final Vector3f rotation) {
    this(mesh, position, rotation, new Vector3f(1.0f));
  }

  public Entity(
      final Mesh mesh, final Vector3f position, final Vector3f rotation, final Vector3f scale) {
    this.mesh = mesh;
    this.position = position;
    this.rotation = rotation;
    this.scale = scale;
    this.modelMatrix = MathUtil.createModelMatrix(position, rotation, scale);
  }

  public void draw() {
    mesh.draw();
  }

  public void clean() {
    mesh.clean();
  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(final Vector3f position) {
    this.position = position;
    this.modelMatrix = MathUtil.createModelMatrix(position, rotation, scale);
  }

  public void setPosition(final float x, final float y, final float z) {
    setPosition(new Vector3f(x, y, z));
  }

  public void setPosition(final float xyz) {
    setPosition(xyz, xyz, xyz);
  }

  public Vector3f getRotation() {
    return rotation;
  }

  public void setRotation(final Vector3f rotation) {
    this.rotation = rotation;
    this.modelMatrix = MathUtil.createModelMatrix(position, rotation, scale);
  }

  public void setRotation(final float x, final float y, final float z) {
    setRotation(new Vector3f(x, y, z));
  }

  public void setRotation(final float xyz) {
    setRotation(xyz, xyz, xyz);
  }

  public Vector3f getScale() {
    return scale;
  }

  public void setScale(final Vector3f scale) {
    this.scale = scale;
    this.modelMatrix = MathUtil.createModelMatrix(position, rotation, scale);
  }

  public void setScale(final float x, final float y, final float z) {
    setScale(new Vector3f(x, y, z));
  }

  public void setScale(final float xyz) {
    setScale(xyz, xyz, xyz);
  }

  public Matrix4f getModelMatrix() {
    return modelMatrix;
  }
}

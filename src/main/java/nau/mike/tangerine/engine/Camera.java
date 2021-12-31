package nau.mike.tangerine.engine;

import nau.mike.tangerine.engine.utils.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unused")
public class Camera {

  private Vector3f position;
  private Vector3f rotation;

  private Matrix4f viewMatrix;

  public Camera() {
    this(new Vector3f(0.0f, 0.0f, 20.0f), new Vector3f(0.0f));
  }

  public Camera(final Vector3f position) {
    this(position, new Vector3f(0.0f));
  }

  public Camera(final Vector3f position, final Vector3f rotation) {
    this.position = position;
    this.rotation = rotation;
    this.viewMatrix = MathUtil.createViewMatrix(position, rotation);
  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(final Vector3f position) {
    this.position = position;
    this.viewMatrix = MathUtil.createViewMatrix(position, rotation);
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
    this.viewMatrix = MathUtil.createViewMatrix(position, rotation);
  }

  public void setRotation(final float x, final float y, final float z) {
    setRotation(new Vector3f(x, y, z));
  }

  public void setRotation(final float xyz) {
    setRotation(xyz, xyz, xyz);
  }

  public Matrix4f getViewMatrix() {
    return viewMatrix;
  }
}

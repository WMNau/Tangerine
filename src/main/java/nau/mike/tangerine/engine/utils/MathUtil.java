package nau.mike.tangerine.engine.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MathUtil {

  private MathUtil() {}

  public static FloatBuffer buffer(final float[] data) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final FloatBuffer buffer = stack.mallocFloat(data.length);
      buffer.put(data).flip();
      return buffer;
    }
  }

  public static FloatBuffer buffer(final Matrix4f matrix) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final FloatBuffer buffer = stack.mallocFloat(16);
      matrix.get(buffer);
      return buffer;
    }
  }

  public static IntBuffer buffer(int[] data) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer buffer = stack.mallocInt(data.length);
      buffer.put(data).flip();
      return buffer;
    }
  }

  public static Matrix4f createModelMatrix(
      final Vector3f position, final Vector3f rotation, final Vector3f scale) {
    final Matrix4f matrix = new Matrix4f().identity();
    matrix.translation(position);
    matrix
        .rotateX((float) Math.toRadians(rotation.x))
        .rotateY((float) Math.toRadians(rotation.y))
        .rotateZ((float) Math.toRadians(rotation.z));
    matrix.scale(scale);
    return matrix;
  }

  public static Matrix4f createViewMatrix(final Vector3f position, final Vector3f rotation) {
    final Matrix4f matrix = new Matrix4f().identity();
    matrix
        .rotateX((float) Math.toRadians(rotation.x))
        .rotateY((float) Math.toRadians(rotation.y))
        .rotateZ((float) Math.toRadians(rotation.z));
    matrix.translation(-position.x, -position.y, -position.z);
    return matrix;
  }

  public static Matrix4f createProjectionMatrix(
      final float fov, final float aspect, final float zNear, final float zFar) {
    final Matrix4f matrix = new Matrix4f().identity();
    matrix.perspective(fov, aspect, zNear, zFar);
    return matrix;
  }
}

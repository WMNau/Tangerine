package nau.mike.tangerine.engine.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MathUtil {

  public static FloatBuffer buffer(final float[] data) {
    final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data).flip();
    return buffer;
  }

  public static FloatBuffer buffer(final Matrix4f matrix) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final FloatBuffer buffer = stack.mallocFloat(16);
      matrix.get(buffer);
      return buffer;
    }
  }

  public static IntBuffer buffer(int[] data) {
    final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data).flip();
    return buffer;
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

  public static float[] toFloatArray(final List<Float> list) {
    final float[] arr = new float[list.size()];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = list.get(i);
    }
    return arr;
  }

  public static int[] toIntArray(final List<Integer> list) {
    final int[] arr = new int[list.size()];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = list.get(i);
    }
    return arr;
  }
}

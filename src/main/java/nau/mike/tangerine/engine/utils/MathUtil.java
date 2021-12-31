package nau.mike.tangerine.engine.utils;

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

  public static IntBuffer buffer(int[] data) {
    try (final MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer buffer = stack.mallocInt(data.length);
      buffer.put(data).flip();
      return buffer;
    }
  }
}

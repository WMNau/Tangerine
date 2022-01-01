package nau.mike.tangerine.engine.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimerUtil {
  private static long lastTime;
  private static final float NS;
  private static float delta;

  private static long resetTimer;
  private static int updates;
  private static int frames;

  static {
    lastTime = System.nanoTime();
    NS = 1_000_000_000.0f / 60.0f;
    delta = 0.0f;
    resetTimer = System.currentTimeMillis();
    updates = 0;
    frames = 0;
  }

  public static void start() {
    final long now = System.nanoTime();
    delta += (now - lastTime) / NS;
    lastTime = now;
  }

  public static boolean shouldUpdate() {
    return delta >= 1.0f;
  }

  public static void update() {
    delta--;
    updates++;
  }

  public static void render() {
    frames++;
  }

  public static boolean shouldReset() {
    return System.currentTimeMillis() - resetTimer > 1000L;
  }

  public static void reset() {
    resetTimer += 1000L;
    updates = 0;
    frames = 0;
  }

  public static String message() {
    return "FPS: " + frames + ", UPS: " + updates;
  }

  public static float getDelta() {
    return delta;
  }
}

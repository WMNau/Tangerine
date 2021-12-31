package nau.mike.tangerine;

import nau.mike.tangerine.engine.Window;
import nau.mike.tangerine.engine.input.Keyboard;
import nau.mike.tangerine.engine.input.Keys;
import nau.mike.tangerine.engine.utils.TimerUtil;

public class Application {

  private final Window window;

  public Application() {
    this.window = new Window("Tangerine Game Engine", 1080, 768);
  }

  private void init() {}

  private void update() {}

  private void render() {}

  private void run() {
    window.setClearColor(0.0f);
    while (!window.shouldClose()) {
      TimerUtil.start();
      while (TimerUtil.shouldUpdate()) {
        update();
        TimerUtil.update();
      }
      window.startFrame();
      render();
      window.endFrame();
      if (Keyboard.pressed(Keys.ESCAPE)) {
        window.close();
      }

      if (TimerUtil.shouldReset()) {
        window.debugTitle(TimerUtil.message());
        TimerUtil.reset();
      }
    }
    window.clean();
  }

  public static void main(String[] args) {
    new Application().run();
  }
}

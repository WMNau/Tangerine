package nau.mike.tangerine;

import nau.mike.tangerine.engine.Window;

public class Application {

  private final Window window;

  public Application() {
    this.window = new Window("Tangerine Game Engine", 1080, 768);
  }

  private void run() {
    window.setClearColor(0.0f);
    while (!window.shouldClose()) {
        window.startFrame();
        window.endFrame();
    }
    window.clean();
  }

  public static void main(String[] args) {
    new Application().run();
  }
}

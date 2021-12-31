package nau.mike.tangerine;

import nau.mike.tangerine.engine.Entity;
import nau.mike.tangerine.engine.Mesh;
import nau.mike.tangerine.engine.Window;
import nau.mike.tangerine.engine.input.Keyboard;
import nau.mike.tangerine.engine.input.Keys;
import nau.mike.tangerine.engine.shaders.MeshShader;
import nau.mike.tangerine.engine.shaders.Shader;
import nau.mike.tangerine.engine.utils.TimerUtil;
import nau.mike.tangerine.engine.utils.Vertex;

public class Application {

  private static final float[] vertices = {
    0.5f, 0.5f, 0.0f, // top right
    0.5f, -0.5f, 0.0f, // bottom right
    -0.5f, -0.5f, 0.0f, // bottom left
    -0.5f, 0.5f, 0.0f // top left
  };
  private static final float[] uvs = {
    1.0f, 1.0f, // top right
    1.0f, 0.0f, // bottom right
    0.0f, 0.0f, // bottom left
    0.0f, 1.0f // top left
  };
  private static final int[] indices = {
    0, 1, 3, // first triangle
    1, 2, 3 // second triangle
  };

  private Entity entity;
  private Shader meshShader;

  private final Window window;

  public Application() {
    this.window = new Window("Tangerine Game Engine", 1080, 768);
  }

  private void init() {
    final Mesh mesh = new Mesh(new Vertex(vertices, uvs, indices));
    mesh.addTexture("awesomeface");
    entity = new Entity(mesh);
    meshShader = new MeshShader();
  }

  int time = 0;

  private void update() {
    time++;
    entity.setPosition((float) Math.sin(time), 0.0f, 0.0f);
  }

  private void render() {
    meshShader.start();
    meshShader.loadModelMatrix(entity.getModelMatrix());
    entity.draw();
    meshShader.end();
  }

  private void clean() {
    entity.clean();
    meshShader.clean();
  }

  private void run() {
    window.setClearColor(0.0f);
    init();
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
    clean();
    window.clean();
  }

  public static void main(String[] args) {
    new Application().run();
  }
}

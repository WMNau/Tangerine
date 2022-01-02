package nau.mike.tangerine;

import nau.mike.tangerine.engine.*;
import nau.mike.tangerine.engine.input.Keyboard;
import nau.mike.tangerine.engine.input.Keys;
import nau.mike.tangerine.engine.shaders.MeshShader;
import nau.mike.tangerine.engine.shaders.Shader;
import nau.mike.tangerine.engine.utils.TimerUtil;
import nau.mike.tangerine.entities.Stall;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Application {

  private Entity entity;

  private Light directionalLight;
  private Light spotLight;
  private final List<Light> pointLightList;

  private final Window window;
  private final Shader meshShader;
  private final Camera camera;
  private final CubeMap skyBox;

  public Application() {
    this.window = new Window("Tangerine Game Engine", 1080, 768);
    this.pointLightList = new ArrayList<>();
    this.meshShader = new MeshShader();
    this.camera = new Camera();
    this.skyBox = new SkyBox();
  }

  private void init() {
    entity =
        new Stall(
            new Vector3f(-3.0f, 9.0f, -31.0f),
            new Vector3f(180.0f, 38.0f, 0.0f),
            new Vector3f(2.0f));

    createDirectionalLight();
    createSpotLight();
    createPointLight();
  }

  private void update() {
    camera.update();
  }

  public void imGui() {
    entity.imGui();
    camera.imGui();
  }

  private void render() {
    skyBox.draw();

    meshShader.start();
    loadLights();
    entity.draw(meshShader);
    meshShader.end();
  }

  private void clean() {
    entity.clean();
    skyBox.clean();
    meshShader.clean();
  }

  private void run() {
    window.setClearColor(0.01f);
    init();
    while (!window.shouldClose()) {
      TimerUtil.start();
      while (TimerUtil.shouldUpdate()) {
        update();
        TimerUtil.update();
      }
      window.startFrame();
      imGui();
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

  private void createDirectionalLight() {
    directionalLight =
        new Light(
            null,
            new Vector3f(-0.2f, -1.0f, -0.3f),
            new Vector3f(0.05f),
            new Vector3f(0.4f),
            new Vector3f(0.5f),
            null,
            null,
            null);
  }

  private void createSpotLight() {
    spotLight =
        new Light(
            Camera.getPosition(),
            new Vector3f(0.0f, 0.0f, -1.0f),
            new Vector3f(0.0f),
            new Vector3f(1.0f),
            new Vector3f(1.0f),
            new Attenuation(1.0f, 0.09f, 0.032f),
            (float) Math.cos(Math.toRadians(12.5f)),
            (float) Math.cos(Math.toRadians(15.0f)));
  }

  private void createPointLight() {
    final Vector3f[] positions =
        new Vector3f[] {
          new Vector3f(0.7f, 0.2f, 2.0f),
          new Vector3f(2.3f, -3.3f, -4.0f),
          new Vector3f(-4.0f, 2.0f, -12.0f),
          new Vector3f(0.0f, 0.0f, -3.0f)
        };
    for (int i = 0; i < 4; i++) {
      final Light pointLight =
          new Light(
              positions[i],
              null,
              new Vector3f(0.5f),
              new Vector3f(0.8f),
              new Vector3f(1.0f),
              new Attenuation(1.0f, 0.09f, 0.032f),
              null,
              null);
      pointLightList.add(pointLight);
    }
  }

  private void loadLights() {
    meshShader.loadDirectionalLight(directionalLight);
    meshShader.loadSpotLight(spotLight);
    meshShader.loadPointLights(pointLightList);
  }
}

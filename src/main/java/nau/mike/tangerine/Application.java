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

  private Camera camera;
  private Entity entity;
  private Shader meshShader;

  private Light directionalLight;
  private Light spotLight;
  private final List<Light> pointLightList;

  private final Window window;

  public Application() {
    this.window = new Window("Tangerine Game Engine", 1080, 768);
    this.pointLightList = new ArrayList<>();
  }

  private void init() {
    camera = new Camera();
    meshShader = new MeshShader();
    entity =
        new Stall(
            new Vector3f(-3.0f, 9.0f, -31.0f),
            new Vector3f(180.0f, 38.0f, 0.0f),
            new Vector3f(2.0f));
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
    spotLight =
        new Light(
            camera.getPosition(),
            new Vector3f(0.0f, 0.0f, -1.0f),
            new Vector3f(0.0f),
            new Vector3f(1.0f),
            new Vector3f(1.0f),
            new Attenuation(1.0f, 0.09f, 0.032f),
            (float) Math.cos(Math.toRadians(12.5f)),
            (float) Math.cos(Math.toRadians(15.0f)));

    Light pointLight =
        new Light(
            new Vector3f(0.7f, 0.2f, 2.0f),
            null,
            new Vector3f(0.5f),
            new Vector3f(0.8f),
            new Vector3f(1.0f),
            new Attenuation(1.0f, 0.09f, 0.032f),
            null,
            null);
    pointLightList.add(pointLight);
    pointLight =
        new Light(
            new Vector3f(2.3f, -3.3f, -4.0f),
            null,
            new Vector3f(0.5f),
            new Vector3f(0.8f),
            new Vector3f(1.0f),
            new Attenuation(1.0f, 0.09f, 0.032f),
            null,
            null);
    pointLightList.add(pointLight);
    pointLight =
        new Light(
            new Vector3f(-4.0f, 2.0f, -12.0f),
            null,
            new Vector3f(0.5f),
            new Vector3f(0.8f),
            new Vector3f(1.0f),
            new Attenuation(1.0f, 0.09f, 0.032f),
            null,
            null);
    pointLightList.add(pointLight);
    pointLight =
        new Light(
            new Vector3f(0.0f, 0.0f, -3.0f),
            null,
            new Vector3f(0.5f),
            new Vector3f(0.8f),
            new Vector3f(1.0f),
            new Attenuation(1.0f, 0.09f, 0.032f),
            null,
            null);
    pointLightList.add(pointLight);
  }

  private void update() {
    camera.update();
  }

  public void imGui() {
    entity.imGui();
  }

  private void render() {
    meshShader.start();
    meshShader.loadModelMatrix(entity.getModelMatrix());
    meshShader.loadViewMatrix(camera.getViewMatrix());
    meshShader.loadProjectionMatrix(Window.getProjectionMatrix());
    meshShader.loadMaterial(entity.getMaterial());
    meshShader.loadDirectionalLight(directionalLight, camera.getPosition());
    meshShader.loadSpotLight(spotLight);
    meshShader.loadPointLights(pointLightList);
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
}

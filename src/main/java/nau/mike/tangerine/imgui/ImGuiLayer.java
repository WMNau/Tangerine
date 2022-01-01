package nau.mike.tangerine.imgui;

import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import nau.mike.tangerine.engine.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

  private long windowPtr;

  private boolean glfwHasPerMonitorDpi;
  private boolean glfwHasMonitorWorkArea;

  private final int[] winWidth = new int[1];
  private final int[] winHeight = new int[1];
  private final int[] fbWidth = new int[1];
  private final int[] fbHeight = new int[1];

  private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

  private final boolean[] mouseJustPressed = new boolean[ImGuiMouseButton.COUNT];
  private final ImVec2 mousePosBackup = new ImVec2();
  private final double[] mouseX = new double[1];
  private final double[] mouseY = new double[1];

  private final int[] windowX = new int[1];
  private final int[] windowY = new int[1];

  private final int[] monitorX = new int[1];
  private final int[] monitorY = new int[1];
  private final int[] monitorWorkAreaX = new int[1];
  private final int[] monitorWorkAreaY = new int[1];
  private final int[] monitorWorkAreaWidth = new int[1];
  private final int[] monitorWorkAreaHeight = new int[1];
  private final float[] monitorContentScaleX = new float[1];
  private final float[] monitorContentScaleY = new float[1];

  private GLFWWindowFocusCallback prevUserCallbackWindowFocus = null;
  private GLFWMouseButtonCallback prevUserCallbackMouseButton = null;
  private GLFWScrollCallback prevUserCallbackScroll = null;
  private GLFWKeyCallback prevUserCallbackKey = null;
  private GLFWCharCallback prevUserCallbackChar = null;
  private GLFWCursorEnterCallback prevUserCallbackCursorEnter = null;

  private boolean callbacksInstalled = false;
  private boolean wantUpdateMonitors = true;
  private double time = 0.0;
  private long mouseWindowPtr;

  private ImGuiImplGl3 imGuiImplGl3;

  public void mouseButtonCallback(
      final long windowId, final int button, final int action, final int mods) {
    if (prevUserCallbackMouseButton != null && windowId == windowPtr) {
      prevUserCallbackMouseButton.invoke(windowId, button, action, mods);
    }

    if (action == GLFW_PRESS && button >= 0 && button < mouseJustPressed.length) {
      mouseJustPressed[button] = true;
    }
  }

  public void scrollCallback(final long windowId, final double xOffset, final double yOffset) {
    if (prevUserCallbackScroll != null && windowId == windowPtr) {
      prevUserCallbackScroll.invoke(windowId, xOffset, yOffset);
    }

    final ImGuiIO io = ImGui.getIO();
    io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
    io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
  }

  public void keyCallback(
      final long windowId, final int key, final int scancode, final int action, final int mods) {
    if (prevUserCallbackKey != null && windowId == windowPtr) {
      prevUserCallbackKey.invoke(windowId, key, scancode, action, mods);
    }

    final ImGuiIO io = ImGui.getIO();

    if (key >= 0) {
      if (action == GLFW_PRESS) {
        io.setKeysDown(key, true);
      } else if (action == GLFW_RELEASE) {
        io.setKeysDown(key, false);
      }
    }

    io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
    io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
    io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
    io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
  }

  public void windowFocusCallback(final long windowId, final boolean focused) {
    if (prevUserCallbackWindowFocus != null && windowId == windowPtr) {
      prevUserCallbackWindowFocus.invoke(windowId, focused);
    }

    ImGui.getIO().addFocusEvent(focused);
  }

  public void cursorEnterCallback(final long windowId, final boolean entered) {
    if (prevUserCallbackCursorEnter != null && windowId == windowPtr) {
      prevUserCallbackCursorEnter.invoke(windowId, entered);
    }

    if (entered) {
      mouseWindowPtr = windowId;
    }
    if (!entered && mouseWindowPtr == windowId) {
      mouseWindowPtr = 0;
    }
  }

  public void charCallback(final long windowId, final int c) {
    if (prevUserCallbackChar != null && windowId == windowPtr) {
      prevUserCallbackChar.invoke(windowId, c);
    }

    final ImGuiIO io = ImGui.getIO();
    io.addInputCharacter(c);
  }

  public void monitorCallback(final long windowId, final int event) {
    wantUpdateMonitors = true;
  }

  public void init(final long windowId, final boolean installCallbacks) {
    this.windowPtr = windowId;
    ImGui.createContext();
    imGuiImplGl3 = new ImGuiImplGl3();
    imGuiImplGl3.init("#version 400 core");

    detectGlfwVersionAndEnabledFeatures();

    final ImGuiIO io = ImGui.getIO();
    io.setDisplaySize(Window.getWindowSize().x, Window.getWindowSize().y);
    io.addBackendFlags(
        ImGuiBackendFlags.HasMouseCursors
            | ImGuiBackendFlags.HasSetMousePos
            | ImGuiBackendFlags.PlatformHasViewports);
    io.setBackendPlatformName("imgui_java_impl_glfw");

    final int[] keyMap = new int[ImGuiKey.COUNT];
    keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
    keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
    keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
    keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
    keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
    keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
    keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
    keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
    keyMap[ImGuiKey.End] = GLFW_KEY_END;
    keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
    keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
    keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
    keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
    keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
    keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
    keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
    keyMap[ImGuiKey.A] = GLFW_KEY_A;
    keyMap[ImGuiKey.C] = GLFW_KEY_C;
    keyMap[ImGuiKey.V] = GLFW_KEY_V;
    keyMap[ImGuiKey.X] = GLFW_KEY_X;
    keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
    keyMap[ImGuiKey.Z] = GLFW_KEY_Z;

    io.setKeyMap(keyMap);

    io.setGetClipboardTextFn(
        new ImStrSupplier() {
          @Override
          public String get() {
            final String clipboardString = glfwGetClipboardString(windowId);
            return clipboardString != null ? clipboardString : "";
          }
        });

    io.setSetClipboardTextFn(
        new ImStrConsumer() {
          @Override
          public void accept(final String str) {
            glfwSetClipboardString(windowId, str);
          }
        });

    final GLFWErrorCallback prevErrorCallback = glfwSetErrorCallback(null);
    mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
    mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    glfwSetErrorCallback(prevErrorCallback);

    if (installCallbacks) {
      callbacksInstalled = true;
      prevUserCallbackWindowFocus = glfwSetWindowFocusCallback(windowId, this::windowFocusCallback);
      prevUserCallbackCursorEnter = glfwSetCursorEnterCallback(windowId, this::cursorEnterCallback);
      prevUserCallbackMouseButton = glfwSetMouseButtonCallback(windowId, this::mouseButtonCallback);
      prevUserCallbackScroll = glfwSetScrollCallback(windowId, this::scrollCallback);
      prevUserCallbackKey = glfwSetKeyCallback(windowId, this::keyCallback);
      prevUserCallbackChar = glfwSetCharCallback(windowId, this::charCallback);
    }

    updateMonitors();
    glfwSetMonitorCallback(this::monitorCallback);

    final ImGuiViewport mainViewport = ImGui.getMainViewport();
    mainViewport.setPlatformHandle(windowPtr);
  }

  public void newFrame() {
    ImGui.newFrame();
    final ImGuiIO io = ImGui.getIO();

    glfwGetWindowSize(windowPtr, winWidth, winHeight);
    glfwGetFramebufferSize(windowPtr, fbWidth, fbHeight);

    io.setDisplaySize(winWidth[0], winHeight[0]);
    if (winWidth[0] > 0 && winHeight[0] > 0) {
      final float scaleX = (float) fbWidth[0] / winWidth[0];
      final float scaleY = (float) fbHeight[0] / winHeight[0];
      io.setDisplayFramebufferScale(scaleX, scaleY);
    }
    if (wantUpdateMonitors) {
      updateMonitors();
    }

    final double currentTime = glfwGetTime();
    io.setDeltaTime(time > 0.0 ? (float) (currentTime - time) : 1.0f / 60.0f);
    time = currentTime;

    updateMousePosAndButtons();
    updateMouseCursor();
  }

  public void endFrame() {
    ImGui.endFrame();
    ImGui.render();
    imGuiImplGl3.renderDrawData(ImGui.getDrawData());
  }

  public void dispose() {
    if (callbacksInstalled) {
      glfwSetWindowFocusCallback(windowPtr, prevUserCallbackWindowFocus);
      glfwSetCursorEnterCallback(windowPtr, prevUserCallbackCursorEnter);
      glfwSetMouseButtonCallback(windowPtr, prevUserCallbackMouseButton);
      glfwSetScrollCallback(windowPtr, prevUserCallbackScroll);
      glfwSetKeyCallback(windowPtr, prevUserCallbackKey);
      glfwSetCharCallback(windowPtr, prevUserCallbackChar);
      callbacksInstalled = false;
    }

    for (int i = 0; i < ImGuiMouseCursor.COUNT; i++) {
      glfwDestroyCursor(mouseCursors[i]);
    }
  }

  private void detectGlfwVersionAndEnabledFeatures() {
    final int[] major = new int[1];
    final int[] minor = new int[1];
    final int[] rev = new int[1];
    glfwGetVersion(major, minor, rev);

    final int version = major[0] * 1000 + minor[0] * 100 + rev[0] * 10;

    glfwHasPerMonitorDpi = version >= 3300;
    glfwHasMonitorWorkArea = version >= 3300;
  }

  private void updateMousePosAndButtons() {
    final ImGuiIO io = ImGui.getIO();
    setMouseDown(io);

    io.getMousePos(mousePosBackup);
    io.setMousePos(-Float.MAX_VALUE, -Float.MAX_VALUE);
    io.setMouseHoveredViewport(0);
    setPlatformerIo(io);
  }

  private void setMouseDown(final ImGuiIO io) {
    for (int i = 0; i < ImGuiMouseButton.COUNT; i++) {
      io.setMouseDown(i, mouseJustPressed[i] || glfwGetMouseButton(windowPtr, i) != 0);
      mouseJustPressed[i] = false;
    }
  }

  private void setPlatformerIo(final ImGuiIO io) {
    final ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
    for (int n = 0; n < platformIO.getViewportsSize(); n++) {
      final ImGuiViewport viewport = platformIO.getViewports(n);
      final long window = viewport.getPlatformHandle();

      final boolean focused = glfwGetWindowAttrib(window, GLFW_FOCUSED) != 0;
      final long mouseWindow = (this.mouseWindowPtr == window || focused) ? window : 0;

      updateMouseButtons(focused, io, window);

      if (io.getWantSetMousePos() && focused) {
        glfwSetCursorPos(
            window, mousePosBackup.x - viewport.getPosX(), mousePosBackup.y - viewport.getPosY());
      }

      if (mouseWindow != 0) {
        glfwGetCursorPos(mouseWindow, mouseX, mouseY);

        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
          glfwGetWindowPos(window, windowX, windowY);
          io.setMousePos((float) mouseX[0] + windowX[0], (float) mouseY[0] + windowY[0]);
        } else {
          io.setMousePos((float) mouseX[0], (float) mouseY[0]);
        }
      }
    }
  }

  private void updateMouseButtons(final boolean focused, final ImGuiIO io, final long window) {
    if (focused) {
      for (int i = 0; i < ImGuiMouseButton.COUNT; i++) {
        io.setMouseDown(i, glfwGetMouseButton(window, i) != 0);
      }
    }
  }

  private void updateMouseCursor() {
    final ImGuiIO io = ImGui.getIO();

    final boolean noCursorChange = io.hasConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
    final boolean cursorDisabled = glfwGetInputMode(windowPtr, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;

    if (noCursorChange || cursorDisabled) {
      return;
    }

    final int imguiCursor = ImGui.getMouseCursor();
    final ImGuiPlatformIO platformIO = ImGui.getPlatformIO();

    for (int n = 0; n < platformIO.getViewportsSize(); n++) {
      final long window = platformIO.getViewports(n).getPlatformHandle();

      if (imguiCursor == ImGuiMouseCursor.None || io.getMouseDrawCursor()) {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
      } else {
        glfwSetCursor(
            window,
            mouseCursors[imguiCursor] != 0
                ? mouseCursors[imguiCursor]
                : mouseCursors[ImGuiMouseCursor.Arrow]);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
      }
    }
  }

  private void updateMonitors() {
    final ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
    final PointerBuffer monitors = glfwGetMonitors();
    if (null == monitors) throw new IllegalStateException("Could not get GLFW Monitors");

    platformIO.resizeMonitors(0);

    for (int n = 0; n < monitors.limit(); n++) {
      final long monitor = monitors.get(n);

      glfwGetMonitorPos(monitor, monitorX, monitorY);
      final GLFWVidMode vidMode = glfwGetVideoMode(monitor);
      if (null == vidMode) throw new IllegalStateException("Could not get GPU");
      final float mainPosX = monitorX[0];
      final float mainPosY = monitorY[0];
      final float mainSizeX = vidMode.width();
      final float mainSizeY = vidMode.height();

      if (glfwHasMonitorWorkArea) {
        glfwGetMonitorWorkarea(
            monitor,
            monitorWorkAreaX,
            monitorWorkAreaY,
            monitorWorkAreaWidth,
            monitorWorkAreaHeight);
      }

      float workPosX = 0;
      float workPosY = 0;
      float workSizeX = 0;
      float workSizeY = 0;

      if (glfwHasMonitorWorkArea && monitorWorkAreaWidth[0] > 0 && monitorWorkAreaHeight[0] > 0) {
        workPosX = monitorWorkAreaX[0];
        workPosY = monitorWorkAreaY[0];
        workSizeX = monitorWorkAreaWidth[0];
        workSizeY = monitorWorkAreaHeight[0];
      }

      if (glfwHasPerMonitorDpi) {
        glfwGetMonitorContentScale(monitor, monitorContentScaleX, monitorContentScaleY);
      }
      final float dpiScale = monitorContentScaleX[0];

      platformIO.pushMonitors(
          mainPosX, mainPosY, mainSizeX, mainSizeY, workPosX, workPosY, workSizeX, workSizeY,
          dpiScale);
    }

    wantUpdateMonitors = false;
  }
}

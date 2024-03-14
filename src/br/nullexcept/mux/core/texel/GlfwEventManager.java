package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.hardware.CharEvent;
import br.nullexcept.mux.input.InputDevice;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MouseEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.system.libffi.FFICIF;

import java.util.ArrayList;
import java.util.HashMap;

class GlfwEventManager {
    private final HashMap<Integer, GlfwKeyEvent> downKeyEvents = new HashMap<>();
    private final HashMap<Integer, GlfwMouseEvent> downMouseEvents = new HashMap<>();
    private final long gfwWindow;
    private final GlfwWindow window;

    private final Point cursorPosition = new Point(0,0);

    public GlfwEventManager(GlfwWindow window){
        this.window = window;

        long address = window.getAddress();

        GLFW.glfwSetCharCallback(address, (win, charCode) -> window.onCharEvent(new CharEvent((char) charCode,System.nanoTime(),null)));
        GLFW.glfwSetKeyCallback(address, (win, i, i1, i2, i3) -> processKeyEvent(i,i1,i2, i3));
        GLFW.glfwSetMouseButtonCallback(address, (win, button, action, modes) -> processMouseEvent(button, action, modes));
        this.gfwWindow = address;
    }

    private void onMouseEvent(MouseEvent event){
        window.onMouseEvent(event);
    }

    private void onKeyEvent(KeyEvent event){
        window.onKeyEvent(event);
    }

    private void processKeyEvent(int keycode, int scancode, int action, int modifiers) {
        if (action == 0 && !downKeyEvents.containsKey(keycode)){
            return;
        }
        if (!downKeyEvents.containsKey(keycode)){
            downKeyEvents.put(keycode, new GlfwKeyEvent(keycode));
        }

        GlfwKeyEvent keyEvent = downKeyEvents.get(keycode);
        if (action == 0){
            downKeyEvents.remove(keycode);
        }
        onKeyEvent(keyEvent);
    }

    private void processMouseEvent(int button, int action, int modifiers){
        if (action == 0 && !downMouseEvents.containsKey(button)){
            return;
        }
        if (!downMouseEvents.containsKey(button)){
            downMouseEvents.put(button, new GlfwMouseEvent(button));
        }
        GlfwMouseEvent mouseEvent = downMouseEvents.get(button);
        if (action == 0){
            downMouseEvents.remove(button);
        }
        mouseEvent.resetTransform();
        onMouseEvent(mouseEvent);
    }

    public void runFrame(){
        double[][] buffer = new double[2][1];
        GLFW.glfwGetCursorPos(gfwWindow, buffer[0], buffer[1]);

        cursorPosition.x = (int) Math.max(0, buffer[0][0]);
        cursorPosition.y = (int) Math.max(0, buffer[1][0]);

        for (MouseEvent event: downMouseEvents.values()){
            onMouseEvent(event);
        }
    }

    private class GlfwKeyEvent extends KeyEvent {
        private final int keyCode;
        private final long downTime = System.nanoTime();

        public GlfwKeyEvent(int keycode) {
            this.keyCode = keycode;
        }

        @Override
        public int getKeyCode() {
            return keyCode;
        }

        @Override
        public int getAction() {
            return downKeyEvents.containsKey(keyCode) ? KeyEvent.ACTION_DOWN : KeyEvent.ACTION_UP;
        }

        @Override
        public long getDownTime() {
            return downTime;
        }

        @Override
        public InputDevice getSource() {
            return null;
        }
    }

    private class GlfwMouseEvent extends MouseEvent {
        private final long downTime;
        private final int button;

        private GlfwMouseEvent(int button) {
            this.downTime = System.nanoTime();
            this.button = button;
        }

        @Override
        public double getRawX() {
            return cursorPosition.x;
        }

        @Override
        public double getRawY() {
            return cursorPosition.y;
        }

        @Override
        public int getButton() {
            return button;
        }

        @Override
        public int getAction() {
            return downMouseEvents.containsKey(button) ? MouseEvent.ACTION_DOWN : MouseEvent.ACTION_UP;
        }

        @Override
        public long getDownTime() {
            return downTime;
        }

        @Override
        protected void resetTransform() {
            super.resetTransform();
        }

        @Override
        public InputDevice getSource() {
            return null;
        }
    }
}

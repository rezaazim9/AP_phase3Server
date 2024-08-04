package controller;

import model.Profile;
import view.containers.MotionPanelView;
import view.menu.PauseMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

import static controller.UserInputHandler.InputAction.InputActionType.*;
import static controller.UserInterfaceController.*;
import static controller.constants.EntityConstants.SHOTS_PER_SECOND;
import static view.containers.GlassFrame.getGlassFrame;

public final class UserInputHandler {
    private static UserInputHandler INSTANCE;
    public static final InputMap inputMap = new InputMap();
    public static final ActionMap actionMap = new ActionMap();
    public static final String PRESSED = "pressed";
    public static final String RELEASED = "released";
    private boolean moveUpInd;
    private boolean moveDownInd;
    private boolean moveLeftInd;
    private boolean moveRightInd;
    private boolean shootInd;
    private long lastShootingTime = System.nanoTime();
    private long shootTimeDiffCapture = 0;

    private UserInputHandler() {
        inputMap.put(KeyStroke.getKeyStroke(Profile.UP_KEYCODE, 0, false), MOVE_UP + PRESSED);
        actionMap.put(MOVE_UP + PRESSED, new InputAction(MOVE_UP, true));
        inputMap.put(KeyStroke.getKeyStroke(Profile.UP_KEYCODE, 0, true), MOVE_UP + RELEASED);
        actionMap.put(MOVE_UP + RELEASED, new InputAction(MOVE_UP, false));

        inputMap.put(KeyStroke.getKeyStroke(Profile.DOWN_KEYCODE, 0, false), MOVE_DOWN + PRESSED);
        actionMap.put(MOVE_DOWN + PRESSED, new InputAction(MOVE_DOWN, true));
        inputMap.put(KeyStroke.getKeyStroke(Profile.DOWN_KEYCODE, 0, true), MOVE_DOWN + RELEASED);
        actionMap.put(MOVE_DOWN + RELEASED, new InputAction(MOVE_DOWN, false));

        inputMap.put(KeyStroke.getKeyStroke(Profile.LEFT_KEYCODE, 0, false), MOVE_LEFT + PRESSED);
        actionMap.put(MOVE_LEFT + PRESSED, new InputAction(MOVE_LEFT, true));
        inputMap.put(KeyStroke.getKeyStroke(Profile.LEFT_KEYCODE, 0, true), MOVE_LEFT + RELEASED);
        actionMap.put(MOVE_LEFT + RELEASED, new InputAction(MOVE_LEFT, false));

        inputMap.put(KeyStroke.getKeyStroke(Profile.RIGHT_KEYCODE, 0, false), MOVE_RIGHT + PRESSED);
        actionMap.put(MOVE_RIGHT + PRESSED, new InputAction(MOVE_RIGHT, true));
        inputMap.put(KeyStroke.getKeyStroke(Profile.RIGHT_KEYCODE, 0, true), MOVE_RIGHT + RELEASED);
        actionMap.put(MOVE_RIGHT + RELEASED, new InputAction(MOVE_RIGHT, false));

        inputMap.put(KeyStroke.getKeyStroke(Profile.PAUSE_KEYCODE, 0, true), PAUSE);
        actionMap.put(PAUSE, new InputAction(PAUSE,true));

        inputMap.put(KeyStroke.getKeyStroke(Profile.SKILL_KEYCODE, 0, true), SKILL);
        actionMap.put(SKILL, new InputAction(SKILL,true));

        getGlassFrame().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {new InputAction(SHOOT).run();}
        });
    }

    public static UserInputHandler getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new UserInputHandler();
        return INSTANCE;
    }

    public static Point getMouseLocation() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    public boolean isMoveUpInd() {
        return moveUpInd;
    }

    public void setMoveUpInd(boolean moveUpInd) {
        this.moveUpInd = moveUpInd;
    }

    public boolean isMoveDownInd() {
        return moveDownInd;
    }

    public void setMoveDownInd(boolean moveDownInd) {
        this.moveDownInd = moveDownInd;
    }

    public boolean isMoveLeftInd() {
        return moveLeftInd;
    }

    public void setMoveLeftInd(boolean moveLeftInd) {
        this.moveLeftInd = moveLeftInd;
    }

    public boolean isMoveRightInd() {
        return moveRightInd;
    }

    public void setMoveRightInd(boolean moveRightInd) {
        this.moveRightInd = moveRightInd;
    }

    public boolean isShootInd() {
        return shootInd;
    }

    public void setShootInd(boolean shootInd) {
        this.shootInd = shootInd;
    }

    public long getLastShootingTime() {
        return lastShootingTime;
    }

    public void setLastShootingTime(long lastShootingTime) {
        this.lastShootingTime = lastShootingTime;
    }

    public void setupInputHandler(MotionPanelView motionPanelView) {
        motionPanelView.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
        motionPanelView.setActionMap(actionMap);
        motionPanelView.requestFocus();
        moveUpInd=false;moveDownInd=false;moveLeftInd=false;moveRightInd=false;
    }

    public long getShootTimeDiffCapture() {
        return shootTimeDiffCapture;
    }

    public void setShootTimeDiffCapture(long shootTimeDiffCapture) {
        this.shootTimeDiffCapture = shootTimeDiffCapture;
    }

    public static class InputAction extends AbstractAction implements Runnable {
        InputActionType inputActionType;
        boolean pressed;

        public InputAction(InputActionType inputActionType, boolean pressed) {
            this.inputActionType = inputActionType;
            this.pressed = pressed;
        }

        public InputAction(InputActionType inputActionType) {
            this.inputActionType = inputActionType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isGameRunning()) {
                switch (inputActionType) {
                    case MOVE_UP -> getINSTANCE().setMoveUpInd(pressed);
                    case MOVE_DOWN -> getINSTANCE().setMoveDownInd(pressed);
                    case MOVE_LEFT -> getINSTANCE().setMoveLeftInd(pressed);
                    case MOVE_RIGHT -> getINSTANCE().setMoveRightInd(pressed);
                    case SHOOT -> run();
                    case PAUSE -> PauseMenu.getINSTANCE().togglePanel();
                    case SKILL -> fireSkill();
                }
            }
        }

        @Override
        public void run() {
            if (this.inputActionType==SHOOT && isGameRunning()) {
                long now = System.nanoTime();
                if (now - getINSTANCE().getLastShootingTime() >= TimeUnit.SECONDS.toNanos(1) / SHOTS_PER_SECOND.getValue()) {
                    getINSTANCE().setShootInd(true);
                    getINSTANCE().setLastShootingTime(now);
                }
            }
        }

        public enum InputActionType {MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, SHOOT, PAUSE, SKILL}
    }
}

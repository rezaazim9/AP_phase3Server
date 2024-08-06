package controller;

import model.Profile;
import model.characters.EpsilonModel;
import model.characters.GeoShapeModel;
import model.collision.Collision;
import model.entities.Ability;
import model.movement.Movable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static model.characters.GeoShapeModel.allShapeModelsList;


public final class GameLoop implements Runnable {
    private static GameLoop INSTANCE = null;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean exit = new AtomicBoolean(false);
    private long updateTimeDiffCapture = 0;
    private long frameTimeDiffCapture = 0;
    private long currentTime;
    private long startTime;
    private long lastFrameTime;
    private long lastUpdateTime;
    private long timeSaveDiffCapture = 0;
    private long timeSave;
    private volatile String info = "";
    private static int PR = 0;

    public static int getPR() {
        return PR;
    }

    public static void setPR(int PR) {
        GameLoop.PR = PR;
    }


    public static void updateView() {

    }

    public static void updateModel() {
        Collision.getINSTANCE().run();

        for (GeoShapeModel model : allShapeModelsList) {
            for (ActionListener actionListener : model.getMovement().getMoveListeners()) {
                if (model.getMovement().getMoveListeners().contains(actionListener)) {
                    actionListener.actionPerformed(new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, null));
                }
            }
        }
    }

    public static GameLoop getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new GameLoop();
        return INSTANCE;
    }

    @Override
    public void run() {
        running.set(true);
        exit.set(false);
        try {
            initializeGame();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AtomicInteger frames = new AtomicInteger(0), ticks = new AtomicInteger(0);
        AtomicFloat deltaU = new AtomicFloat(0), deltaF = new AtomicFloat(0);
        currentTime = System.nanoTime();
        startTime = System.nanoTime();
        lastFrameTime = currentTime;
        lastUpdateTime = currentTime;
        timeSave = currentTime;
        float timePerFrame = (float) TimeUnit.SECONDS.toNanos(1) / Profile.getCurrent().getFps();
        float timePerUpdate = (float) TimeUnit.SECONDS.toNanos(1) / Profile.getCurrent().getUps();
        while (!exit.get()) {
            if (running.get()) {
                currentTime = System.nanoTime();
                gameLoopCycle(frames, ticks, deltaF, deltaU, timePerFrame, timePerUpdate);
            }
        }
    }

    public void gameLoopCycle(AtomicInteger frames, AtomicInteger ticks, AtomicFloat deltaF, AtomicFloat deltaU, float timePerFrame, float timePerUpdate) {
        if (deltaU.get() >= 1) {
            updateModel();
            ticks.addAndGet(1);
            deltaU.addAndGet(-1);
        }
        if (deltaF.get() >= 1) {
            updateView();
            frames.addAndGet(1);
            deltaF.addAndGet(-1);
        }
        if (currentTime - lastFrameTime > timePerFrame) {
            deltaF.addAndGet((currentTime - lastFrameTime - timePerFrame) / timePerFrame);
            updateView();
            frames.addAndGet(1);
            lastFrameTime = currentTime;
        }
        if (currentTime - lastUpdateTime > timePerUpdate) {
            deltaU.addAndGet((currentTime - lastUpdateTime - timePerUpdate) / timePerUpdate);
            updateModel();
            ticks.addAndGet(1);
            lastUpdateTime = currentTime;
        }
        if (currentTime - timeSave >= TimeUnit.SECONDS.toNanos(1)) {
            long time = (currentTime - startTime) / 1000000000;
            StringBuilder abilities = new StringBuilder();
            for (Ability ability : Ability.values()) {
                if (UserInterfaceController.canActiveAbility(ability.getName())) {
                    abilities.append(" <br/>").append(ability.getName());
                }
            }
            info = "<html>PR:" + PR   +" <br/>FPS:" + frames + " <br/>UPS:" + ticks + " <br/>XP:" + Profile.getCurrent().getCurrentGameXP() + " <br/>HP:" + EpsilonModel.getINSTANCE().getHealth() + " <br/>Wave:" + " <br/>Time:" + time + " <br/>Abilities:" + abilities + "</html>";
            frames.set(0);
            ticks.set(0);
            timeSave = currentTime;
        }
    }

    public void initializeGame() throws InterruptedException {
    }

    public void forceExitGame() {
        exit.set(true);
    }


    public void toggleGameLoop() {

            if (running.get()) {
                long now = System.nanoTime();
                updateTimeDiffCapture = now - lastUpdateTime;
                frameTimeDiffCapture = now - lastFrameTime;
                timeSaveDiffCapture = now - timeSave;
                UserInputHandler.getINSTANCE().setShootTimeDiffCapture(now - UserInputHandler.getINSTANCE().getLastShootingTime());
                for (Movable movable : Movable.movables)
                    movable.setPositionUpdateTimeDiffCapture(now - movable.getLastPositionUpdateTime());
            }
            if (!running.get()) {
                currentTime = System.nanoTime();
                lastUpdateTime = currentTime - updateTimeDiffCapture;
                lastFrameTime = currentTime - frameTimeDiffCapture;
                timeSave = currentTime - timeSaveDiffCapture;
                UserInputHandler.getINSTANCE().setLastShootingTime(currentTime - UserInputHandler.getINSTANCE().getShootTimeDiffCapture());
                for (Movable movable : Movable.movables)
                    movable.setLastPositionUpdateTime(currentTime - movable.getPositionUpdateTimeDiffCapture());
            }

    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean isOn() {
        return !exit.get();
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }
}

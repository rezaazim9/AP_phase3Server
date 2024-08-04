package model;

import controller.GameLoop;

import model.characters.*;
import model.movement.Movable;
import view.menu.MainMenu;
import view.menu.PauseMenu;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static controller.UserInterfaceController.*;
import static controller.constants.MovementConstants.*;
import static model.characters.GeoShapeModel.allShapeModelsList;
import static model.collision.Collidable.collidables;
import static view.menu.MainMenu.spawn;

public class WaveManager {
    public static List<GeoShapeModel> waveEntities = new CopyOnWriteArrayList<>();
    public static final Random random = new Random();
    public static int wave=Profile.getCurrent().getWave();
    public static int PR = 0;
    private long waveStart = System.nanoTime();
    private long waveFinish;
    public static int killedEnemies = 0;



    public void start() {
        initiateWave(Profile.getCurrent().getWave());
    }


    public int progressRateTotalWave() {
        return (int) (wave * (waveFinish - waveStart) / 1000000000);
    }

    public int progressRisk(int p) {
        Profile currentProfile = Profile.getCurrent();
        EpsilonModel epsilonModel = EpsilonModel.getINSTANCE();
        if (currentProfile != null && epsilonModel != null) {
            if (epsilonModel.getHealth() != 0) {
                return (10 * currentProfile.getCurrentGameXP() * p / epsilonModel.getHealth());
            }
        }
        return 0;
    }

    public void initialPortal() {
        int x = (int) EpsilonModel.getINSTANCE().getAnchor().getX();
        int y = (int) EpsilonModel.getINSTANCE().getAnchor().getY();
        while (Math.sqrt(Math.pow((x - EpsilonModel.getINSTANCE().getAnchor().getX()), 2) + Math.pow((y - EpsilonModel.getINSTANCE().getAnchor().getY()), 2)) < 250) {
            x = random.nextInt((int) EpsilonModel.getINSTANCE().getAnchor().getX() - 200, (int) EpsilonModel.getINSTANCE().getAnchor().getX() + 200);
            y = random.nextInt((int) EpsilonModel.getINSTANCE().getAnchor().getY() - 200, (int) EpsilonModel.getINSTANCE().getAnchor().getY() + 200);
        }
        new PortalModel(getMainMotionPanelId(), new Point(x, y));
    }


    private void initiateWave(int wave) {
        if (wave != Profile.getCurrent().getWave()) {
            initialPortal();
        }
        GameLoop.setWaveStart(System.nanoTime());
        waveFinish = System.nanoTime();
        PR += progressRisk(progressRateTotalWave());
        waveStart = System.nanoTime();
        Timer waveTimer = new Timer((100), null);
        waveTimer.addActionListener(e -> {
            boolean waveFinished = false;
            if (killedEnemies - 1 > wave) {
                waveFinished = true;
                killedEnemies = 0;
            }
            if (waveFinished) {
                WaveManager.wave++;
                if (waveEntities != null) {
                    waveTimer.stop();
                    for (GeoShapeModel shapeModel : waveEntities) {
                        allShapeModelsList.remove(shapeModel);
                        collidables.remove(shapeModel);
                        Movable.movables.remove(shapeModel);
                        eliminateView(shapeModel.getModelId(), shapeModel.getMotionPanelId());
                    }
                    waveEntities.clear();
                }
                float length = showMessage(4 - WaveManager.wave);
                if (WaveManager.wave < 4) initiateWave(WaveManager.wave + 1);
                else {
                    Profile.getCurrent().setWave(0);
                    Profile.getCurrent().setPaused(true);
                    finishGame(length);
                }
            }
        });
        waveTimer.start();
    }
    public static void finishGame(float lastSceneTime) {
        Timer timer = new Timer((int) TimeUnit.NANOSECONDS.toMillis((long) lastSceneTime), e -> {
            GameLoop.setPR(0);
            Profile.getCurrent().saveXP();
            exitGame();
            spawn.interrupt();
            PauseMenu.getINSTANCE().togglePanel(true);
            MainMenu.flushINSTANCE();
            MainMenu.getINSTANCE().togglePanel();
        });
        timer.setRepeats(false);
        timer.start();
    }


    public void stopEnemies() {
        for (GeoShapeModel shapeModel : waveEntities) {
            shapeModel.getMovement().setSpeed(0);
            shapeModel.getMovement().setAngularSpeed(0);
        }
    }

    public void releaseEnemies() {
        for (GeoShapeModel shapeModel : waveEntities) {
            shapeModel.getMovement().setSpeed(shapeModel.getMovement().getSpeedSave());
            shapeModel.getMovement().setAngularSpeed(random.nextFloat(-ANGULAR_SPEED_BOUND.getValue(), ANGULAR_SPEED_BOUND.getValue()));
        }
    }
}

package model;


import model.characters.*;
import model.movement.Direction;

import java.awt.*;

import static controller.AudioHandler.random;
import static controller.UserInterfaceController.*;
import static controller.constants.WaveConstants.MAX_ENEMY_SPAWN_RADIUS;
import static controller.constants.WaveConstants.MIN_ENEMY_SPAWN_RADIUS;
import static main.java.model.Utils.*;

public class SpawnThread extends Thread {
    int i = 0;

    public void setRunning(boolean running) {
        this.running = running;
    }

    private boolean running = true;


    @Override
    public void run() {
        while (true) {
            if (running) {
                try {
                    sleep((int) (2700 / (Math.pow(WaveManager.wave + 1, 0.2))));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println(i++);
                Point location = roundPoint(addUpPoints(EpsilonModel.getINSTANCE().getAnchor(),
                        multiplyPoint(new Direction(random.nextFloat(0, 360)).getDirectionVector(),
                                random.nextFloat(MIN_ENEMY_SPAWN_RADIUS.getValue(), MAX_ENEMY_SPAWN_RADIUS.getValue()))));
                GeoShapeModel model;

                if (WaveManager.wave == 0) {
                    model = new SquarantineModel(location, getMainMotionPanelId());
                } else {
                    model = switch (random.nextInt(0, 2)) {
                        case 0 -> new SquarantineModel(location, getMainMotionPanelId());
                        case 1 -> new TrigorathModel(location, getMainMotionPanelId());
                        default -> null;
                    };
                }
                if (model != null) {
                    WaveManager.waveEntities.add(model);
                    model.getMovement().lockOnTarget(EpsilonModel.getINSTANCE().getModelId());
                }

            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

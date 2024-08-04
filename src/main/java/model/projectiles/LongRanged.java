package model.projectiles;

import model.characters.GeoShapeModel;
import model.collision.Collidable;
import model.movement.Direction;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;

import static controller.UserInterfaceController.playShootSoundEffect;
import static controller.constants.EntityConstants.EPSILON_RAPID_SHOOTING_DELAY;
import static model.Utils.roundPoint;

public interface LongRanged extends Collidable {
    String getMotionPanelId();

    int getShootingRapidity();

    void setShootingRapidity(int shootingRapidity);

    default void shoot(GeoShapeModel shooter, Direction direction, int damage) {
        AtomicInteger cnt = new AtomicInteger();
        Timer rapidFire = new Timer(EPSILON_RAPID_SHOOTING_DELAY.getValue(), null);
        rapidFire.setCoalesce(true);
        rapidFire.addActionListener(e -> {
            new BulletModel(roundPoint(getAnchor()), getMotionPanelId(), damage) {
                @Override
                public boolean collide(Collidable collidable) {
                    return !(collidable instanceof BulletModel) && collidable != shooter;
                }
            }.getMovement().setDirection(direction);
            playShootSoundEffect();
            cnt.getAndIncrement();
            if (cnt.get() == getShootingRapidity()) rapidFire.stop();
        });
        rapidFire.start();
    }
}

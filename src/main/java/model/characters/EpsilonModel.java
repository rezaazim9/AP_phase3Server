package model.characters;

import controller.GameLoop;
import controller.UserInputHandler;
import controller.constants.AbilityConstants;
import model.Profile;
import model.collision.Collidable;
import model.entities.AttackTypes;
import model.movement.Direction;
import model.projectiles.LongRanged;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.concurrent.TimeUnit;

import static controller.UserInputHandler.getMouseLocation;
import static controller.UserInterfaceController.*;
import static controller.constants.EntityConstants.EPSILON_HEALTH;
import static controller.constants.EntityConstants.EPSILON_SHOOTING_RAPIDITY;
import static controller.constants.EntityConstants.EntityVertices.EPSILON_VERTICES;
import static controller.constants.MovementConstants.EPSILON_SPEED;


public final class EpsilonModel extends GeoShapeModel implements LongRanged {
    private static EpsilonModel INSTANCE;
    private boolean moveUpIndSave;
    private boolean moveDownIndSave;
    private boolean moveLeftIndSave;
    private boolean moveRightIndSave;
    private int shootingRapidity = EPSILON_SHOOTING_RAPIDITY.getValue();

    private EpsilonModel(String motionPanelId) {
        super(new Point(0, 0), EPSILON_VERTICES.getValue(), EPSILON_HEALTH.getValue());
        this.setCircular(true);
        setMotionPanelId(motionPanelId);
        Point2D anchor = getMotionPanelCenterLocation(getMainMotionPanelId());
        moveShapeModel(anchor);
        getMovement().setAnchor(anchor);
        getDamageSize().put(AttackTypes.MELEE, Profile.getCurrent().getEpsilonMeleeDamage());
        getDamageSize().put(AttackTypes.RANGED, Profile.getCurrent().getEpsilonRangedDamage());
        getDamageSize().put(AttackTypes.COLLISION, Profile.getCurrent().getEpsilonCollisionDamage());
        activateMovement();
    }

    public static EpsilonModel getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new EpsilonModel(getMainMotionPanelId());
        return INSTANCE;
    }

    public static void flushINSTANCE() {
        INSTANCE = null;
    }

    @Override
    public void eliminate() {
        super.eliminate();
        Timer timer=new Timer((int) TimeUnit.NANOSECONDS.toMillis((long) showMessage()), e -> {
            GameLoop.setPR(0);
            Profile.getCurrent().setCurrentGameXP(0);
            exitGame();
          });
        timer.setRepeats(false);
        timer.start();
    }

    public void deactivateMovement() {
        getMovement().getMoveListeners().clear();
        getMovement().setAngularSpeed(0);
        getMovement().setSpeed(0);
        getMovement().setSpeedSave(0);
    }
    public void activateMovement() {
        getMovement().setAngularSpeed(0);
        getMovement().setSpeed(EPSILON_SPEED.getValue());
        getMovement().setSpeedSave(EPSILON_SPEED.getValue());

        getMovement().getMoveListeners().clear();
        getMovement().getMoveListeners().add(e -> {

            Direction downDirection=new Direction(0, Direction.DirectionOrientation.POSITIVE);
            downDirection.setDownside(true);
            getMovement().setDirection(downDirection);

            if (UserInputHandler.getINSTANCE().isMoveUpInd()) {
                moveUpIndSave = true;
                getMovement().dampenDecelerations();
                getMovement().updateAnchor();
            } else if (moveUpIndSave) {
                moveUpIndSave = false;
                getMovement().decelerate(new Direction(getMovement().getDirection()));
            }

            Direction upDirection=new Direction(0, Direction.DirectionOrientation.POSITIVE);
            upDirection.setUpside(true);
            getMovement().setDirection(upDirection);

            if (UserInputHandler.getINSTANCE().isMoveDownInd()) {
                moveDownIndSave = true;
                getMovement().dampenDecelerations();
                getMovement().updateAnchor();
            } else if (moveDownIndSave) {
                moveDownIndSave = false;
                getMovement().decelerate(new Direction(getMovement().getDirection()));
            }

            getMovement().setDirection(new Direction(0, Direction.DirectionOrientation.NEGATIVE));
            if (UserInputHandler.getINSTANCE().isMoveLeftInd()) {
                moveLeftIndSave = true;
                getMovement().dampenDecelerations();
                getMovement().updateAnchor();
            } else if (moveLeftIndSave) {
                moveLeftIndSave = false;
                getMovement().decelerate(new Direction(getMovement().getDirection()));
            }

            getMovement().setDirection(new Direction(0, Direction.DirectionOrientation.POSITIVE));
            if (UserInputHandler.getINSTANCE().isMoveRightInd()) {
                moveRightIndSave = true;
                getMovement().dampenDecelerations();
                getMovement().updateAnchor();
            } else if (moveRightIndSave) {
                moveRightIndSave = false;
                getMovement().decelerate(new Direction(getMovement().getDirection()));
            }
            getMovement().move();

            Point mouseLocation = getMouseLocation();
          if (UserInputHandler.getINSTANCE().isShootInd()) {
                if (getDamageSize().get(AttackTypes.RANGED) == AbilityConstants.PHONOI_ABILITY_RANGED_DAMAGE.getValue()) {
                    getDamageSize().put(AttackTypes.RANGED, Profile.getCurrent().getEpsilonRangedDamage());
                }
                UserInputHandler.getINSTANCE().setShootInd(false);
            }

        });
    }

    public void healEpsilon() {
        addHealth(Profile.getCurrent().getEpsilonHealingAmount());
    }

    @Override
    public boolean collide(Collidable collidable) {
        return true;
    }

    @Override
    public int getShootingRapidity() {
        return shootingRapidity;
    }

    @Override
    public void setShootingRapidity(int shootingRapidity) {
        this.shootingRapidity = shootingRapidity;
    }
}

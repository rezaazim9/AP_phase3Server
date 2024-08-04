package model.movement;

import controller.TypedActionListener;
import model.characters.GeoShapeModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.UserInterfaceController.findModel;
import static controller.constants.ImpactConstants.*;
import static controller.constants.MovementConstants.*;
import static model.Utils.*;

@SuppressWarnings("FieldMayBeFinal")
public class Movement {
    private final List<ActionListener> moveListeners = new CopyOnWriteArrayList<>();
    private Point2D lastAnchor = new Point2D.Float(0, 0);
    private long lastAnchorUpdateTime = System.nanoTime();
    private long positionUpdateTimeDiffCapture = 0;
    private Point2D anchor = new Point2D.Float(0, 0);
    private Direction direction = new Direction(0, Direction.DirectionOrientation.STABLE);
    private float angularSpeed = random.nextFloat(-ANGULAR_SPEED_BOUND.getValue(), ANGULAR_SPEED_BOUND.getValue());
    private float speedSave = DEFAULT_SPEED.getValue();
    private float speed = getSpeedSave();
    private float deceleration = DEFAULT_DECELERATION.getValue();
    private float decay = DECELERATION_DECAY.getValue();
    private String modelId;
    private Point2D target = null;
    private String targetModelId = null;
    private static final Random random=new Random();

    public Movement(String modelId, Point2D anchor) {
        this.setModelId(modelId);
        setAnchor(anchor);
        moveListeners.add(new TypedActionListener(TypedActionListener.ActionListenerType.MOVE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAnchor();
                move();
            }
        });
        moveListeners.add(new TypedActionListener(TypedActionListener.ActionListenerType.ROTATE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRotation();
                rotate();
            }
        });
    }

    public void move() {
        GeoShapeModel model = findModel(getModelId());
        if (model == null) return;
        model.moveShapeModel(getAnchor());
    }

    public void updateAnchor(float speed, Direction direction) {
        if (getTargetModelId() != null) lockOnTarget(getTargetModelId());
        else if (getTarget() != null) lockOnTarget(getTarget());
        GeoShapeModel model = findModel(getModelId());
        if (model == null) return;
        Point2D destination = addUpPoints(getAnchor(), multiplyPoint(direction.getDirectionVector(), speed));
        if (!model.crossesUnmovable(destination)) setAnchor(destination);
    }

    public void updateAnchor() {
        updateAnchor(getSpeed(), getDirection());
    }

    public void updateRotation() {
        this.setAngularSpeed(this.getAngularSpeed() * DEFAULT_ANGULAR_DECAY.getValue());
    }

    public void rotate() {
        GeoShapeModel model = findModel(getModelId());
        if (model == null) return;
        model.rotateShapeModel(getAngularSpeed());
    }

    public void decelerate(Direction direction, float tempSpeed, float tempAcceleration) {
        moveListeners.removeIf(actionListener -> actionListener instanceof DecelerationWorker decelerationWorker && decelerationWorker.direction.equals(direction));
        moveListeners.add(new DecelerationWorker(direction, tempSpeed, tempAcceleration) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (speed > DECELERATION_SENSITIVITY.getValue()) {
                    speed += acceleration;
                    acceleration = acceleration >= 0 ? acceleration * getDecay() : acceleration / getDecay();
                    updateAnchor(speed, direction);
                } else moveListeners.remove(this);
            }
        });
    }

    public void decelerate(Direction direction) {
        decelerate(direction, getSpeed(), getDeceleration());
    }

    public void impact(Direction direction, float tempSpeed, float tempAcceleration, float scale) {
        moveListeners.removeIf(DecelerationWorker.class::isInstance);
        moveListeners.removeIf(actionListener -> actionListener instanceof TypedActionListener typedAL && typedAL.getType()== TypedActionListener.ActionListenerType.IMPACT);
        setSpeed(getSpeedSave());
        final Movement[] finalMovement = {this};
        final float[] finalTempSpeed = {tempSpeed * scale};
        final float[] finalTempAcc = {tempAcceleration * scale};
        moveListeners.add(new TypedActionListener(TypedActionListener.ActionListenerType.IMPACT) {
            boolean speedDecreased = false;
            long timeDecreased = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!speedDecreased) {
                    finalMovement[0].setSpeed(finalMovement[0].getSpeed() / Math.max(IMPACT_DRIFT_THRESHOLD.getValue(), IMPACT_DRIFT_FACTOR.getValue() * scale));
                    speedDecreased = true;
                    timeDecreased = System.nanoTime();
                } else if (System.nanoTime() - timeDecreased > IMPACT_COOLDOWN.getValue()) {
                    finalMovement[0].setSpeed(finalMovement[0].getSpeedSave());
                }
                if (finalTempSpeed[0] > DECELERATION_SENSITIVITY.getValue()) {
                    finalTempSpeed[0] += finalTempAcc[0];
                    finalTempAcc[0] = finalTempAcc[0] >= 0 ? finalTempAcc[0] * getDecay() : finalTempAcc[0] / getDecay();
                    updateAnchor(finalTempSpeed[0], direction);
                } else {
                    moveListeners.remove(this);
                }
            }
        });
    }

    public void impact(Direction direction, float scale) {
        impact(direction, IMPACT_SPEED.getValue(), IMPACT_DECELERATION.getValue(), scale);
    }

    public void lockOnTarget(Point2D point) {
        if (point != null) {
            setTarget(point);
            setDirection(new Direction(relativeLocation(point, getAnchor())));
        }
    }

    public void lockOnTarget(String targetModelId) {
        this.setTargetModelId(targetModelId);
        GeoShapeModel model = findModel(targetModelId);
        if (model != null) setTarget(model.getMovement().getAnchor());
        lockOnTarget(getTarget());
    }

    public void dampenDecelerations() {
        for (ActionListener actionListener : moveListeners) {
            if (actionListener instanceof DecelerationWorker decelerationWorker) {
                decelerationWorker.acceleration *= decelerationWorker.acceleration >= 0 ? 1 / DAMPEN_FACTOR.getValue() : DAMPEN_FACTOR.getValue();
            }
        }
    }

    public List<ActionListener> getMoveListeners() {
        return moveListeners;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Point2D getAnchor() {
        if (anchor == null) anchor = new Point2D.Float(0, 0);
        return anchor;
    }

    public void setAnchor(Point2D point) {
        long now = System.nanoTime();
        if (now - getLastAnchorUpdateTime() >= POSITION_UPDATE_INTERVAL.getValue()) {
            setLastAnchorUpdateTime(now);
            this.setLastAnchor(deepClone(anchor));
        }
        this.anchor = new Point2D.Float((float) point.getX(), (float) point.getY());
    }

    public Point2D getLastAnchor() {
        return lastAnchor;
    }

    public void setLastAnchor(Point2D lastAnchor) {
        this.lastAnchor = lastAnchor;
    }

    public long getLastAnchorUpdateTime() {
        return lastAnchorUpdateTime;
    }

    public void setLastAnchorUpdateTime(long lastAnchorUpdateTime) {
        this.lastAnchorUpdateTime = lastAnchorUpdateTime;
    }

    public long getPositionUpdateTimeDiffCapture() {
        return positionUpdateTimeDiffCapture;
    }

    public void setPositionUpdateTimeDiffCapture(long positionUpdateTimeDiffCapture) {
        this.positionUpdateTimeDiffCapture = positionUpdateTimeDiffCapture;
    }

    public Direction getDirection() {
        return direction;
    }

    public float getAngularSpeed() {
        return angularSpeed;
    }

    public void setAngularSpeed(float angularSpeed) {
        this.angularSpeed = angularSpeed;
    }

    public float getSpeedSave() {
        return speedSave;
    }

    public void setSpeedSave(float speedSave) {
        this.speedSave = speedSave;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDeceleration() {
        return deceleration;
    }

    public float getDecay() {
        return decay;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Point2D getTarget() {
        return target;
    }

    public void setTarget(Point2D target) {
        this.target = target;
    }

    public String getTargetModelId() {
        return targetModelId;
    }

    public void setTargetModelId(String targetModelId) {
        this.targetModelId = targetModelId;
    }

    public abstract static class DecelerationWorker extends TypedActionListener {
        protected Direction direction;
        protected float speed;
        protected float acceleration;

        protected DecelerationWorker(Direction direction, float speed, float acceleration) {
            super(ActionListenerType.DECELERATE);
            this.direction = direction;
            this.speed = speed;
            this.acceleration = acceleration;
        }
    }
}

package model.movement;

import controller.constants.DefaultMethods;

import java.awt.geom.Point2D;

import static controller.constants.ImpactConstants.DIRECTION_SENSITIVITY;
import static model.Utils.*;

public class Direction {
    private final float directionSlope;
    private final DirectionOrientation orientation;
    private boolean isUpside = false;
    private boolean isDownside = false;

    public Direction(float directionSlope, DirectionOrientation orientation) {
        this.directionSlope = directionSlope;
        this.orientation = orientation;
    }

    public Direction(Point2D point) {
        if ((point.getX() == 0 || Math.abs(point.getY() / point.getX()) > 1 / DIRECTION_SENSITIVITY.getValue()) && point.getY() > 0) {
            this.directionSlope = 0;
            this.orientation = DirectionOrientation.POSITIVE;
            isUpside = true;
        } else if ((point.getX() == 0 || Math.abs(point.getY() / point.getX()) > 1 / DIRECTION_SENSITIVITY.getValue()) && point.getY() < 0) {
            this.directionSlope = 0;
            this.orientation = DirectionOrientation.POSITIVE;
            isDownside = true;
        } else if (point.getX() == 0 || Math.abs(point.getY() / point.getX()) > 1 / DIRECTION_SENSITIVITY.getValue()) {
            this.directionSlope = 0;
            this.orientation = DirectionOrientation.STABLE;
        } else {
            this.directionSlope = (float) (point.getY() / point.getX());
            if (point.getX() > 0) this.orientation = DirectionOrientation.POSITIVE;
            else this.orientation = DirectionOrientation.NEGATIVE;
        }
    }

    public Direction(float angle) {
        this(new Direction(new Point2D.Float((float) DefaultMethods.cosTable[(int) validateAngle(angle)], (float) DefaultMethods.sinTable[(int) validateAngle(angle)])));
    }
    public Direction(Direction orientation){
        this.isUpside = orientation.isUpside;
        this.isDownside = orientation.isDownside;
        this.orientation = orientation.orientation;
        this.directionSlope = orientation.directionSlope;
    }

    public Point2D getDirectionVector() {
        if (orientation == DirectionOrientation.STABLE) return new Point2D.Float(0, 0);
        if (isDownside) return new Point2D.Float(0, -1);
        if (isUpside) return new Point2D.Float(0, 1);

        float normalScale = (float) Math.sqrt(1 / (1 + directionSlope * directionSlope));
        if (orientation == DirectionOrientation.POSITIVE) return multiplyPoint(new Point2D.Float(1, directionSlope), normalScale);
        if (orientation == DirectionOrientation.NEGATIVE) return multiplyPoint(new Point2D.Float(-1, -directionSlope), normalScale);
        return null;
    }

    public void setUpside(boolean upside) {
        isUpside = upside;
    }

    public void setDownside(boolean downside) {
        isDownside = downside;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Direction direction1)) return false;
        return directionSlope==direction1.directionSlope && isUpside==direction1.isUpside && isDownside==direction1.isDownside && orientation.equals(direction1.orientation);
    }

    @Override
    public int hashCode() {
        return getDirectionVector().hashCode();
    }

    public enum DirectionOrientation {
        POSITIVE, NEGATIVE, STABLE
    }
}

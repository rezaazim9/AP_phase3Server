package model.collision;

import model.characters.GeoShapeModel;
import model.movement.Direction;

import java.awt.geom.Point2D;

public class MovementState {
    Direction direction;
    float torque;
    float scale;

    public MovementState(Direction direction, float torque, float scale) {
        this.direction = direction;
        this.torque = torque;
        this.scale = scale;
    }

    public static class ShapeMovementState extends MovementState {
        GeoShapeModel geoShapeModel;

        public ShapeMovementState(GeoShapeModel geoShapeModel, Direction direction, float torque, float scale) {
            super(direction, torque, scale);
            this.geoShapeModel = geoShapeModel;
        }
    }

    public static class CollidableMovementState extends MovementState {
        Collidable collidable;

        public CollidableMovementState(Collidable collidable, Direction direction, float torque, float scale) {
            super(direction, torque, scale);
            this.collidable = collidable;
        }
    }

    public static class CollisionState {
        public final Point2D collisionPoint;
        public final MovementState.CollidableMovementState stateOf1;
        public final MovementState.CollidableMovementState stateOf2;

        public CollisionState(Point2D point) {
            this.collisionPoint = point;
            this.stateOf1 = null;
            this.stateOf2 = null;
        }

        public CollisionState(Point2D collisionPoint, MovementState.CollidableMovementState stateOf1, MovementState.CollidableMovementState stateOf2) {
            this.collisionPoint = collisionPoint;
            this.stateOf1 = stateOf1;
            this.stateOf2 = stateOf2;
        }

    }
}

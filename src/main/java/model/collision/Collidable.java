package model.collision;

import controller.constants.DefaultMethods;
import model.movement.Direction;
import model.movement.Translatable;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.operation.distance.DistanceOp;

import java.awt.geom.Point2D;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.constants.ImpactConstants.COLLISION_SENSITIVITY;
import static controller.constants.ImpactConstants.DETECTION_SENSITIVITY;
import static controller.constants.MovementConstants.MAX_SAFE_ROTATION;
import static model.Utils.*;

public interface Collidable {

    CopyOnWriteArrayList<Collidable> collidables = new CopyOnWriteArrayList<>();

    /**
     * Creates current geometry for all the collidables
     */
    static void CreateAllGeometries() {
        for (Collidable collidable : collidables) collidable.createGeometry();
    }

    static Coordinate getClosestCoordinate(Coordinate anchor, Geometry geometry) {
        if (geometry.getCoordinates().length == 0) return anchor;
        return DistanceOp.nearestPoints(geometry, new GeometryFactory().createLineString(new Coordinate[]{anchor, anchor}))[0];
    }

    void createGeometry();

    Geometry getGeometry();

    boolean isCircular();

    float getRadius();

    Point2D getAnchor();

    Point2D getLastAnchor();

    float getSpeed();

    /**
     * <p>Note: Collisions only occur if both collidables agree on it
     *
     * @param collidable second collidable of the collision
     * @return whether {@code this} agrees on the collision
     */
    boolean collide(Collidable collidable);

    /**
     * <p>Note : this method is called very frequently and thus should be very fast</p>
     *
     * @param collidable a Collidable instance to check collision with
     * @return a {@link MovementState.CollisionState} of the collision of objects. Returns {@code null} if objects don't collide
     * @see #resolveCollisionEffect(Collidable, Point2D)
     * @see #getTangentDirection(Coordinate)
     */
    default MovementState.CollisionState checkCollision(Collidable collidable) {
        if (!collide(collidable) || !collidable.collide(this)) return null;
        Point2D collisionPoint;
        if (isCircular() && collidable.isCircular()) collisionPoint=checkCircularCircularCollision(collidable);
        else if (isCircular() && !collidable.isCircular()) collisionPoint=checkCircularNonCircularCollision(collidable);
        else if (!isCircular() && collidable.isCircular()) return collidable.checkCollision(this);
        else collisionPoint=checkNonCircularNonCircularCollision(collidable);
        return analyzeMotion(collidable, collisionPoint);
    }
    default Point2D checkCircularCircularCollision(Collidable collidable){
        if (getAnchor().distance(collidable.getAnchor()) <= (getRadius() + collidable.getRadius()) + COLLISION_SENSITIVITY.getValue()) {
            return weightedAddPoints(getAnchor(), collidable.getAnchor(), collidable.getRadius(), getRadius());
        } else return null;
    }
    default Point2D checkCircularNonCircularCollision(Collidable collidable){
        Coordinate closest = getClosestCoordinate(toCoordinate(getAnchor()), collidable.getGeometry());
        if (closest.distance(toCoordinate(getAnchor())) <= getRadius() + COLLISION_SENSITIVITY.getValue()) return toPoint(closest);
        else return null;
    }
    default Point2D checkNonCircularNonCircularCollision(Collidable collidable){
        //Weird bug, probably due to runtime flow
        if (getGeometry().getCoordinates().length==0 || collidable.getGeometry().getCoordinates().length==0) return null;
        Coordinate[] coordinates = DistanceOp.nearestPoints(getGeometry(), (collidable.getGeometry()));
        LineSegment segment = new LineSegment(coordinates[0], coordinates[1]);
        if (segment.getLength() <= COLLISION_SENSITIVITY.getValue()) return toPoint(segment.midPoint());
        else return null;
    }
    default MovementState.CollisionState analyzeMotion(Collidable collidable, Point2D collisionPoint) {
        if (collisionPoint==null) return null;
        Direction direction1 = new Direction(new Point2D.Float(0, 0));
        Direction direction2 = new Direction(new Point2D.Float(0, 0));
        float torque1 = 0;
        float torque2 = 0;
        float scale1 = 1;
        float scale2 = 1;
        if (this instanceof Translatable) {
            LineSegment tangentLine = collidable.getTangentDirection(toCoordinate(collisionPoint));
            scale1 = calculateCollisionScale(tangentLine, collisionPoint);
            direction1 = resolveCollisionEffect(collidable, collisionPoint);
            torque1 = isCircular() ? 0 : -crossProduct(relativeLocation(collisionPoint, getAnchor()),
                    multiplyPoint(direction1.getDirectionVector(), getSpeed()));

        }
        if (collidable instanceof Translatable) {
            LineSegment tangentLine = getTangentDirection(toCoordinate(collisionPoint));
            scale2 = collidable.calculateCollisionScale(tangentLine, collisionPoint);
            direction2 = collidable.resolveCollisionEffect(this, collisionPoint);
            torque2 = collidable.isCircular() ? 0 : -crossProduct(relativeLocation(collisionPoint, collidable.getAnchor()),
                    multiplyPoint(direction2.getDirectionVector(), collidable.getSpeed()));
        }
        torque1 = Math.abs(torque1) >= MAX_SAFE_ROTATION.getValue() ? (MAX_SAFE_ROTATION.getValue() * Math.signum(torque1)) : torque1;
        torque2 = Math.abs(torque2) >= MAX_SAFE_ROTATION.getValue() ? (MAX_SAFE_ROTATION.getValue() * Math.signum(torque2)) : torque2;

        MovementState.CollidableMovementState stateOfCollidable1=new MovementState.CollidableMovementState(this,direction1,torque1,scale1);
        MovementState.CollidableMovementState stateOfCollidable2=new MovementState.CollidableMovementState(collidable,direction2,torque2,scale2);
        return new MovementState.CollisionState(collisionPoint,stateOfCollidable1,stateOfCollidable2);
    }

    /**
     * @return a vertex of the geometry if the given {@code coordinate} matches that vertex. Returns {@code null} otherwise
     */
    default Coordinate isGeometryVertex(Coordinate coordinate) {
        float minDistance = Float.MAX_VALUE;
        Coordinate out = null;
        for (Coordinate vertex : getGeometry().getCoordinates()) {
            float tempDistance = (float) vertex.distance(coordinate);
            if (tempDistance < DETECTION_SENSITIVITY.getValue() && tempDistance < minDistance) {
                minDistance = tempDistance;
                out = vertex;
            }
        }
        return out;
    }

    /**
     * @return magnitude of impact force based on the angle of collision
     */
    default float calculateCollisionScale(LineSegment surface, Point2D collisionPoint) {
        Point2D.Float point1 = (Point2D.Float) relativeLocation(getAnchor(), collisionPoint);
        Point2D.Float point2 = (Point2D.Float) relativeLocation(toPoint(surface.getCoordinate(0)), collisionPoint);
        float angle1 = calculateAngle(point1);
        float angle2 = calculateAngle(point2);
        return (float) Math.abs(DefaultMethods.sinTable[(int) validateAngle(angle2-angle1)]);
    }

    /**
     * @return the direction of motion after the collision based on the collision details
     */
    default Direction resolveCollisionEffect(Collidable collidable, Point2D collisionPoint) {
        LineSegment surface = collidable.getTangentDirection(toCoordinate(collisionPoint));
        Point2D movementVector = addUpPoints(getMovementVector(collisionPoint), collidable.getMovementVector(collisionPoint));
        Point2D collidableMovementVector = collidable.getMovementVector(collisionPoint);
        Point2D anchor = relativeLocation(getAnchor(), collisionPoint);

        Point2D tangentLineDirection = new Direction(relativeLocation(toPoint(surface.getCoordinate(0)), collisionPoint)).getDirectionVector();
        tangentLineDirection = new Point2D.Double(tangentLineDirection.getY(), -tangentLineDirection.getX());
        boolean anchorSide = dotProduct(tangentLineDirection, anchor) > 0;
        boolean movementSidesWithAnchor = dotProduct(tangentLineDirection, movementVector) > 0 == anchorSide;
        boolean collidableMovementSidesWithAnchor = dotProduct(tangentLineDirection, collidableMovementVector) > 0 == anchorSide;
        Direction out;
        if (movementSidesWithAnchor && collidableMovementSidesWithAnchor) out = new Direction(addUpPoints(movementVector, collidableMovementVector));
        else if (!movementSidesWithAnchor) out = reflectToSurface(surface, collisionPoint);
        else out = new Direction(relativeLocation(getAnchor(), collisionPoint));
        return out;
    }

    /**
     * @param surface        the surface to which collidable is to be reflected
     * @param collisionPoint the collision point of the collidable and the surface
     * @return the reflection direction of this collidable after colliding with the surface in the collision point
     */
    default Direction reflectToSurface(LineSegment surface, Point2D collisionPoint) {
        if (surface == null) return null;
        Point2D.Float trHead1 = (Point2D.Float) relativeLocation(toPoint(surface.getCoordinate(0)), collisionPoint);
        Point2D.Float trHead2 = (Point2D.Float) relativeLocation(toPoint(surface.getCoordinate(1)), collisionPoint);
        float angle1 = calculateAngle(trHead1);
        float angle2 = calculateAngle(trHead2);
        float angleMin = Math.min(angle1, angle2);
        float angleMax = Math.max(angle1, angle2);
        float angleAction = calculateAngle(getMovementVector(collisionPoint));
        float reflectionAngle = angleMin + angleMax - angleAction;
        reflectionAngle = reflectionAngle < 180 ? reflectionAngle + 180 : reflectionAngle - 180;
        return new Direction(reflectionAngle);
    }

    /**
     * Coordinate is supposed to be on the geometry. (appx)
     *
     * @param coordinate a coordinate on geometry where tangent is to be calculated
     * @return if collidable is circular, returns the tangents to the circle. Otherwise, checks if the collision point is close to a vertex.
     * If affirmed, returns the perpendicular to bisector. Otherwise, checks if the collision point is close to an edge. If affirmed, returns
     * the edge itself "UNTIL SHAPES ARE POLYGONS"
     * @throws AssertionError if coordinate is not (appx.) on the geometry, projects (Euclidean) the coordinate on the geometry and calls the method again
     */
    default LineSegment getTangentDirection(Coordinate coordinate) {
        LineSegment out;
        if (isCircular()) out = getTangentToCircle(coordinate);
        else {
            int vertexIndex= getCorrespondingVertexOfBoundaryPoint(coordinate);
            if (vertexIndex != -1) {
                int previous = (vertexIndex != 0) ? vertexIndex - 1 : getGeometry().getCoordinates().length - 2;
                int next = (vertexIndex != getGeometry().getCoordinates().length - 1) ? vertexIndex + 1 : 1;
                Coordinate coordinatePrev = getGeometry().getCoordinates()[previous];
                Coordinate coordinateCur = getGeometry().getCoordinates()[vertexIndex];
                Coordinate coordinateNext = getGeometry().getCoordinates()[next];
                Coordinate diff1 = toCoordinate(relativeLocation(toPoint(coordinatePrev), toPoint(coordinateCur)));
                Coordinate diff2 = toCoordinate(relativeLocation(toPoint(coordinateNext), toPoint(coordinateCur)));
                double sizeDiff1 = diff1.distance(new Coordinate(0, 0));
                double sizeDiff2 = diff2.distance(new Coordinate(0, 0));
                diff1 = new Coordinate(diff1.x / sizeDiff1, diff1.y / sizeDiff1);
                diff2 = new Coordinate(diff2.x / sizeDiff2, diff2.y / sizeDiff2);
                Point2D.Float bisector = new Point2D.Float((float) ((diff1.x + diff2.x) / 2), (float) ((diff1.y + diff2.y) / 2));
                Coordinate head1 = toCoordinate(rotateAbout(bisector, new Point2D.Float(0, 0), 90));
                Coordinate head2 = toCoordinate(rotateAbout(bisector, new Point2D.Float(0, 0), -90));
                head1 = toCoordinate(addUpPoints(toPoint(head1), toPoint(coordinateCur)));
                head2 = toCoordinate(addUpPoints(toPoint(head2), toPoint(coordinateCur)));
                out = new LineSegment(head1, head2);
            }
            else {
                out=getUnderlyingEdgeOfBoundaryPoint(coordinate);
                out = (out == null) ? getTangentDirection(getClosestCoordinate(coordinate, getGeometry())) : out;
            }
        }
        return out;
    }
    default LineSegment getTangentToCircle(Coordinate coordinate){
        Coordinate head1 = toCoordinate(rotateAbout(getAnchor(), toPoint(coordinate), 90));
        Coordinate head2 = toCoordinate(rotateAbout(getAnchor(), toPoint(coordinate), -90));
        return new LineSegment(head1, head2);
    }
    default int getCorrespondingVertexOfBoundaryPoint(Coordinate coordinate){
        int index = -1;
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < getGeometry().getCoordinates().length; i++) {
            float tempDistance = (float) getGeometry().getCoordinates()[i].distance(coordinate);
            if (tempDistance < DETECTION_SENSITIVITY.getValue() / 2F && tempDistance < minDistance) {
                index = i;
                minDistance = tempDistance;
            }
        }
        return index;
    }
    default LineSegment getUnderlyingEdgeOfBoundaryPoint(Coordinate coordinate){
        LineSegment out = null;
        float minEdgeDistance = Float.MAX_VALUE;
        for (int i = 0; i < getGeometry().getCoordinates().length - 1; i++) {
            LineSegment edge = new LineSegment(getGeometry().getCoordinates()[i], getGeometry().getCoordinates()[i + 1]);
            float tempDistance = (float) edge.distance(coordinate);
            if (tempDistance < DETECTION_SENSITIVITY.getValue() && tempDistance < minEdgeDistance) {
                minEdgeDistance = tempDistance;
                out = edge;
            }
        }
        return out;
    }

    default Point2D getMovementVector(Point2D collisionPoint) {
        return relativeLocation(getAnchor(), getLastAnchor());
    }

    default boolean willCross(Collidable collidable, Point2D anchorLocation) {
        Point2D progression = relativeLocation(anchorLocation, getAnchor());
        if (!isCircular()) {
            createGeometry();
            Geometry geometry = getGeometry();
            Coordinate[] newCoordinates = new Coordinate[geometry.getCoordinates().length];
            for (int i = 0; i < geometry.getCoordinates().length; i++)
                newCoordinates[i] = toCoordinate(addUpPoints(progression, toPoint(geometry.getCoordinates()[i])));
            geometry = new GeometryFactory().createLineString(newCoordinates);
            if (!collidable.isCircular()) {
                collidable.createGeometry();
                return geometry.crosses(collidable.getGeometry());
            }
            return collidable.getAnchor().distance(toPoint(getClosestCoordinate(toCoordinate(collidable.getAnchor()), geometry))) > collidable.getRadius();
        } else if (isCircular() && collidable.isCircular()) return getAnchor().distance(collidable.getAnchor()) > getRadius() + collidable.getRadius();
        else {
            collidable.createGeometry();
            return getClosestCoordinate(toCoordinate(anchorLocation), collidable.getGeometry()).distance(toCoordinate(anchorLocation)) < getRadius();
        }
    }

    /**
     * Checks if the collidable overlaps with an unmovable entity
     */
    default boolean crossesUnmovable(Point2D anchorLocation) {
        if (!(this instanceof Translatable)) return false;
        for (Collidable collidable : collidables) {
            boolean shouldCollide = this.collide(collidable) && collidable.collide(this);
            if (!(collidable instanceof Translatable) && shouldCollide && willCross(collidable, anchorLocation)) return true;
        }
        return false;
    }
}

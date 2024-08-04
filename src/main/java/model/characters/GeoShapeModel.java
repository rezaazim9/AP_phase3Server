package model.characters;

import model.collision.Collidable;
import model.entities.Entity;
import model.movement.Movable;
import model.movement.Translatable;
import model.movement.Movement;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.UserInterfaceController.moveGeoShape;
import static controller.UserInterfaceController.rotateGeoShape;
import static controller.constants.DefaultMethods.cosTable;
import static controller.constants.DefaultMethods.sinTable;
import static model.Utils.*;

public class GeoShapeModel extends Entity implements Collidable, Translatable, Movable {
    public static final List<GeoShapeModel> allShapeModelsList = new CopyOnWriteArrayList<>();
    private boolean isCircular;
    public final String modelId;
    private Point2D anchorSave;
    private final List<Point2D> vertices=new CopyOnWriteArrayList<>();
    private final List<Point2D> verticesSave=new CopyOnWriteArrayList<>();
    private final Movement movement;
    private String motionPanelId;
    private float totalRotation = 0;
    private Geometry geometry;
    public GeoShapeModel(Point2D anchor, List<Point2D> vertices, int health) {
        setVerticesSave(vertices);
        this.setFullHealth(health);
        this.setHealth(health);
        this.modelId=UUID.randomUUID().toString();
        setVulnerable(true);
        movement = new Movement(getModelId(), anchor);
        allShapeModelsList.add(this);
        Collidable.collidables.add(this);
        Movable.movables.add(this);
    }

    public void placeVertices(int n) {
        if (isCircular()) {
            CopyOnWriteArrayList<Point2D> newVertices = new CopyOnWriteArrayList<>();
            float step = 360f / n;
            for (int i = 0; i < n; i++) {
                float angleModified = validateAngle(step * i);
                Point2D vertex = new Point2D.Float((float) (getRadius() * cosTable[(int) angleModified]), (float) (getRadius() * sinTable[(int) angleModified]));
                vertex = addUpPoints(vertex, getAnchorSave());
                newVertices.add(vertex);
            }
            setVerticesSave(newVertices);
        }
    }

    public void addVertex() {
        if (isCircular()) placeVertices(verticesSave.size() + 1);
    }

    public void setVerticesSave(List<Point2D> verticesSave) {
        this.vertices.clear();
        this.vertices.addAll(deepCloneList(verticesSave));
        this.verticesSave.clear();
        this.verticesSave.addAll(deepCloneList(verticesSave));
        createGeometry();
    }

    @Override
    public void moveShapeModel(Point2D newAnchor) {
        for (int i = 0; i < verticesSave.size(); i++) getVertices().set(i, addUpPoints(verticesSave.get(i), relativeLocation(newAnchor, getAnchorSave())));
        moveGeoShape(getModelId(), movement.getAnchor());
    }

    @Override
    public void rotateShapeModel(float currentRotation) {
        setTotalRotation(getTotalRotation() - currentRotation);
        for (int i = 0; i < getVertices().size(); i++)
            getVertices().set(i, addUpPoints(relativeLocation(getMovement().getAnchor(), getAnchorSave()),
                    rotateAbout(verticesSave.get(i), getAnchorSave(), getTotalRotation())));
        rotateGeoShape(getModelId(), currentRotation);
    }

    @Override
    public void createGeometry() {
        if (!verticesSave.isEmpty()) {
            Coordinate[] coordinates = new Coordinate[verticesSave.size() + 1];
            for (int i = 0; i < getVertices().size(); i++) coordinates[i] = toCoordinate(getVertices().get(i));
            coordinates[getVertices().size()] = toCoordinate(getVertices().get(0));
            geometry = new GeometryFactory().createLineString(coordinates);
        } else geometry = new GeometryFactory().createLineString(new Coordinate[0]);
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public boolean isCircular() {
        return isCircular;
    }

    @Override
    public float getRadius() {
        if (getAnchorSave() == null) return 0;
        return (float) getAnchorSave().getX();
    }


    @Override
    public Point2D getAnchor() {
        return movement.getAnchor();
    }

    @Override
    public Point2D getLastAnchor() {
        return movement.getLastAnchor();
    }

    @Override
    public float getSpeed() {
        return movement.getSpeed();
    }

    @Override
    public boolean collide(Collidable collidable) {
        return collidable instanceof GeoShapeModel;
    }

    @Override
    public long getPositionUpdateTimeDiffCapture() {
        return this.movement.getPositionUpdateTimeDiffCapture();
    }

    @Override
    public void setPositionUpdateTimeDiffCapture(long time) {
        this.movement.setPositionUpdateTimeDiffCapture(time);
    }

    @Override
    public long getLastPositionUpdateTime() {
        return this.movement.getLastAnchorUpdateTime();
    }

    @Override
    public void setLastPositionUpdateTime(long time) {
        this.movement.setLastAnchorUpdateTime(time);
    }

    public Point2D getCenter() {
        Point2D sumPoint = new Point2D.Float(0, 0);
        for (Point2D vertex : getVertices()) sumPoint = addUpPoints(sumPoint, vertex);
        return new Point2D.Float((float) sumPoint.getX() / getVertices().size(), (float) sumPoint.getY() / getVertices().size());
    }

    public Movement getMovement() {
        return movement;
    }

    @Override
    public String getModelId() {return modelId;}

    @Override
    public String getMotionPanelId() {
        return motionPanelId;
    }

    public void setMotionPanelId(String motionPanelId) {
        this.motionPanelId = motionPanelId;
    }

    public void setCircular(boolean circular) {
        isCircular = circular;
    }

    public Point2D getAnchorSave() {
        return anchorSave;
    }

    public void setAnchorSave(Point2D anchorSave) {
        this.anchorSave = anchorSave;
    }

    public List<Point2D> getVertices() {
        return vertices;
    }

    public float getTotalRotation() {
        return totalRotation;
    }

    public void setTotalRotation(float totalRotation) {
        this.totalRotation = totalRotation;
    }
}

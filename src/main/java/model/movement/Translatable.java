package model.movement;

import java.awt.geom.Point2D;

public interface Translatable {
    void moveShapeModel(Point2D point);

    void rotateShapeModel(float angle);
}

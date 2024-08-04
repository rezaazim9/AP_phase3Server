package model.characters;

import model.collision.Collidable;
import model.movement.Direction;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static controller.UserInterfaceController.createCollectible;
import static controller.constants.EntityConstants.*;
import static controller.constants.EntityConstants.EntityVertices.COLLECTIBLE_VERTICES;
import static controller.constants.ViewConstants.COLLECTIBLE_SIZE_OFFSET;
import static model.Utils.*;

public class CollectibleModel extends GeoShapeModel {
    public static final Random random = new Random();
    public final GeoShapeModel ancestor;

    public CollectibleModel(GeoShapeModel geoShapeModel) {
        super(new Point2D.Float(0, 0), COLLECTIBLE_VERTICES.getValue(), COLLECTIBLE_HEALTH.getValue());
        this.setCircular(true);
        this.ancestor = geoShapeModel;
        this.setMotionPanelId(geoShapeModel.getMotionPanelId());
        this.setAnchorSave(new Point2D.Float((COLLECTIBLE_SIZE_OFFSET.getValue() + geoShapeModel.getCollectibleValue()) / 2f, (COLLECTIBLE_SIZE_OFFSET.getValue() + geoShapeModel.getCollectibleValue()) / 2f));
       createCollectible(getModelId(), geoShapeModel.getModelId(), geoShapeModel.getCollectibleValue(), roundPoint(getAnchorSave()), geoShapeModel.getMotionPanelId());
        Point2D spawnLocation = addUpPoints(geoShapeModel.getAnchor(), multiplyPoint(new Direction(random.nextFloat(0, 360)).getDirectionVector(), random.nextFloat(0, geoShapeModel.getRadius())));
        moveShapeModel(spawnLocation);
        getMovement().setAnchor(spawnLocation);
        Timer timer=new Timer((int) TimeUnit.SECONDS.toMillis(COLLECTIBLE_LIFE_TIME.getValue()), e -> eliminate());
        timer.setCoalesce(true);
        timer.setRepeats(false);
        timer.start();
    }

    public static void bulkCreateCollectibles(GeoShapeModel geoShapeModel) {
        for (int i = 0; i < geoShapeModel.getNumberOfCollectibles(); i++) new CollectibleModel(geoShapeModel);
    }

    public int getValue() {
        return ancestor.getCollectibleValue();
    }

    @Override
    public boolean collide(Collidable collidable) {
        return collidable instanceof EpsilonModel;
    }
}

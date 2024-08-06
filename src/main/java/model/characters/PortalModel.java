package model.characters;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.concurrent.TimeUnit;
import static controller.constants.EntityConstants.*;

import static controller.constants.EntityConstants.EntityVertices.PORTAL_VERTICES;

public class PortalModel extends GeoShapeModel{
    public PortalModel( String motionPanelId, Point2D anchor) {
        super(new Point(0, 0),PORTAL_VERTICES.getValue(), PORTAL_HEALTH.getValue());
        this.setCircular(true);
        setMotionPanelId(motionPanelId);
        moveShapeModel(anchor);
        getMovement().setAnchor(anchor);
        setNumberOfCollectibles(0);
        setCollectibleValue(0);
        Timer timer=new Timer((int) TimeUnit.SECONDS.toMillis(PORTAL_LIFE_TIME.getValue()), e -> eliminate());
        timer.setCoalesce(true);
        timer.setRepeats(false);
        timer.start();
    }
}

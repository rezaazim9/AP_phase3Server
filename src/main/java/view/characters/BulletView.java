package view.characters;

import view.containers.MotionPanelView;

import java.awt.*;

import static controller.constants.DimensionConstants.BULLET_DIMENSION;
import static controller.constants.FilePaths.BULLET_IMAGEPATH;


public class BulletView extends GeoShapeView {

    public BulletView(Point relativeAnchorLocation, MotionPanelView motionPanelView) {
        super(getRawImage(BULLET_IMAGEPATH.getValue()), BULLET_DIMENSION.getValue(), relativeAnchorLocation, motionPanelView, true);
    }

}

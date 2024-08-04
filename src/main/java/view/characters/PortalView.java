package view.characters;

import view.containers.MotionPanelView;

import java.awt.*;

import static controller.constants.DimensionConstants.PORTAL_DIMENSION;
import static controller.constants.FilePaths.PORTAL_IMAGEPATH;

public class PortalView  extends GeoShapeView {
    public PortalView(Point relativeAnchorLocation, MotionPanelView motionPanelView) {
        super(getRawImage(PORTAL_IMAGEPATH.getValue()), PORTAL_DIMENSION.getValue(), relativeAnchorLocation, motionPanelView, true);
    }

}

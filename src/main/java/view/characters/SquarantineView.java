package view.characters;

import view.containers.MotionPanelView;

import java.awt.*;

import static controller.constants.DimensionConstants.SQUARANTINE_DIMENSION;
import static controller.constants.FilePaths.SQUARANTINE_IMAGEPATH;

public class SquarantineView extends GeoShapeView {

    public SquarantineView(Point relativeAnchorLocation, MotionPanelView motionPanelView) {
        super(getRawImage(SQUARANTINE_IMAGEPATH.getValue()), SQUARANTINE_DIMENSION.getValue(), relativeAnchorLocation, motionPanelView, false);
    }
}

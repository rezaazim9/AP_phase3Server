package view.characters;

import view.containers.MotionPanelView;

import java.awt.*;

import static controller.constants.DimensionConstants.EPSILON_DIMENSION;
import static controller.constants.FilePaths.EPSILON_IMAGEPATH;

public class EpsilonView extends GeoShapeView {

    public EpsilonView(Point relativeAnchorLocation, MotionPanelView motionPanelView) {
        super(getRawImage(EPSILON_IMAGEPATH.getValue()), EPSILON_DIMENSION.getValue(), relativeAnchorLocation, motionPanelView, true);
    }
}

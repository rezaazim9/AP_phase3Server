package view.characters;

import view.containers.MotionPanelView;

import java.awt.*;

import static controller.constants.DimensionConstants.TRIGORATH_DIMENSION;
import static controller.constants.FilePaths.TRIGORATH_IMAGEPATH;

public class TrigorathView extends GeoShapeView {

    public TrigorathView(Point relativeAnchorLocation, MotionPanelView motionPanelView) {
        super(getRawImage(TRIGORATH_IMAGEPATH.getValue()), TRIGORATH_DIMENSION.getValue(), relativeAnchorLocation, motionPanelView, false);
    }
}

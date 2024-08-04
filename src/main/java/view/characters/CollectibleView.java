package view.characters;

import view.containers.MotionPanelView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static controller.constants.DefaultMethods.GET_AVERAGE_TONE_OF_CHARACTER;
import static controller.constants.ViewConstants.COLLECTIBLE_SIZE_OFFSET;

public class CollectibleView extends GeoShapeView {
    private static final ConcurrentMap<Class<?>, BufferedImage> collectibleImageCache = new ConcurrentHashMap<>();

    public CollectibleView(Point relativeAnchorLocation, int value, GeoShapeView ancestor, MotionPanelView motionPanelView) {
        super(createCollectibleImage(ancestor, (int) (value + COLLECTIBLE_SIZE_OFFSET.getValue())),
                new Dimension((int) (COLLECTIBLE_SIZE_OFFSET.getValue() + value), (int) (COLLECTIBLE_SIZE_OFFSET.getValue() + value)),
                relativeAnchorLocation, motionPanelView, true);
    }

    public static BufferedImage createCollectibleImage(GeoShapeView geoShapeView, int size) {
        if (!collectibleImageCache.containsKey(geoShapeView.getClass())) {
            BufferedImage bimage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = bimage.createGraphics();
            graphics2D.setColor(GET_AVERAGE_TONE_OF_CHARACTER(geoShapeView));
            graphics2D.fillOval(0, 0, size, size);
            collectibleImageCache.put(geoShapeView.getClass(), bimage);
        }
        return collectibleImageCache.get(geoShapeView.getClass());
    }
}

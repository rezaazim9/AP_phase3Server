package view.containers;

import view.characters.GeoShapeView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.UserInterfaceController.isGameOn;
import static controller.constants.DefaultMethods.getCenterOffset;
import static controller.constants.FilePaths.GAME_BACKGROUND_PATH;
import static controller.constants.ViewConstants.VERTEX_RADIUS;
import static view.Utils.toBufferedImage;
import static view.containers.GlassFrame.getGlassFrame;

public class MotionPanelView extends PanelB {
    private static MotionPanelView mainMotionPanelView;
    public static final List<MotionPanelView> allMotionPanelViewsList = new CopyOnWriteArrayList<>();
    public final List<GeoShapeView> shapeViews = new CopyOnWriteArrayList<>();
    private String viewId;
    public MotionPanelView(Dimension size, Point location) {
        super(size.width, size.height,toBufferedImage(GAME_BACKGROUND_PATH.getValue()));
        if (getMainMotionPanelView() == null) setMainMotionPanelView(this);
        setBackground(new Color(0, 0, 0, 0));
        setSize(size);
        setDoubleBuffered(true);
        setLocation(location);
        setBorder(BorderFactory.createLineBorder(Color.black, 5));
        allMotionPanelViewsList.add(this);
        getGlassFrame().add(this);
    }

    public static MotionPanelView getMainMotionPanelView() {
        return mainMotionPanelView;
    }

    public static void setMainMotionPanelView(MotionPanelView mainMotionPanelView) {
        MotionPanelView.mainMotionPanelView = mainMotionPanelView;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        for (GeoShapeView shapeView : shapeViews) {
            shapeView.getRotatedIcon().paintIcon(this, g, 0, 0);
            if (isGameOn()) {
                for (Point point : shapeView.getVertexLocations()) {
                    g.fillOval((int) (point.x - getCenterOffset(VERTEX_RADIUS.getValue())), (int) (point.y - getCenterOffset(VERTEX_RADIUS.getValue())),
                            (int) VERTEX_RADIUS.getValue(), (int) VERTEX_RADIUS.getValue());
                }
            }
        }
    }


}

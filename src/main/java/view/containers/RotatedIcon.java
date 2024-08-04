package view.containers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static controller.constants.DefaultMethods.radianTable;
import static view.Utils.relativeLocation;
import static view.Utils.validateAngle;

public class RotatedIcon implements Icon {
    private final Icon icon;
    private final boolean isCircular;
    private Point corner = new Point(0, 0);
    private Point offset = new Point(0, 0);
    private int width;
    private int height;
    private Point rotationAnchor;
    private float opacity = 1;
    private float degrees;

    public RotatedIcon(BufferedImage image, Point rotationAnchor, float degrees, boolean isCircular) {
        this.icon = new ImageIcon(image);
        this.setRotationAnchor(rotationAnchor);
        this.setDegrees(degrees);
        this.isCircular = isCircular;
    }

    @Override
    public int getIconWidth() {
        if (isCircular()) return getIcon().getIconWidth();
        else return getWidth();
    }

    @Override
    public int getIconHeight() {
        if (isCircular()) return getIcon().getIconHeight();
        else return getHeight();
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Point relativeCorner = relativeLocation(c, getCorner());
        Point relativeOffset = relativeLocation(c, getOffset());
        Point relativeIconCorner = new Point(Math.max(relativeOffset.x, 0), Math.max(relativeOffset.y, 0));
        int upperX = Math.min(relativeIconCorner.x + getIconWidth(), c.getWidth());
        int upperY = Math.min(relativeIconCorner.y + getIconHeight(), c.getHeight());

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(relativeIconCorner.x, relativeIconCorner.y, upperX - relativeIconCorner.x, upperY - relativeIconCorner.y);
        g2d.rotate(radianTable[(int) validateAngle(getDegrees())], (double) relativeCorner.x + getRotationAnchor().x, (double) relativeCorner.y + getRotationAnchor().y);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getOpacity()));
        getIcon().paintIcon(c, g2d, relativeCorner.x, relativeCorner.y);
        g2d.dispose();
    }

    public void rotate(float degrees) {
        this.setDegrees(validateAngle(this.degrees-degrees));
    }

    public Point getRotationAnchor() {
        return rotationAnchor;
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean isCircular() {
        return isCircular;
    }

    public Point getCorner() {
        return corner;
    }

    public void setCorner(Point corner) {
        this.corner = corner;
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRotationAnchor(Point rotationAnchor) {
        this.rotationAnchor = rotationAnchor;
    }

    public float getOpacity() {return opacity;}

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }
}
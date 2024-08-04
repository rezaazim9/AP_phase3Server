package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static model.Utils.rotateAbout;

public class Utils {
    private Utils(){}

    public static BufferedImage toBufferedImage(String path){
        try {return ImageIO.read(new File(path));}
        catch (IOException e) {throw new ImagingOpException("Failed to read image from path: "+path);}
    }
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage bufferedImage) return bufferedImage;
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    public static void darkenImage(BufferedImage image, Rectangle rectangle) {
        int xStart = Math.max(rectangle.x, 0);
        int yStart = Math.max(rectangle.y, 0);
        int xEnd = Math.min(rectangle.x + rectangle.width, image.getWidth());
        int yEnd = Math.min(rectangle.y + rectangle.height, image.getHeight());

        IntStream.range(yStart, yEnd).parallel().forEach(y -> IntStream.range(xStart, xEnd).parallel().forEach(x -> {
            int rgb = image.getRGB(x, y);
            Color color = new Color(rgb, true);

            int red = (int) (color.getRed() * 0.5); // Darken the red channel
            int green = (int) (color.getGreen() * 0.5); // Darken the green channel
            int blue = (int) (color.getBlue() * 0.5); // Darken the blue channel

            Color newColor = new Color(red, green, blue, color.getAlpha());
            image.setRGB(x, y, newColor.getRGB());
        }));
    }

    public static Color changeColorOpacity(Color color, float scale) {
        if (scale < 0 || scale > 1) return null;
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, scale);
    }

    public static BufferedImage bufferedImageClone(BufferedImage bi) {
        if (bi==null) return null;
        BufferedImage clone = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        bi.copyData(clone.getRaster());
        return clone;
    }

    public static Point relativeLocation(Component component, Point universalLocation) {
        if (universalLocation == null) return null;
        return new Point(universalLocation.x - component.getX(), universalLocation.y - component.getY());
    }

    public static Point[] rotatedInfo(Dimension viewSize, Point relativeAnchor, float angle, boolean isCircular) {
        //Returns an array of 2 points containing newSize, Offset

        if (isCircular) return new Point[]{new Point(viewSize.width, viewSize.height), new Point(0, 0)};
        Point[] corners = new Point[]{new Point(0, 0), new Point(viewSize.width, 0), new Point(0, viewSize.height), new Point(viewSize.width, viewSize.height)};
        Point[] rotatedCorners = new Point[4];
        for (int i = 0; i < corners.length; i++) rotatedCorners[i] = (Point) rotateAbout(corners[i], relativeAnchor, angle);
        CopyOnWriteArrayList<Integer> rotatedCornersX = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<Integer> rotatedCornersY = new CopyOnWriteArrayList<>();
        for (Point point : rotatedCorners) {
            rotatedCornersX.add(point.x);
            rotatedCornersY.add(point.y);
        }
        int xLower = Collections.min(rotatedCornersX);
        int yLower = Collections.min(rotatedCornersY);
        int xUpper = Collections.max(rotatedCornersX);
        int yUpper = Collections.max(rotatedCornersY);

        return new Point[]{new Point(xUpper - xLower + 1, yUpper - yLower + 1), new Point(-xLower, -yLower)};
    }

    public static Color averageTone(BufferedImage image) {
        if (image == null) return new Color(0, 0, 0, 0);
        int sumR = 0, sumG = 0, sumB = 0, cnt = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if ((image.getRGB(i, j) >> 24) != 0x00) {
                    Color pointColor = new Color(image.getRGB(i, j));
                    sumR += pointColor.getRed();
                    sumG += pointColor.getGreen();
                    sumB += pointColor.getBlue();
                    cnt++;
                }
            }
        }
        if (cnt == 0) return new Color(0, 0, 0, 0);
        return new Color(sumR / cnt, sumG / cnt, sumB / cnt);
    }
    public static float validateAngle(float angle){return (float) (angle - Math.floor(angle / 360) * 360);}

    public static BufferedImage cropImage(Point2D location, Dimension dimension, BufferedImage image, boolean resize){
        if (resize) return toBufferedImage(image.getScaledInstance(dimension.width, dimension.height, Image.SCALE_SMOOTH));
        else return image.getSubimage((int) location.getX(), (int) location.getY(),dimension.width,dimension.height);
    }
}

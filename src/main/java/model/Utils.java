package main.java.model;

import org.locationtech.jts.geom.Coordinate;

import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {
    private Utils(){}
    public static boolean areInstancesOf(Object object1, Object object2, Class<?> class1, Class<?> class2) {
        return (class1.isInstance(object1) && class2.isInstance(object2)) || (class2.isInstance(object1) && class1.isInstance(object2));
    }

    /**
     * @param collection a collection of type {@code T} to be cloned
     * @param <T>        generic class type
     * @return deep cloned thread-safe Arraylist. All objects are cloned via {@link #deepClone(Object)} method
     */
    public static <T> List<T> deepCloneList(Collection<T> collection) {
        if (collection == null) return null;
        CopyOnWriteArrayList<T> output = new CopyOnWriteArrayList<>();
        for (T t : collection) output.add(deepClone(t));
        return output;
    }

    /**
     * <p> Note : Exception is suppressed by try/catch because it is checked that the clone method has public access. Unchecked cast is also safe
     * since clone method of {@code T} returns another T instance </p>
     *
     * @param t   object of type {@code T}
     * @param <T> generic class type
     * @return deep cloned copy of the object. Calls on {@link #deepCloneList(Collection)} if {@code t} is a collection
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T t) {
        Method cloneMethod;
        try {cloneMethod = t.getClass().getMethod("clone");}
        catch (NoSuchMethodException e) {return null;}

        if (Modifier.isPublic(cloneMethod.getModifiers())) {
            Object cloned;
            try {cloned = cloneMethod.invoke(t);}
            catch (IllegalAccessException | InvocationTargetException e) {throw new UnsupportedOperationException("Failed to clone instance of class: "+t.getClass().getName());}
            return (T) cloned;
        }
        return null;
    }

    /**
     * @param point  a Point2D instance to be rotated about anchor
     * @param anchor a Point2D instance acting as the anchor of rotation
     * @param angle  the rotation angle passed in degrees
     * @return the rotation of {@code point} about {@code anchor} with magnitude of {@code angle}
     */
    public static Point2D rotateAbout(Point2D point, Point2D anchor, float angle) {
        Point2D.Float translated = new Point2D.Float((float) (point.getX() - anchor.getX()), (float) (point.getY() - anchor.getY()));
        float angleModified = validateAngle(angle);
//        float rotatedX = (float) (translated.x * DefaultMethods.cosTable[(int) angleModified] - translated.y * DefaultMethods.sinTable[(int) angleModified]);
//        float rotatedY = (float) (translated.x * DefaultMethods.sinTable[(int) angleModified] + translated.y * DefaultMethods.cosTable[(int) angleModified]);
//        Point2D.Float rotated = new Point2D.Float(rotatedX, rotatedY);
//        Point2D out = addUpPoints(anchor, rotated);
//        if (anchor instanceof Point || point instanceof Point) return roundPoint(out);
//        return out;
        return null;
    }

    /**
     * <p>Note : if {@code point} is on the origin, method returns zero</p>
     *
     * @return the angle between the line connecting the origin to {@code point} and the positive direction of x-axis
     */
    public static float calculateAngle(Point2D point) {
        if (point.getX() == 0 && point.getY() == 0) return 0;
        float angle = (float) Math.toDegrees(Math.atan2(point.getY(), point.getX()));
        return validateAngle(angle);
    }

    /**
     * @return cross product of 2D vectors {@code vector1},{@code vector2}
     */
    public static float crossProduct(Point2D vector1, Point2D vector2) {
        return (float) (vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX());
    }

    /**
     * @return dot product of 2D vectors {@code vector1},{@code vector2}
     */
    public static float dotProduct(Point2D vector1, Point2D vector2) {
        return (float) (vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY());
    }

    /**
     * @return the location of {@code point} after translating {@code anchor} to the origin of canvas
     */
    public static Point2D relativeLocation(Point2D point, Point2D anchor) {
        Point2D out = new Point2D.Float((float) (point.getX() - anchor.getX()), (float) (point.getY() - anchor.getY()));
        if (point instanceof Point || anchor instanceof Point) return roundPoint(out);
        return out;
    }

    /**
     * @return addition (component-wise) of the given points
     */
    public static Point2D addUpPoints(Point2D point1, Point2D point2) {
        Point2D out = new Point2D.Float((float) (point2.getX() + point1.getX()), (float) (point2.getY() + point1.getY()));
        if (point1 instanceof Point || point2 instanceof Point) roundPoint(out);
        return out;
    }

    /**
     * @return the scalar multiplication (component-wise) of the Point2D instance {@code point} by {@code scalar}
     */
    public static Point2D multiplyPoint(Point2D point, float scalar) {
        Point2D out = new Point2D.Float((float) (point.getX() * scalar), (float) (point.getY() * scalar));
        if (point instanceof Point) return roundPoint(out);
        return out;
    }

    /**
     * @return weighted addition of Point2D instances {@code point1},{@code point2} with weights {@code weight1},{@code weight2} resp.
     */
    public static Point2D weightedAddPoints(Point2D point1, Point2D point2, float weight1, float weight2) {
        Point2D.Float weightedSum = (Point2D.Float) addUpPoints(multiplyPoint(point1, weight1), multiplyPoint(point2, weight2));
        return multiplyPoint(weightedSum, 1 / (weight1 + weight2));
    }

    /**
     * @return rounded copy (component-wise) of Point2D instance {@code point2D} as a Point instance
     */
    public static Point roundPoint(Point2D point2D) {
        return new Point((int) point2D.getX(), (int) point2D.getY());
    }

    /**
     * @return rounded copy (component-wise) of Point2D instance {@code point2D} as a Dimension instance
     */
    public static Dimension pointToDimension(Point2D point2D) {
        return new Dimension((int) point2D.getX(), (int) point2D.getY());
    }

    public static Point2D.Float toPoint(Coordinate coordinate) {
        return new Point2D.Float((float) coordinate.x, (float) coordinate.y);
    }

    public static Coordinate toCoordinate(Point2D point2D) {
        return new Coordinate(point2D.getX(), point2D.getY());
    }

    public static float validateAngle(float angle){return (float) (angle - Math.floor(angle / 360) * 360);}
}
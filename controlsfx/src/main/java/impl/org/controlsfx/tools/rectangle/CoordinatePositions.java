package impl.org.controlsfx.tools.rectangle;

import java.util.EnumSet;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Computes coordinate positions relative to a rectangle.
 */
public class CoordinatePositions {

    /**
     * Returns all positions the specified point has regarding the specified rectangle and its edges (using the
     * specified tolerance).
     * 
     * @param rectangle
     *            the rectangle relative to which the point will be checked
     * @param point
     *            the checked point
     * @param edgeTolerance
     *            the tolerance in pixels used to determine whether the coordinates are on some edge
     * @return a set of those positions the coordinates have regarding the specified rectangle
     */
    public static EnumSet<CoordinatePosition> onRectangleAndEdges(Rectangle2D rectangle, Point2D point, double edgeTolerance) {
        EnumSet<CoordinatePosition> positions = EnumSet.noneOf(CoordinatePosition.class);
        positions.add(inRectangle(rectangle, point));
        positions.add(onEdges(rectangle, point, edgeTolerance));
        return positions;
    }

    /*
     * RECTANGLE
     */

    /**
     * Returns the position the specified coordinates have regarding the specified rectangle. Edges are not checked.
     * 
     * @param rectangle
     *            the rectangle relative to which the point will be checked
     * @param point
     *            the checked point
     * @return a set of those positions the coordinates have regarding the specified rectangle
     */
    public static CoordinatePosition inRectangle(Rectangle2D rectangle, Point2D point) {
        if (rectangle.contains(point))
            return CoordinatePosition.IN_RECTANGLE;
        else
            return CoordinatePosition.OUT_OF_RECTANGLE;
    }

    /*
     * EDGES
     */

    /**
     * Returns the position the specified coordinates have regarding the specified rectangle's edges using the specified
     * tolerance.
     * 
     * @param rectangle
     *            the rectangle relative to which the point will be checked
     * @param point
     *            the checked point
     * @param edgeTolerance
     *            the tolerance in pixels used to determine whether the coordinates are on some edge
     * @return that edge position the coordinates have regarding the specified rectangle; the value might be null
     */
    public static CoordinatePosition onEdges(Rectangle2D rectangle, Point2D point,
            double edgeTolerance) {

        CoordinatePosition vertical = closeToVertical(rectangle, point, edgeTolerance);
        CoordinatePosition horizontal = closeToHorizontal(rectangle, point, edgeTolerance);

        return extractSingleCardinalDirection(vertical, horizontal);
    }

    /**
     * Returns the vertical bound the specified coordinates are closest to, if the distance is smaller than the edge
     * tolerance. Otherwise, null is returned.
     * 
     * @param rectangle
     *            the rectangle relative to which the point will be checked
     * @param point
     *            the checked point
     * @param edgeTolerance
     *            the tolerance in pixels used to determine whether the coordinates are on some edge
     * @return EAST_EDGE, WEST_EDGE or null
     */
    private static CoordinatePosition closeToVertical(Rectangle2D rectangle, Point2D point, double edgeTolerance) {

        double xDistanceToLeft = Math.abs(point.getX() - rectangle.getMinX());
        double xDistanceToRight = Math.abs(point.getX() - rectangle.getMaxX());
        boolean xCloseToLeft = xDistanceToLeft < edgeTolerance && xDistanceToLeft < xDistanceToRight;
        boolean xCloseToRight = xDistanceToRight < edgeTolerance && xDistanceToRight < xDistanceToLeft;

        if (!xCloseToLeft && !xCloseToRight)
            return null;

        boolean yCloseToVertical = rectangle.getMinY() - edgeTolerance < point.getY()
                && point.getY() < rectangle.getMaxY() + edgeTolerance;
        if (yCloseToVertical) {
            if (xCloseToLeft)
                return CoordinatePosition.WEST_EDGE;
            if (xCloseToRight)
                return CoordinatePosition.EAST_EDGE;
        }

        return null;
    }

    /**
     * Returns the horizontal bound the specified coordinates are closest to, if the distance is smaller than the edge
     * tolerance. Otherwise, null is returned.
     * 
     * @param rectangle
     *            the rectangle relative to which the point will be checked
     * @param point
     *            the checked point
     * @param edgeTolerance
     *            the tolerance in pixels used to determine whether the coordinates are on some edge
     * @return NORTH_EDGE, SOUTH_EDGE or null
     */
    private static CoordinatePosition closeToHorizontal(Rectangle2D rectangle, Point2D point, double edgeTolerance) {

        double yDistanceToUpper = Math.abs(point.getY() - rectangle.getMinY());
        double yDistanceToLower = Math.abs(point.getY() - rectangle.getMaxY());
        boolean yCloseToUpper = yDistanceToUpper < edgeTolerance && yDistanceToUpper < yDistanceToLower;
        boolean yCloseToLower = yDistanceToLower < edgeTolerance && yDistanceToLower < yDistanceToUpper;

        if (!yCloseToUpper && !yCloseToLower)
            return null;

        boolean xCloseToHorizontal = rectangle.getMinX() - edgeTolerance < point.getX()
                && point.getX() < rectangle.getMaxX() + edgeTolerance;
        if (xCloseToHorizontal) {
            if (yCloseToUpper)
                return CoordinatePosition.NORTH_EDGE;
            if (yCloseToLower)
                return CoordinatePosition.SOUTH_EDGE;
        }

        return null;
    }

    /**
     * Extracts a single cardinal direction from the two specified positions. The conditions stated below are not
     * checked!
     * 
     * @param vertical
     *            a vertical edge (EAST or WEST) or null
     * @param horizontal
     *            a horizontal edge (NORTH OR SOUTH) or null
     * @return the single coordinate position which matches the specified positions <br>
     *         (e.g. NORTH for (null, NORTH) and SOUTHWEST for (WEST, SOUTH))
     */
    private static CoordinatePosition extractSingleCardinalDirection(CoordinatePosition vertical,
            CoordinatePosition horizontal) {
        if (vertical == null)
            return horizontal;

        if (horizontal == null)
            return vertical;

        // north
        if (horizontal == CoordinatePosition.NORTH_EDGE && vertical == CoordinatePosition.EAST_EDGE)
            return CoordinatePosition.NORTHEAST_EDGE;
        if (horizontal == CoordinatePosition.NORTH_EDGE && vertical == CoordinatePosition.WEST_EDGE)
            return CoordinatePosition.NORTHWEST_EDGE;

        // south
        if (horizontal == CoordinatePosition.SOUTH_EDGE && vertical == CoordinatePosition.EAST_EDGE)
            return CoordinatePosition.SOUTHEAST_EDGE;
        if (horizontal == CoordinatePosition.SOUTH_EDGE && vertical == CoordinatePosition.WEST_EDGE)
            return CoordinatePosition.SOUTHWEST_EDGE;

        throw new IllegalArgumentException();
    }

}

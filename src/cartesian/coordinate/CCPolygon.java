/*
 * Copyright (C) 2012 Andreas Halle
 *
 * This file is part of jcoolib
 *
 * jcoolib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jcoolib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public license
 * along with jcoolib. If not, see <http://www.gnu.org/licenses/>.
 */
package cartesian.coordinate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 * {@code CCPolygon} represents a polygon in a Cartesian coordinate system, i.e.
 * a shape consisting of straight lines joined through points to form a circuit.
 *
 * @author Andreas Halle
 * @see    CCSystem
 */
public class CCPolygon {
    protected double[] xpoints;
    protected double[] ypoints;
    protected Paint paint;
    protected Paint fill;
    protected Stroke stroke;
    
    
    
    /**
     * Create a polygon from a set of points. A polygon needs more than two
     * points.
     * 
     * @param xpoints
     *        x-coordinates for the points that form the polygon.
     * @param ypoints
     *        y-coordinates for the points that form the polygon.
     * @param paint
     *        {@code Paint} to paint the edges of the polygon with.
     * @param fill
     *        {@code Paint} to fill the interior of the polygon with.
     * @param stroke
     *        Draw the edges of the polygon with this {@code Stroke}.
     */
    public CCPolygon(double [] xpoints, double[] ypoints, Paint paint,
                                            Paint fill, Stroke stroke) {
        if (xpoints.length != ypoints.length) {
            String e = "number of x-coordinates must match number of y"
                     + "-coordinates";
            throw new IllegalArgumentException(e);
        }
        if (xpoints.length < 2) {
            String e = "cannot create a polygon from less than three points.";
            throw new IllegalArgumentException(e);
        }
        
        this.xpoints = xpoints;
        this.ypoints = ypoints;
        this.paint = paint;
        this.fill = fill;
        this.stroke = stroke;
    }
    
    
    
    /**
     * Create a polygon from a set of points. A polygon needs more than two
     * points.
     * 
     * @param points
     *        Array of {@code Point2D} points that form the polygon.
     * @param paint
     *        {@code Paint} to paint the edges of the polygon with.
     * @param fill
     *        {@code Paint} to fill the interior of the polygon with.
     * @param stroke
     *        Draw the edges of the polygon with this {@code Stroke}.
     */
    public CCPolygon(Point2D[] points, Paint paint,
                                            Paint fill, Stroke stroke) {
        this(new double[points.length], new double[points.length], paint, fill,
                                                                        stroke);
        for (int i = 0; i < points.length; i++) {
            xpoints[i] = points[i].getX();
            ypoints[i] = points[i].getY();
        }
    }
    
    
    
    /**
     * Create a polygon from a set of points. A polygon needs more than two
     * points.
     * <p>
     * The edges will be painted in black with a 1 pixel thick edge. The
     * interior of the polygon will be filled in pink.
     * 
     * @param xpoints
     *        x-coordinates for the points that form the polygon.
     * @param ypoints
     *        y-coordinates for the points that form the polygon.
     */
    public CCPolygon(double [] xpoints, double [] ypoints) {
        this(xpoints, ypoints, Color.black, Color.pink, new BasicStroke(1f));
    }
    
    
    
    /**
     * Create a polygon from a set of points. A polygon needs more than two
     * points.
     * <p>
     * The edges will be painted in black with a 1 pixel thick edge. The
     * interior of the polygon will be filled in pink.
     * 
     * @param points
     *        Array of {@code Point2D} points that form the polygon.
     */
    public CCPolygon(Point2D[] points) {
        this(points, Color.black, Color.pink, new BasicStroke(1f));
    }
}
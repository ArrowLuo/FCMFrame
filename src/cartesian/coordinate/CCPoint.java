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

/**
 * {@code CCPoint} represents a point in a Cartesian coordinate system.
 *
 * @author Andreas Halle
 * @see    CCSystem
 */
public class CCPoint {
    protected double x;
    protected double y;
    protected Paint paint;
    protected Stroke stroke;
    
    
    
    /**
     * Create a point at given coordinates.
     * 
     * @param x
     *        x-coordinate for the location of the point.
     * @param y
     *        y-coordinate for the location of the point.
     * @param paint
     *        {@code Paint} to use when painting a circle at this point's
     *        location.
     * @param stroke
     *        Draw the circle at this point's location using this {@code Stroke}
     *        
     */
    public CCPoint(double x, double y, Paint paint, Stroke stroke) {
        this.x = x;
        this.y = y;
        this.paint = paint;
        this.stroke = stroke;
    }
    
    
    
    /**
     * Create a point at given coordinates.
     * <p>
     * The point will be painted in black with a 1 pixel thick edge.
     * 
     * @param x
     *        x-coordinate for the location of the point.
     * @param y
     *        y-coordinate for the location of the point.
     */
    public CCPoint(double x, double y) {
        this(x, y, Color.black, new BasicStroke(1f));
    }
    
    /**
     * Create a point at given coordinates.
     * <p>
     * The point will be painted  with a 1 pixel thick edge.
     * 
     * @param x 
     * 			x-coordinate for the location of the point.
     * @param y 
     * 			y-coordinate for the location of the point.
     * @param paint 
     * 			{@code Paint} to use when painting a circle at this point's
     *        location.
     */
    public CCPoint(double x, double y, Paint paint) {
        this(x, y, paint, new BasicStroke(1f));
    }
}
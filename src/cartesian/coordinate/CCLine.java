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
 * {@code CCLine} represents a straight line in a Cartesian coordinate system.
 * <p>
 * Lines are stored as a linear equation on the format:
 * <pre>
 *     ax + by = c
 * </pre>
 *
 * @author Andreas Halle
 * @see    CCSystem
 */
public class CCLine {
    protected double a;
    protected double b;
    protected double c;
    protected Paint paint;
    protected Stroke stroke;
    
    
    
    /**
     * Create a new line from a linear equation in slope-intercept form:
     * <pre>
     *     y = mx + b
     * </pre>
     * 
     * @param m
     *        The slope of the line.
     * @param b
     *        y-intercept; the y-coordinate of the location where the line
     *        crosses the y axis.
     */
    public CCLine(double m, double b) {
        this(-m, 1, b);
    }



    /**
     * Create a new line from a linear equation in general form:
     * <pre>
     *     ax + by = c
     * </pre>
     * 
     * @param a
     *        Coefficient of the x-variable.
     * @param b
     *        Coefficient of the y-variable.
     * @param c
     *        A constant.
     */
    public CCLine(double a, double b, double c) {
        this(a, b, c, Color.black, new BasicStroke(1f));
    }
    
    
    
    /**
     * Create a new line from a linear equation in general form:
     * <pre>
     *     ax + by = c
     * </pre>
     * 
     * @param a
     *        Coefficient of the x-variable.
     * @param b
     *        Coefficient of the y-variable.
     * @param c
     *        A constant.
     * @param paint
     *        Draw the line using this paint.
     */
    public CCLine(double a, double b, double c, Paint paint) {
        this(a, b, c, paint, new BasicStroke(1f));
    }
    
    
    
    /**
     * Create a new line from a linear equation in slope-intercept form:
     * <pre>
     *     y = mx + b
     * </pre>
     * 
     * @param m
     *        The slope of the line.
     * @param b
     *        y-intercept; the y-coordinate of the location where the line
     *        crosses the y axis.
     * @param paint
     *        Draw the line using this paint.
     */
    public CCLine(double m, double b, Paint paint) {
        this(-m, 1, b, paint);
    }



    /**
     * Create a new line from a linear equation in general form:
     * <pre>
     *     ax + by = c
     * </pre>
     * 
     * @param a
     *        Coefficient of the x-variable.
     * @param b
     *        Coefficient of the y-variable.
     * @param c
     *        A constant.
     * @param paint
     *        Draw the line using this paint.
     * @param stroke
     *        Draw the line using this stroke.
     */
    public CCLine(double a, double b, double c, Paint paint, Stroke stroke) {
        if (a == 0 && b == 0) {
            String e = "at least one of a or b must be nonzero in ax + by = c.";
            throw new IllegalArgumentException(e);
        }
        
        this.a = a;
        this.b = b;
        this.c = c;
        this.paint = paint;
        this.stroke = stroke;
    }
    
    
    
    /**
     * Create a new line from a linear equation in slope-intercept form:
     * <pre>
     *     y = mx + b
     * </pre>
     * 
     * @param m
     *        The slope of the line.
     * @param b
     *        y-intercept; the y-coordinate of the location where the line
     *        crosses the y axis.
     * @param paint
     *        Draw the line using this paint.
     * @param stroke
     *        Draw the line using this stroke.
     */
    public CCLine(double m, double b, Paint paint, Stroke stroke) {
        this(-m, 1, b, paint, stroke);
    }
    
    
    
    /**
     * Find the x-value of the line at a specific y-value.
     * <p>
     * Will return {@code NaN} if <i>a</i> is 0.
     * 
     * @param  y
     *         y value of the function.
     * @return
     *         x value at the given y.
     */
    public double solveForX(double y) {
        return (c-b*y)/a;
    }
    
    
    
    /**
     * Find the y-value of the line at a specific x-value.
     * <p>
     * Will return {@code NaN} if <i>b</i> is 0.
     * 
     * @param  x
     *         x value of the function.
     * @return
     *         y value at the given x.
     */
    public double solveForY(double x) {
        return (c-a*x)/b;
    }
}
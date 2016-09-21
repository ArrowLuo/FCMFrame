/*
 * Copyright (C) 2012-2014 Andreas Halle
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
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.freehep.graphics2d.VectorGraphics;

/**
 * A class representing a visible Cartesian coordinate system.
 * <p>
 * The system contains (in)visible x- and y-axes that range from one double
 * precision number to another.
 * <p>
 * The system can contain objects such as lines, points and polygons.
 * 
 * @author Andreas Halle
 * @see    CCLine
 */
public class CCSystem extends JPanel {
    /*
     * In this class there are two coordinate systems:
     * 
     * 1. A two-dimensional coordinate system for Java2D where x lies in the
     *    interval [0, window width] and y lies in the interval
     *    [0, window height] where the units of both x and y are pixels.
     *    
     * 2. An emulated two-dimensional coordinate system where x and y can lie in
     *    any range definable by double precision numbers.
     * 
     * Throughout this class, Point is used to represent a point in system 1
     * while Point2D is used to represent a point in system 2.
     * 
     * The translate.*(.)-methods are used to translate between the two systems.
     */
    private static final long serialVersionUID = 1L;
    
    /* Some visual options */
    private boolean axisXVisible;
    private boolean axisYVisible;
    private boolean gridXVisible;
    private boolean gridYVisible;
    private boolean unitXVisible;
    private boolean unitYVisible;
    
    private Paint axisXPaint;
    private Paint axisYPaint;
    private Paint gridXPaint;
    private Paint gridYPaint;
    private Paint unitXPaint;
    private Paint unitYPaint;
    
    private Stroke axisXStroke;
    private Stroke axisYStroke;
    private Stroke gridXStroke;
    private Stroke gridYStroke;
    private Stroke unitXStroke;
    private Stroke unitYStroke;
    
    private boolean niceGraphics;
    /* End of visual options */
    
    /* The number of grid lines between each unit line */
    private double gridRatio;
    
    /* Other options */
    private boolean movable;
    private boolean zoomable;
    
    /* Object containers */
    private List<CCLine> lines;
    private List<CCPolygon> polygons;
    private List<CCPoint> points;
    
    /* Define the range of the visible xy-plane */
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    
    /* The length of the domain of x and y */
    private double distX;
    private double distY;
    
    /* The ratio between system 1 and system 2 */
    private double xscale;
    private double yscale;
    
    /* The distance between each unit line, in pixels */
    private int ulScale;
    
    /* The size of each unit line, in pixels */
    private int ulSize;
    
    /* Total number of units on the axes */
    private double unitsX;
    private double unitsY;
    
    /* Exact value between each unit line on the axes */
    private double udistX;
    private double udistY;
    
    /* 
     * Round this exact value to a value (of the same magnitude) that can be
     * written with very few decimals (or lots of trailing zeroes.)
     * 
     * vbu stands for "value between unit lines"
     */
    private BigDecimal vbuX;
    private BigDecimal vbuY;
    
    /* The origin of system 1 and system 2 */
    private Point2D.Double origin2d;
    private Point origin;
    
    /* Some listeners */
    private MouseListener mouseListener;
    private MouseWheelListener mouseWheelListener;
    
    private final MathContext prec = new MathContext(10);
    
    
    
    /**
     * Initialize a new empty coordinate system.
     * <p>
     * xy-axes, units and a grid is drawn by default. This can be changed with
     * the following methods:
     * <pre>
     *     setAxesVisible(boolean)
     *     setUnitsVisible(boolean)
     *     setGridVisible(boolean)
     * </pre>
     * respectively.
     */
    public CCSystem(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        /* Setting some default values. */
        axisXVisible = true;
        axisYVisible = true;
        gridXVisible = true;
        gridYVisible = true;
        unitXVisible = true;
        unitYVisible = true;
        
        axisXPaint = Color.black;
        axisYPaint = Color.black;
        gridXPaint = Color.black;
        gridYPaint = Color.black;
        unitXPaint = Color.black;
        unitYPaint = Color.black;
        
        axisXStroke = new BasicStroke(1.3f);
        axisYStroke = new BasicStroke(1.3f);
        gridXStroke = new BasicStroke(0.1f);
        gridYStroke = new BasicStroke(0.1f);
        unitXStroke = new BasicStroke(1f);
        unitYStroke = new BasicStroke(1f);
        
        gridRatio = 5;
        niceGraphics = true;
        zoomable = true;
        movable = true;
        ulScale = 65;
        ulSize = 4;

        lines = new ArrayList<CCLine>();
        polygons = new ArrayList<CCPolygon>();
        points = new ArrayList<CCPoint>();
        
        /* Add some default listeners */
        mouseListener = new mouseListener();
        mouseWheelListener = new mouseWheelListener();
        
        addMouseListener(mouseListener);
        addMouseMotionListener((MouseMotionListener) mouseListener);
        addMouseWheelListener(mouseWheelListener);
    }
    
    
    
    public CCSystem() {
        this(-10, -10, 10, 10);
    }
    
    
    
    /**
     * Add a {@code CCLine} to the coordinate system.
     * 
     * @param line
     *        a {@code CCLine} object.
     */
    public void add(CCLine line) {
        lines.add(line);
    }
    
    
    
    public void add(CCPoint point) {
        points.add(point);
    }
    
    
    
    public void add(CCPolygon polygon) {
        polygons.add(polygon);
    }
    
    
    
    /**
     * Remove all visible objects in the current system.
     */
    public void clear() {
        lines.clear();
        points.clear();
        polygons.clear();
        updateUI();
    }
    
    
    
    /* Move the visible area relevant to the current position. */ 
    private void drag(double moveX, double moveY) {
        minX += moveX;
        maxX += moveX;
        minY += moveY;
        maxY += moveY;
    }
    
    
    
    /*
     * Draw the axes and unit lines in the best looking way possible for the
     * given x- and y-ranges.
     */
    private void drawAxes(Graphics2D g2d) {
        if (axisXVisible) {
            g2d.setPaint(axisXPaint);
            g2d.setStroke(axisXStroke);
            g2d.drawLine(origin.x, 0, origin.x, getHeight());
            if (unitXVisible) {
                g2d.setPaint(unitXPaint);
                g2d.setStroke(unitXStroke);
                drawXUnitLines(g2d);
            }
        }
        if (axisYVisible) {
            g2d.setPaint(axisYPaint);
            g2d.setStroke(axisYStroke);
            g2d.drawLine(0, origin.y, getWidth(), origin.y);
            if (unitYVisible) {
                g2d.setPaint(unitYPaint);
                g2d.setStroke(unitYStroke);
                drawYUnitLines(g2d);
            }
        }
    }
    
    
    
    /*
     * Draw a grid for the coordinate system.
     */
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(0.1f));
        
        if (gridXVisible) drawXGridLines(g2d);
        if (gridYVisible) drawYGridLines(g2d);
    }
    
    
    
    /* Draw one vertical grid line. */
    private void drawXGridLine(Graphics2D g2d, double val) {
        int x = translateX(val);
        int y1 = translateY(minY);
        int y2 = translateY(maxY);
        g2d.drawLine(x, y1, x, y2);
    }
    
    
    
    /*
     * Draw vertical grid lines.
     */
    private void drawXGridLines(Graphics2D g2d) {
        drawXGridLines(g2d, gridRatio, gridXStroke, gridXPaint);
    }
    
    
    
    /*
     * Draw vertical grid lines a given amount of times between each unit line.
     * 
     * Use the given stroke and paint to draw the grid lines.
     */
    private void drawXGridLines(Graphics2D g2d, double ratio,
                               Stroke stroke, Paint paint) {
        double vbu = this.vbuX.doubleValue() / ratio;
        
        int idx = (int) Math.ceil(minX / vbu);
        int end = (int) Math.floor(maxX / vbu);
        
        g2d.setStroke(stroke);
        g2d.setPaint(paint);
        for (int i = idx; i <= end; i++) drawXGridLine(g2d, i*vbu);
    }
    
    
    /* Draw one horizontal grid line. */
    private void drawYGridLine(Graphics2D g2d, double val) {
        int y = translateY(val);
        int x1 = translateX(minX);
        int x2 = translateX(maxX);
        g2d.drawLine(x1, y, x2, y);
    }
    
    
    
    /*
     * Draw horizontal grid lines.
     */
    private void drawYGridLines(Graphics2D g2d) {
        drawYGridLines(g2d, gridRatio, gridYStroke, gridYPaint);
    }
    
    
    
    /*
     * Draw horizontal grid lines a given amount of times between each unit line
     * 
     * Use the given stroke and paint to draw the grid lines. 
     */
    private void drawYGridLines(Graphics2D g2d, double ratio,
                               Stroke stroke, Paint paint) {
        double vbu = this.vbuY.doubleValue() / ratio;
        
        int idx = (int) Math.ceil(minY / vbu);
        int end = (int) Math.floor(maxY / vbu);
        
        g2d.setStroke(stroke);
        g2d.setPaint(paint);
        for (int i = idx; i <= end; i++) drawYGridLine(g2d, i*vbu);
    }



    /*
     * Draw a Line.
     */
    private void drawLine(Graphics2D g2d, CCLine line) {
        g2d.setPaint(line.paint);
        g2d.setStroke(line.stroke);
        if (line.b == 0.0) drawLineVertical(g2d, line);
        else if (line.a == 0.0) drawLineHorizontal(g2d, line);
        else drawLineSlope(g2d, line);
    }



    /* Assume a == 0.0 */
    private void drawLineHorizontal(Graphics2D g2d, CCLine line) {
        int mul = (line.b  < 0) ? -1 : 1;
        double yval = line.c*mul;
        if (!validY(yval)) return; /* Don't draw lines off the screen. */
        
        int y = translateY(yval);
        int x1 = translateX(minX);
        int x2 = translateX(maxX);
        
        g2d.drawLine(x1, y, x2, y);
    }



    /* 
     * Draw a line with a defined slope.
     * 
     * Assume a, b != 0.
     */
    private void drawLineSlope(Graphics2D g2d, CCLine line) {
        /* Find intercepts with the display window */
        double i_minX = line.solveForY(minX);
        double i_maxX = line.solveForY(maxX);
        double i_minY = line.solveForX(minY);
        double i_maxY = line.solveForX(maxY);
        boolean v_minX = validY(i_minX);
        boolean v_maxX = validY(i_maxX);
        boolean v_minY = validX(i_minY);
        boolean v_maxY = validX(i_maxY);

        /* 
         * Possible intercept-pairs:
         *  1. minX and minY        2. minX and maxY        3. minX and maxX
         *  4. minY and maxX        5. minY and maxY        6. maxX and maxY
         */
        Point2D p2d1;
        Point2D p2d2;
        /* Special case, from corner to corner */
        if (v_minX && v_maxX && v_minY && v_maxY) {
            if (line.a < 0) {
                p2d1 = new Point2D.Double(minX, minY);
                p2d2 = new Point2D.Double(maxX, maxY);
            } else {
                p2d1 = new Point2D.Double(maxX, minY);
                p2d2 = new Point2D.Double(minX, maxY);
            }
        } else if (v_minX && v_minY) {
            p2d1 = new Point2D.Double(minX, i_minX);
            p2d2 = new Point2D.Double(i_minY, minY);
        } else if (v_minX && v_maxY) {
            p2d1 = new Point2D.Double(minX, i_minX);
            p2d2 = new Point2D.Double(i_maxY, maxY);
        } else if (v_minX && v_maxX) {
            p2d1 = new Point2D.Double(minX, i_minX);
            p2d2 = new Point2D.Double(maxX, i_maxX);
        } else if (v_minY && v_maxX) {
            p2d1 = new Point2D.Double(i_minY, minY);
            p2d2 = new Point2D.Double(maxX, i_maxX);
        } else if (v_minY && v_maxY) {
            p2d1 = new Point2D.Double(i_minY, minY);
            p2d2 = new Point2D.Double(i_maxY, maxY);
        } else if (v_maxX && v_maxY) {
            p2d1 = new Point2D.Double(maxX, i_maxX);
            p2d2 = new Point2D.Double(i_maxY, maxY);
        } else {
            return; /* Don't draw lines off the screen. */
        }

        Point p1 = translate(p2d1);
        Point p2 = translate(p2d2);

        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
    }



    /* Assume b == 0.0 */
    private void drawLineVertical(Graphics2D g2d, CCLine line) {
        int mul = (line.a < 0) ? -1 : 1;
        double xval = line.c*mul;
        if (!validX(xval)) return; /* Don't draw lines off the screen. */
        
        int x = translateX(xval);
        int y1 = translateY(minY);
        int y2 = translateY(maxY);
        
        g2d.drawLine(x, y1, x, y2);
    }
    
    
    
    /* Draw a point */
    private void drawPoint(Graphics2D g2d, CCPoint point) {
        Point p = translate(new Point2D.Double(point.x,  point.y));
        
        g2d.setPaint(point.paint);
        g2d.setStroke(point.stroke);
        Ellipse2D r2d = new Ellipse2D.Double(p.x-2, p.y-2, 4, 4);
        g2d.draw(r2d);
        g2d.fill(r2d);
    }
    
    
    
    /* Draw a polygon */
    private void drawPolygon(Graphics2D g2d, CCPolygon poly) {
        int num = poly.xpoints.length;
        int[] xpoints = new int[num];
        int[] ypoints = new int[num];
        
        for (int i = 0; i < num; i++) {
            xpoints[i] = translateX(poly.xpoints[i]);
            ypoints[i] = translateY(poly.ypoints[i]);
        }
        Polygon p = new Polygon(xpoints, ypoints, num);
        
        /* If the polygon has GradientPaint, translate the coordinates of GP */
        if (poly.fill instanceof GradientPaint) {
            g2d.setPaint(translateGradientPaint((GradientPaint) poly.fill));
            g2d.fill(p);
        } else if (poly.fill != null) {
            g2d.setPaint(poly.fill);
            g2d.fill(p);
        }
        
        if (poly.stroke != null && poly.paint != null) {
            g2d.setStroke(poly.stroke);
            g2d.setPaint(poly.paint);
            g2d.draw(p);
        }
    }



    /* Draw a single unit line on the x-axis at a given value. */
    private void drawXUnitLine(Graphics2D g2d, BigDecimal val) {
        /* Don't draw anything at the origin. */
        if (val.doubleValue() == 0.0) return;
        
        /* val is "small" if -10^7 < val < 10^7. */
        BigDecimal big = BigDecimal.valueOf(10000000);
        boolean small = val.compareTo(big) < 0
                && val.compareTo(big.negate()) > 0;
        
        /* 
         * When val is not "small", BigDecimal's toString does not use
         * scientific notation, so Double's toString is used instead.
         */
        String strval;
        if (small) strval = val.toString();
        else strval = Double.toString(val.doubleValue());
        
        Point2D.Double p2d = new Point2D.Double(val.doubleValue(), origin2d.y);
        Point p = translate(p2d);
        
        int strValPixels = 7 * strval.length();
        int offset = (minY >= -translateY(40)) ? -10 : 20;

        g2d.drawLine(p.x, p.y-ulSize, p.x, p.y+ulSize);
        g2d.drawString(strval, p.x - strValPixels/2, p.y + offset);
    }



    /* Draw all the unit lines on the x-axis. */ 
    private void drawXUnitLines(Graphics2D g2d) {
        /* 
         * The value at each unit line will now be defined as i * vbuX. We need
         * to find the value of i such that i * vbuX is the value at the first
         * visible unit line.
         */
        int idx = (int) Math.ceil(minX / vbuX.doubleValue());
        
        /* Also find the value of the last visible unit line. */
        int end = (int) Math.floor(maxX / vbuX.doubleValue());
        
        for (int i = idx; i <= end; i++) drawXUnitLine(g2d,
                BigDecimal.valueOf(i).multiply(vbuX, prec));
    }



    /* Draw a single unit line on the y-axis at a given value. */
    private void drawYUnitLine(Graphics2D g2d, BigDecimal val) {
        if (val.doubleValue() == 0.0) return;

        BigDecimal big = BigDecimal.valueOf(10000000);
        boolean small = val.compareTo(big) < 0
                && val.compareTo(big.negate()) > 0;

        String strval;
        if (small) strval = val.toString();
        else strval = Double.toString(val.doubleValue());
        
        Point2D.Double p2d = new Point2D.Double(origin2d.x, val.doubleValue());
        Point p = translate(p2d);
        
        int strValPixels = 7 * strval.length() + 7;
        int offset = (minX >= -translateX(strValPixels*2)) ? 5 : -strValPixels;
        
        g2d.drawLine(p.x-ulSize, p.y, p.x+ulSize, p.y);
        g2d.drawString(strval, p.x+offset, p.y+5);
    }



    /* Draw all the unit lines on the x-axis. */ 
    private void drawYUnitLines(Graphics2D g2d) {
        int idx = (int) Math.ceil(minY / vbuY.doubleValue());
        int end = (int) Math.floor(maxY / vbuY.doubleValue());
        
        
        for (int i = idx; i <= end; i++) drawYUnitLine(g2d,
                BigDecimal.valueOf(i).multiply(vbuY, prec));
    }
    
    
    
    private BigDecimal findScale(double num) {
        int x = (int) Math.floor(Math.log10(num));
        
        BigDecimal scale;
        try {
            scale = BigDecimal.TEN.pow(x, prec);
        } catch (ArithmeticException e) {
            scale = BigDecimal.valueOf(Double.MAX_VALUE);
        }
        
        /* Don't need more than double precision here */
        double quot = num / scale.doubleValue();
        if (quot > 5.0) return scale.multiply(BigDecimal.TEN, prec);
        if (quot > 2.0) return scale.multiply(BigDecimal.valueOf(5), prec);
        if (quot > 1.0) return scale.multiply(BigDecimal.valueOf(2), prec);
        else return scale;
    }



    /* 
     * Round this exact value to a value (of the same magnitude) that can be
     * written with very few decimals.
     */
//    private double findScale(double num) {
//        int x = (int) Math.floor(Math.log10(num));
//        double scale = Math.pow(10, x);
//        
//        double quot = num / scale;
//        if (quot > 5.0) return 10*scale;
//        if (quot > 2.0) return 5*scale;
//        if (quot > 1.0) return 2*scale;
//        else return scale;
//    }
    
    
    
    private RenderingHints getNiceGraphics() {
        RenderingHints rh = new RenderingHints(null);
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        rh.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        rh.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        rh.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return rh;
    }
    
    
    
    /**
     * Moves the entire visible area of the current system. The visible area
     * will be x and y in [loX, hiX] and [loY, hiY], respectively.
     * 
     * @param loX
     *        Lowest visible value of x. 
     * @param hiX
     *        Highest visible value of x.
     * @param loY
     *        Lowest visible value of y.
     * @param hiY
     *        Highest visible value of y.
     */
    public void move(double loX, double hiX, double loY, double hiY) {
        this.minX = loX;
        this.maxX = hiX;
        this.minY = loY;
        this.maxY = hiY;
    }
    
    
    
    public void paintComponent(Graphics g) {
    	if (g == null) return;
    	VectorGraphics g2d = VectorGraphics.create(g);
    	Dimension dim = getSize();
    	Insets insets = getInsets();
    	g2d.setColor(Color.white);
    	g2d.fillRect(insets.left, insets.top,
                     dim.width-insets.left-insets.right,
                     dim.height-insets.top-insets.bottom);
    	/*替换为VectorGraphics by @luo*/
    	/*super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;*/
		
        updatePosition();
        
        if (niceGraphics) g2d.addRenderingHints(getNiceGraphics());
        
        for (CCPolygon p : polygons) drawPolygon(g2d, p);
        for (CCLine line : lines) drawLine(g2d, line);
        
        drawGrid(g2d);
        drawAxes(g2d);
        
        for (CCPoint p : points) drawPoint(g2d, p);
    }
    
    
    
    /**
     * Set which paint the axes should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the axes with.
     */
    public void setAxesPaint(Paint paint) {
        axisXPaint = paint;
        axisYPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the axes should be painted with.
     * 
     * @param stroke
     *        {@Code Stroke} to paint the axes with.
     */
    public void setAxesStroke(Stroke stroke) {
        axisXStroke = stroke;
        axisYStroke = stroke;
    }



    /**
     * Set whether the axes should be visible or not.
     * 
     * @param visible
     *        If true, the axes are drawn. Otherwise they are hidden.
     */
    public void setAxesVisible(boolean visible) {
        axisXVisible = visible;
        axisYVisible = visible;
    }
    
    
    
    /**
     * Set which paint the x-axis should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the x-axis with.
     */
    public void setAxisXPaint(Paint paint) {
        axisXPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the x-axis should be painted with.
     * 
     * @param stroke
     *        {@Code Stroke} to paint the x-axis with.
     */
    public void setAxisXStroke(Stroke stroke) {
        axisXStroke = stroke;
    }



    /**
     * Set whether the x-axis should be visible or not.
     * 
     * @param visible
     *        If true, the x-axis is drawn. Otherwise it is hidden.
     */
    public void setAxisXVisible(boolean visible) {
        axisXVisible = visible;
    }



    /**
     * Set which paint the y-axis should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the y-axis with.
     */
    public void setAxisYPaint(Paint paint) {
        axisYPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the y-axis should be painted with.
     * 
     * @param stroke
     *        {@Code Stroke} to paint the y-axis with.
     */
    public void setAxisYStroke(Stroke stroke) {
        axisYStroke = stroke;
    }
    
    
    
    /**
     * Set whether the y-axis should be visible or not.
     * 
     * @param visible
     *        If true, the y-axis is drawn. Otherwise it is hidden.
     */
    public void setAxisYVisible(boolean visible) {
        axisYVisible = visible;
    }
    
    
    
    /**
     * Set which paint the grid should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the grid with.
     */
    public void setGridPaint(Paint paint) {
        gridXPaint = paint;
        gridYPaint = paint;
    }
    
    
    
    /**
     * Set the number of grid lines between each unit line.
     * 
     * @param ratio
     *        The number of grid lines between each unit line.
     */
    public void setGridRatio(double ratio) {
        gridRatio = ratio;
    }
    
    
    
    /**
     * Set which stroke the grid should be painted with.
     * 
     * @param stroke
     *        {@Code Stroke} to paint the grid with.
     */
    public void setGridStroke(Stroke stroke) {
        gridXStroke = stroke;
        gridYStroke = stroke;
    }



    /**
     * Set whether the grid should be visible or not.
     * 
     * @param visible
     *        If true, the grid is drawn. Otherwise it is hidden.
     */
    public void setGridVisible(boolean visible) {
        gridXVisible = visible;
        gridYVisible = visible;
    }
    
    
    
    /**
     * Set which paint the horizontal grid-lines should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the horizontal grid-lines with.
     */
    public void setGridXPaint(Paint paint) {
        gridXPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the horizontal grid-lines should be painted with.
     * 
     * @param paint
     *        {@code Stroke} to paint the horizontal grid-lines with.
     */
    public void setGridXStroke(Stroke stroke) {
        gridXStroke = stroke;
    }



    /**
     * Set whether the horizontal grid-lines should be visible or not.
     * 
     * @param visible
     *        If true, the horizontal grid lines are drawn.
     *        Otherwise they are hidden.
     */
    public void setGridXVisible(boolean visible) {
        gridXVisible = visible;
    }



    /**
     * Set which paint the vertical grid-lines should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the vertical grid-lines with.
     */
    public void setGridYPaint(Paint paint) {
        gridYPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the vertical grid-lines should be painted with.
     * 
     * @param stroke
     *        {@Code Stroke} to paint the vertical grid-lines with.
     */
    public void setGridYStroke(Stroke stroke) {
        gridYStroke = stroke;
    }
    
    
    
    /**
     * Set whether the vertical grid-lines should be visible or not.
     * 
     * @param visible
     *        If true, the vertical grid-lines are drawn.
     *        Otherwise they are hidden.
     */
    public void setGridYVisible(boolean visible) {
        gridYVisible = visible;
    }
    
    
    
    /**
     * Set which paint the unit lines should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the unit lines with.
     */
    public void setUnitsPaint(Paint paint) {
        unitXPaint = paint;
        unitYPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the unit lines should be painted with.
     * 
     * @param stroke
     *        {@code Stroke} to paint the unit lines lines with.
     */
    public void setUnitsStroke(Stroke stroke) {
        unitXStroke = stroke;
        unitYStroke = stroke;
    }
    
    
    
    /**
     * Set whether the unit lines on the axes should be visible or not.
     * 
     * @param visible
     *        If true, unit lines are drawn on the axes.
     *        Otherwise they are hidden.
     */
    public void setUnitsVisible(boolean visible) {
        unitXVisible = visible;
        unitYVisible = visible;
    }
    
    
    
    /**
     * Set which paint the unit lines on the x-axis should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the unit lines on the x-axis with.
     */
    public void setUnitXPaint(Paint paint) {
        unitXPaint = paint;
        unitYPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the unit lines on the x-axis should be painted with.
     * 
     * @param stroke
     *        {@code Stroke} to paint the unit lines on the x-axis lines with.
     */
    public void setUnitXStroke(Stroke stroke) {
        unitXStroke = stroke;
        unitYStroke = stroke;
    }
    
    
    
    /**
     * Set whether the unit lines on the x-axis should be visible or not.
     * 
     * @param visible
     *        If true, unit lines are drawn on the x-axis.
     *        Otherwise they are hidden.
     */
    public void setUnitXVisible(boolean visible) {
        unitXVisible = visible;
    }
    
    
    
    /**
     * Set which paint the unit lines on the y-axis should be painted with.
     * 
     * @param paint
     *        {@code Paint} to paint the unit lines on the y-axis with.
     */
    public void setUnitYPaint(Paint paint) {
        unitXPaint = paint;
        unitYPaint = paint;
    }
    
    
    
    /**
     * Set which stroke the unit lines on the y-axis should be painted with.
     * 
     * @param stroke
     *        {@code Stroke} to paint the unit lines on the y-axis lines with.
     */
    public void setUnitYStroke(Stroke stroke) {
        unitXStroke = stroke;
        unitYStroke = stroke;
    }
    
    
    
    /**
     * Set whether the unit lines on the y-axis should be visible or not.
     * 
     * @param visible
     *        If true, unit lines are drawn on the y-axis.
     *        Otherwise they are hidden.
     */
    public void setUnitYVisible(boolean visible) {
        unitYVisible = visible;
    }
    
    
    
    /**
     * Set whether the coordinate system is movable with the mouse, i.e.
     * the scope of the system changes as the the mouse is clicked, held
     * and dragged over the system.
     * 
     * @param movable
     *        If true, move is possible.
     */
    public void setMovable(boolean movable) {
        if (this.movable && movable) return;
        if (!this.movable && !movable) return;
        
        if (movable) {
            addMouseListener(mouseListener);
            addMouseMotionListener((MouseMotionListener) mouseListener);
        }
        else {
            removeMouseListener(mouseListener);
            removeMouseMotionListener((MouseMotionListener) mouseListener);
        }
        
        movable = !movable;
    }
    
    
    
    /**
     * Turn on nice graphics.
     * <p>
     * More specifically:
     * <pre>
     *     KEY_ALPHA_INTERPOLATION = VALUE_ALPHA_INTERPOLATION_QUALITY
     *     KEY_ANTIALIASING = VALUE_ANTIALIAS_ON
     *     KEY_COLOR_RENDERING = VALUE_COLOR_RENDER_QUALITY
     *     KEY_DITHERING = VALUE_DITHER_ENABLE
     *     KEY_FRACTIONALMETRICS = VALUE_FRACTIONALMETRICS_ON
     *     KEY_INTERPOLATION = VALUE_INTERPOLATION_BICUBIC
     *     KEY_RENDERING = VALUE_RENDER_QUALITY
     *     KEY_STROKE_CONTROL = VALUE_STROKE_NORMALIZE
     *     KEY_TEXT_ANTIALIASING = VALUE_TEXT_ANTIALIAS_ON.
     * </pre>
     * 
     * @param niceGraphics
     *        If true, use nice graphics.
     */
    public void setNiceGraphics(boolean niceGraphics) {
        this.niceGraphics = niceGraphics;
    }
    
    
    
    /**
     * Set whether it is possible to zoom in/out in the coordinate
     * system by scrolling the mouse wheel.
     * 
     * @param zoomable
     *        If true, zoom is possible.
     */
    public void setZoomable(boolean zoomable) {
        if (this.zoomable && zoomable) return;
        if (!this.zoomable && !zoomable) return;
        
        if (zoomable) addMouseWheelListener(mouseWheelListener);
        else removeMouseWheelListener(mouseWheelListener);
        
        zoomable = !zoomable;
    }



    /*
     * Convert points from system 1 to system 2.
     */
    private Point2D.Double translate(Point p) {
        double x = p.x * xscale + minX;
        double y = p.y * yscale + minY;

        return new Point2D.Double(x, y);
    }
    
    
    
    /* Translate a given point from System 2 to System 1. */
    private Point translate(Point2D p2d) {
        return translate(p2d.getX(), p2d.getY());
    }
    
    
    
    /* Translate the point (x, y) from System 2 to System 1. */
    private Point translate(double x, double y) {
        return new Point(translateX(x), translateY(y));
    }
    
    
    
    private GradientPaint translateGradientPaint(GradientPaint gp) {
        Point p1 = translate(gp.getPoint1());
        Point p2 = translate(gp.getPoint2());

        Color c1 = gp.getColor1();
        Color c2 = gp.getColor2();

        return new GradientPaint(p1, c1, p2, c2);
    }
    
    
    
    /* Translate a single x-coordinate from System 2 to System 1. */
    private int translateX(double x) {
        return (int) Math.round((x - minX) / xscale);
    }

    private double translateX(int x) {
        return x * xscale + minX;
    }

    private double translateY(int y) {
        return y * yscale + minY;
    }

    
    
    /* 
     * Translate a single y-coordinate from System 2 to System 1.
     * 
     * Subtract from getHeight() since increasing y goes
     * south in System 1 but north in System 2.
     */
    private int translateY(double y) {
        return getHeight() - (int) Math.round((y - minY) / yscale);
    }
    
    
    
    private void updatePosition() {
        distX = maxX - minX;
        distY = maxY - minY;
        
        xscale = distX / getWidth();
        yscale = distY / getHeight();
        
        /* Total number of units on the axis */
        unitsX = getWidth() / ulScale;
        unitsY = getHeight() / ulScale;
        
        /* Exact value between each unit line */
        udistX = distX / unitsX;
        udistY = distY / unitsY;
        
        vbuX = findScale(udistX);
        vbuY = findScale(udistY);
        
        /* Find origin */
        double ox = 0;
        double oy = 0;
        
        /* 
         * Place origin along the edges of the screen if 
         * (0, 0) is not in the visible area.
         */
        if (minX >= 0) ox = minX;
        else if (maxX <= 0) ox = maxX;
        
        if (minY >= 0) oy = minY;
        else if (maxY <= 0) oy = maxY;
        
        origin2d = new Point2D.Double(ox, oy);
        origin = translate(origin2d);
    }
    
    
    
    private boolean valid(double x, double y) {
        return validX(x) && validY(y);
    }
    
    
    
    private boolean valid(Point2D p2d) {
        return valid(p2d.getX(), p2d.getY());
    }
    
    
    
    private boolean validX(double x) {
        return (x >= minX && x <= maxX);
    }
    
    
    
    private boolean validY(double y) {
        return (y >= minY && y <= maxY);
    }
    
    
    
    /*
     * Zoom into the visible area relevant to the current
     * position by keeping the center the same.
     */
    private void zoom(double zoomX, double zoomY) {
        minX -= zoomX;
        maxX += zoomX;
        minY -= zoomY;
        maxY += zoomY;
    }
    
    
    
    /**
     * A {@code MouseWheelListener} making it possible to zoom in and out
     * of the coordinate system using the mouse wheel.
     */
    class mouseWheelListener implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int units = e.getUnitsToScroll();

            double zoomx = distX / 100.0 * units;
            double zoomy = distY / 100.0 * units;

            zoom(zoomx, zoomy);

            repaint();
        }
    }
    
    
    
    /**
     * A {@code MouseListener} making it possible to click and drag to move the
     * position of the coordinate system.
     */
    class mouseListener implements MouseListener, MouseMotionListener {
        private int lastX;
        private int lastY;

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            
            int dx = lastX - x;
            int dy = lastY - y;

            double moveX = distX / getWidth() * dx;
            double moveY = distY / getHeight() * dy;
            
            drag(moveX, -moveY);

            repaint();
            
            lastX = x;
            lastY = y;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
        }
    }
}
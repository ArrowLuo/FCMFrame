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
package cartesian;

/**
 * A {@code Class} containing a grammar for the String parser.
 * 
 * @author Andreas Halle
 */
public class Representiatons {
    /*
     * Using this makes definitions that are space-insensitive much more
     * readable.
     * NOTE: This one alone matches "". 
     */
    private static final String S = "\\s*";
    
    
    
    /**
     * Matches an integer or a decimal number.
     * <p>
     * Examples of allowed forms:
     * <pre>
     *     50
     *     5.
     *     3.1415
     *     .020
     * </pre>
     * <p>
     * There is no limit on quantity of numbers after the decimal point. Leading
     * and trailing zeroes are allowed.
     */
    public static final String NUM = "(?:\\d+(?:\\.\\d*)?|\\.\\d+)";
    
    /**
     * Variables might contain letters, underscores and digits. Variables
     * <b>must</b> start with a letter.
     * <p>
     * Examples of allowed forms:
     * <pre>
     *     x1
     *     x_1
     *     y101
     *     var_1
     * </pre>
     */
    public static final String VAR = "[a-z_]+\\d*";
    
    /**
     * Slope-intercept form:
     * <pre>
     *     y = mx + b
     * </pre>
     * where x and y are variables while m and b are constants.
     * <p>
     * Allowed forms:
     * <pre>
     *     y = mx + b
     *     y = m*x + b
     *     y = mx
     * </pre>
     * <p>
     * The forms are space-insensitive, even between m and x.
     */
    public static final String SLOPEINTERCEPTFORM2D =
            VAR + S + "=" + S + NUM + "\\*?" + VAR + "(?:" + S + "\\+" + S + NUM + ")?";
}
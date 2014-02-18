/*
 * Copyright The Sett Ltd, 2005 to 2009.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.common.geom;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Edge2D is a container class for two {@link java.awt.geom.Line2D} extension classes that provide extra methods to the
 * Line2D class. A container class is used because there are seperate float and double implementaions of line segments
 * and the same convention as used in the java.awt.geom package is used for naming these classes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide equality checking and hash codes for line segments.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class Edge2D
{
    /**
     * Edge2D.Double enhances {@link java.awt.geom.Line2D.Double} with an equality method.
     */
    public static class Double extends Line2D.Double
    {
        /**
         * Creates a new Double edge object.
         *
         * @param p1 The double precision start point of the edge.
         * @param p2 The double precision end point of the edge.
         */
        public Double(Point2D p1, Point2D p2)
        {
            super(p1, p2);
        }

        /**
         * Compares two edges for equality. They are equal if they have equal start and end points.
         *
         * @param  o The object to compare with this one for being a line segment.
         *
         * @return True if they have equal start and end points.
         */
        public boolean equals(Object o)
        {
            if (o instanceof Line2D.Double)
            {
                Line2D.Double compare = (Line2D.Double) o;

                return compare.getP1().equals(getP1()) && compare.getP2().equals(getP2());
            }

            return false;
        }

        /**
         * Computes a hash code of this line segment by adding the hash codes of its start and end points.
         *
         * @return A hash code of this.
         */
        public int hashCode()
        {
            return getP1().hashCode() + getP2().hashCode();
        }
    }

    /**
     * Edge2D.Float enhances {@link java.awt.geom.Line2D.Float} with an equality method.
     */
    public static class Float extends Line2D.Double
    {
        /**
         * Creates a new Float edge object.
         *
         * @param p1 The single precision start point of the edge.
         * @param p2 The single precision end point of the edge.
         */
        public Float(Point2D p1, Point2D p2)
        {
            super(p1, p2);
        }

        /**
         * Compares two edges for equality. They are equal if they have equal start and end points.
         *
         * @param  o The object to compare with this one for being a line segment.
         *
         * @return True if they have equal start and end points.
         */
        public boolean equals(Object o)
        {
            if (o instanceof Line2D.Float)
            {
                Line2D.Float compare = (Line2D.Float) o;

                return compare.getP1().equals(getP1()) && compare.getP2().equals(getP2());
            }

            return false;
        }

        /**
         * Computes a hash code of this line segment by adding the hash codes of its start and end points.
         *
         * @return A hash code of this.
         */
        public int hashCode()
        {
            return getP1().hashCode() + getP2().hashCode();
        }
    }
}

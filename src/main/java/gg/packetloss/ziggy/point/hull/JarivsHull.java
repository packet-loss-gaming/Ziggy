/*
 * Copyright (c) 2020 Wyatt Childers.
 *
 * This file is part of Ziggy.
 *
 * Ziggy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ziggy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Ziggy.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gg.packetloss.ziggy.point.hull;

import gg.packetloss.ziggy.point.Point2D;

import java.util.ArrayList;
import java.util.List;

public class JarivsHull implements HullSolver {
    private enum CCWState {
        COLINEAR,
        CLOCKWISE,
        COUNTERCLOCKWISE
    }

    /**
     * Calculates the direction based on the cross product.
     */
    private CCWState ccw(Point2D p1, Point2D p2, Point2D p3) {
        int val = (p2.getZ() - p1.getZ()) * (p3.getX() - p2.getX()) -
                  (p2.getX() - p1.getX()) * (p3.getZ() - p2.getZ());

        if (val == 0) return CCWState.COLINEAR;
        return (val > 0) ? CCWState.CLOCKWISE : CCWState.COUNTERCLOCKWISE;
    }

    @Override
    public List<Point2D> hull(List<Point2D> points) {
        if (points.size() < 4) {
            return new ArrayList<>(points);
        }

        // Find the left most point
        int left = 0;
        for (int i = 0; i < points.size(); ++i) {
            if (points.get(i).getX() < points.get(left).getX()) {
                left = i;
            }
        }

        List<Point2D> hullPoints = new ArrayList<>(4);

        // Setup the left value
        int p = left;
        hullPoints.add(points.get(p));

        while (true) {
            int q = (p + 1) % points.size();

            for (int i = 0; i < points.size(); ++i) {
                if (ccw(points.get(p), points.get(i), points.get(q)) == CCWState.COUNTERCLOCKWISE) {
                    q = i;
                }
            }

            p = q;

            if (p == left) {
                break;
            }

            hullPoints.add(points.get(q));
        }

        return hullPoints;
    }
}

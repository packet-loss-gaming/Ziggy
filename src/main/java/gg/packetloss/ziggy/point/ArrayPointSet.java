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

package gg.packetloss.ziggy.point;

import java.util.*;

public class ArrayPointSet implements Set<Point2D> {
    private List<Point2D> pointList = new ArrayList<>();

    public ArrayPointSet() { }

    public ArrayPointSet(List<Point2D> pointList) {
        addAll(pointList);
    }

    public ArrayPointSet(ArrayPointSet pointSet) {
        pointList.addAll(pointSet);
    }

    public Point2D get(int index) {
        return pointList.get(index);
    }

    @Override
    public boolean add(Point2D point) {
        for (Point2D existingPoint : pointList) {
            if (existingPoint.equals(point)) {
                return false;
            }
        }

        pointList.add(point);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return pointList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return pointList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Point2D> c) {
        boolean changed = false;
        for (Point2D point : c) {
            changed |= add(point);
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return pointList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        pointList.clear();
    }

    public int size() {
        return pointList.size();
    }

    @Override
    public boolean isEmpty() {
        return pointList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return pointList.contains(o);
    }

    @Override
    public Iterator<Point2D> iterator() {
        return pointList.iterator();
    }

    @Override
    public Object[] toArray() {
        return pointList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return pointList.toArray((T[]) a);
    }

    public List<Point2D> asList() {
        return new ArrayList<>(pointList);
    }
}

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

package gg.packetloss.ziggy.bukkit;

import java.util.Calendar;

class TimingSupport {
    private static final Calendar CALENDAR = Calendar.getInstance();

    /**
     * Gets the ticks till a given base 24 hour
     *
     * @param hour      The hour, for example 13 is 1 P.M.
     * @return the number of ticks till the given time
     */
    public static long getTicksTill(int hour) {
        Calendar localCalendar = Calendar.getInstance();
        long returnValue;

        localCalendar.add(Calendar.MINUTE, 60 - localCalendar.get(Calendar.MINUTE));

        while (localCalendar.get(Calendar.HOUR_OF_DAY) != hour) {
            localCalendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        returnValue = localCalendar.getTimeInMillis() - CALENDAR.getTimeInMillis();
        returnValue /= 1000; // To seconds
        returnValue *= 20; // To Ticks

        return returnValue;
    }

    public static long convertSecondsToTicks(int seconds) {
        return seconds * 20;
    }

    public static long convertMinutesToTicks(int minutes) {
        return convertSecondsToTicks(minutes * 60);
    }

    public static long convertHoursToTicks(int hours) {
        return convertMinutesToTicks(hours * 60);
    }
}

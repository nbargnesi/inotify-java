/**
 * Copyright © 2009 Nick Bargnesi <nick@den-4.com>.  All rights reserved.
 *
 * inotify-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * inotify-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with inotify-java.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File: EventModifier.java
 * Project: inotify-trunk
 * Package: com.den_4.inotify_java.enums
 * Author: Nick Bargnesi
 */

package com.den_4.inotify_java.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bitmask-style enumerations amplifying received events.
 * 
 * @author Nick Bargnesi
 */
public enum EventModifier {

    /**
     * A watch was removed explicitly or automatically due to a deletion or
     * unmount.
     */
    Ignored(0x00008000),

    /** Received event concerns a directory. */
    Is_Directory(0x40000000),

    /** The event queue has overflowed. */
    Event_Queue_Overflow(0x00004000),

    /** The filesystem on which the the file or directory resides was unmounted. */
    Unmount(0x00002000);

    /** Unique value associated with each enumeration. */
    private final int value;

    private final static Map<String, EventModifier> STRINGTOENUM = new HashMap<String, EventModifier>();

    static {
        for (EventModifier e : values())
            STRINGTOENUM.put(e.toString(), e);
    }

    /**
     * Constructor for setting the private field {@code #value}.
     * 
     * @param value integer value
     */
    EventModifier(int value) {
        this.value = value;
    }

    /**
     * Returns the integral value associated with the event modifier.
     * 
     * @return int
     * @see #ordinal()
     */
    public int value() {
        return value;
    }

    /**
     * Returns the event modifier associated to the specified string, or null if
     * no association exists.
     * 
     * @param s Conversion value
     * @return event modifier
     */
    public EventModifier fromString(String s) {
        return STRINGTOENUM.get(s);
    }

    /**
     * Returns the {@code EventModifier} associated to the specified value.
     * 
     * @param value Conversion value
     * @return event modifier
     */
    public static EventModifier fromValue(int value) {
        for (EventModifier e : values())
            if (e.value == value)
                return e;
        throw new IllegalArgumentException("bad event modifier value: " + value);
    }

    /**
     * Returns {@code true} if the specified event modifier is set in the mask,
     * {@code false} otherwise.
     * 
     * @param m Event modifier such that the statement, {@code e.value & mask},
     * returns {@code true} or {@code false}
     * @param mask Mask value such that the statement, {@code e.value & mask},
     * returns {@code true} or {@code false}
     * @return {@code true} if {@code mask} is set in {@code e}, {@code false}
     * otherwise
     */
    public static boolean isSet(EventModifier m, int mask) {
        if (m.value == (m.value & mask))
            return true;
        return false;
    }

    /**
     * Returns an array of EventModifier enums set in the specified {@code mask}
     * .
     * 
     * @param mask Mask to convert
     * @return EventModifier[]
     */
    public static EventModifier[] maskToEventModifiers(int mask) {
        List<EventModifier> mods = new ArrayList<EventModifier>();

        for (EventModifier m : values())
            if (m.value == (m.value & mask))
                mods.add(m);

        return mods.toArray(new EventModifier[0]);
    }

    /**
     * Returns the <b>bitwise or</b> mask of the event modifiers provided.
     * 
     * @param eventModifiers modifiers to operate on
     * @return mask of provided event modifiers
     */
    public static int eventModifiersToMask(EventModifier... eventModifiers) {
        int mask = 0;

        for (EventModifier m : eventModifiers)
            mask |= m.value;

        return mask;
    }
}

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
 * File: WatchModifier.java
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
 * Bitmask-style enumerations extending the
 * {@link com.den_4.inotify_java.enums.Event}, limited to watch additions.
 * 
 * @author Nick Bargnesi
 */
public enum WatchModifier {

    /** Symlinks are not dereferenced. */
    No_Dereference_Symlinks(0x02000000),

    /** Events are added to existing watches, instead of replacing old events. */
    Add(0x20000000),

    /** Watch for events once, then remove from the watch list. */
    Oneshot(0x80000000),

    /** Only watch directories. */
    Only_Directories(0x01000000);

    /** Unique value associated with each enumeration. */
    private final int value;

    private final static Map<String, WatchModifier> STRINGTOENUM = new HashMap<String, WatchModifier>();

    static {
        for (WatchModifier w : values())
            STRINGTOENUM.put(w.toString(), w);
    }

    /**
     * Constructor for setting the private field {@code #value}.
     * 
     * @param value integer value
     */
    WatchModifier(int value) {
        this.value = value;
    }

    /**
     * Returns the integral value associated with the watch modifier.
     * 
     * @return int
     * @see #ordinal()
     */
    public int value() {
        return value;
    }

    /**
     * Returns the watch modifier associated to the specified string, or null if
     * no association exists.
     * 
     * @param s Coversion value
     * @return Watch modifier
     */
    public WatchModifier fromString(String s) {
        return STRINGTOENUM.get(s);
    }

    /**
     * Returns the {@code WatchModifier} associated to the specified value.
     * 
     * @param value Conversion value
     * @return Watch modifier
     */
    public static WatchModifier fromValue(int value) {
        for (WatchModifier w : values())
            if (w.value == value)
                return w;
        throw new IllegalArgumentException("bad watch modifier value: " + value);
    }

    /**
     * Returns {@code true} if the specified watch modifier is set in the mask,
     * {@code false} otherwise.
     * 
     * @param m Watch modifier such that the statement, {@code e.value & mask},
     * returns {@code true} or {@code false}
     * @param mask Mask value such that the statement, {@code e.value & mask},
     * returns {@code true} or {@code false}
     * @return {@code true} if {@code mask} is set in {@code e}, {@code false}
     * otherwise
     */
    public static boolean isSet(WatchModifier m, int mask) {
        if (m.value == (m.value & mask))
            return true;
        return false;
    }

    /**
     * Returns an array of WatchModifier enums set in the specified mask.
     * 
     * @param mask Mask to convert
     * @return WatchModifier[]
     */
    public static WatchModifier[] maskToWatchModifiers(int mask) {
        List<WatchModifier> mods = new ArrayList<WatchModifier>();

        for (WatchModifier m : values())
            if (m.value == (m.value & mask))
                mods.add(m);

        return mods.toArray(new WatchModifier[0]);
    }

    /**
     * Returns the <b>bitwise or</b> mask of the watch modifiers provided.
     * 
     * @param watchModifiers modifiers to operate on
     * @return mask of provided watch modifiers
     */
    public static int watchModifiersToMask(WatchModifier... watchModifiers) {
        int mask = 0;

        for (WatchModifier m : watchModifiers)
            mask |= m.value;

        return mask;
    }
}

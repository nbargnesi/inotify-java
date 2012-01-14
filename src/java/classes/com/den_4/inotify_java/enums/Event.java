/**
 * Copyright © 2009-2012 Nick Bargnesi <nick@den-4.com>. All rights reserved.
 *
 * inotify-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * inotify-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with inotify-java. If not, see <http://www.gnu.org/licenses/>.
 *
 * File: Event.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */

package com.den_4.inotify_java.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bitmask-style enumerations for amplifying watch additions and events.
 * <p>
 * When a directory is being monitored, events that can occur for files in that
 * directory are defined as <em>directory events</em>, refer to
 * {@link #isDirectoryEvent(Event)}.
 * </p>
 * 
 * @author Nick Bargnesi
 */
public enum Event {

    /**
     * The access of a file or directory. This event qualifies as a
     * <em>directory event</em>.
     */
    Access(0x00000001),

    /**
     * The closing of a file not opened for writing. This event qualifies as a
     * <em>directory event</em>.
     */
    Close_No_Write(0x00000010),

    /**
     * The closing of a file opened for writing. This event qualifies as a
     * <em>directory event</em>.
     */
    Close_Write(0x00000008),

    /**
     * The closing of a file. This event qualifies as a <em>directory event</em>
     * .
     */
    Close(Close_No_Write.value | Close_Write.value),

    /**
     * The creation of a file. This event qualifies as a
     * <em>directory event</em>.
     */
    Create(0x00000100),

    /**
     * A file or directory being deleted from a watched directory. This event
     * qualifies as a <em>directory event</em>.
     */
    Delete(0x00000200),

    /**
     * A watched file or directory being deleted.
     */
    Delete_Self(0x00000400),

    /**
     * The changing of metadata on a directory or file. Examples of metadata
     * changes include permissions, extended attributes, link counts, et cetera.
     * This event qualifies as a <em>directory event</em>.
     */
    Metadata(0x00000004),
    /**
     * A file modification. This event qualifies as a <em>directory event</em>.
     */
    Modify(0x00000002),

    /**
     * A watched file or directory moving.
     */
    Move_Self(0x00000800),

    /**
     * A file moving out of a watched directory. This event qualifies as a
     * <em>directory event</em>.
     */
    Moved_From(0x00000040),

    /**
     * A file moving into a watched directory. This event qualifies as a
     * <em>directory event</em>.
     */
    Moved_To(0x00000080),

    /**
     * A file moving out of or into a watched directory. This event qualifies as
     * a <em>directory event</em>.
     */
    Moved(Moved_From.value | Moved_To.value),

    /**
     * The opening of a file. This event qualifies as a <em>directory event</em>
     * .
     */
    Open(0x00000020),

    /**
     * Representation of every {@code Event} enumeration.
     */
    All(Access.value | Close.value | Create.value | Delete.value
            | Delete_Self.value | Metadata.value | Modify.value
            | Move_Self.value
            | Moved.value | Open.value);

    /** Unique value associated with each enumeration. */
    private final int value;

    private final static Map<String, Event> STRINGTOENUM;

    static {
        STRINGTOENUM = new HashMap<>();
        for (Event e : values())
            STRINGTOENUM.put(e.toString(), e);
    }

    /**
     * Constructor for setting the private field {@code #value}.
     * 
     * @param value integer value
     */
    Event(int value) {
        this.value = value;
    }

    /**
     * Returns the integral value associated with the event.
     * 
     * @return int
     * @see #ordinal()
     */
    public int value() {
        return value;
    }

    /**
     * Returns the event associated to the specified string, or null if no
     * association exists.
     * 
     * @param s Conversion value
     * @return event
     */
    public static Event fromString(String s) {
        return STRINGTOENUM.get(s);
    }

    /**
     * Returns {@code true} if the specified event is a directory event,
     * {@code false} otherwise. Events that qualify as directory events are
     * defined by
     * their ability to occur for files in a watched directory. The following
     * events are considered <em>directory events</em>:
     * <ol>
     * <li>{@link #Access}</li>
     * <li>{@link #Metadata}</li>
     * <li>{@link #Close_No_Write}</li>
     * <li>{@link #Close_Write}</li>
     * <li>{@link #Close}</li>
     * <li>{@link #Create}</li>
     * <li>{@link #Delete}</li>
     * <li>{@link #Modify}</li>
     * <li>{@link #Moved_From}</li>
     * <li>{@link #Moved_To}</li>
     * <li>{@link #Moved}</li>
     * <li>{@link #Open}</li>
     * </ol>
     * 
     * @param e Event to test
     * @return {@code true} if event {@code e} is a directory event,
     * {@code false} otherwise
     * @since Version 2
     */
    public static boolean isDirectoryEvent(Event e) {
        if (e == Move_Self || e == Delete_Self)
            return false;
        return true;
    }

    /**
     * Returns the {@code Event} associated to the specified value.
     * 
     * @param value Conversion value
     * @return Event
     */
    public static Event fromValue(int value) {
        for (Event e : values()) {
            if (e.value == value) return e;
        }
        throw new IllegalArgumentException("bad event value: " + value);
    }

    /**
     * Returns {@code true} if the specified event is set in the mask,
     * {@code false} otherwise.
     * 
     * @param e Event to be evaluated as the first operand in:
     * <tt>e.value & mask</tt>
     * @param mask Mask to be evaluated as the second operand in:
     * <tt>e.value & mask</tt>
     * @return {@code true} if {@code mask} is set in {@code e}, {@code false}
     * otherwise
     */
    public static boolean isSet(Event e, int mask) {
        final int value = e.value;
        return isSet(value, mask);
    }

    /**
     * Returns {@code true} if the specified event mask is set in the mask,
     * {@code false} otherwise.
     * 
     * @param event Event to be evaluated as the first operand in:
     * <tt>event & mask</tt>
     * @param mask Mask to be evaluated as the second operand in:
     * <tt>event & mask</tt>
     * @return {@code true} if {@code event} is set in {@code mask},
     * {@code false} otherwise
     */
    public static boolean isSet(int event, int mask) {
        if (event == (event & mask)) {
            return true;
        }

        if (event == Close.value) {
            if (Close_No_Write.value == (Close_No_Write.value & mask))
                return true;
            else if (Close_Write.value == (Close_Write.value & mask))
                return true;
        }

        if (event == Moved.value) {
            if (Moved_From.value == (Moved_From.value & mask))
                return true;
            else if (Moved_To.value == (Moved_To.value & mask))
                return true;
        }
        return false;
    }

    /**
     * Returns an array of Event enums set in the specified {@code mask}.
     * 
     * @param mask Mask to convert
     * @return Event[]
     */
    public static Event[] maskToEvents(int mask) {
        List<Event> evs = new ArrayList<>();

        for (Event e : values()) {
            if (e.value == (e.value & mask)) evs.add(e);
        }

        return evs.toArray(new Event[0]);
    }

    /**
     * Returns the <b>bitwise or</b> mask of the events provided.
     * 
     * @param events to operate on
     * @return mask of provided events
     */
    public static int eventsToMask(Event... events) {
        int mask = 0;

        for (Event e : events) {
            mask |= e.value;
        }

        return mask;
    }
}

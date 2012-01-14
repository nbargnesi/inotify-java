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
 * File: InotifyEvent.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */

package com.den_4.inotify_java;

import static com.den_4.inotify_java.enums.Event.*;
import static com.den_4.inotify_java.enums.EventModifier.*;

import com.den_4.inotify_java.enums.Event;
import com.den_4.inotify_java.enums.EventModifier;

/**
 * InotifyEvent is an event carrying data relevant to a watched file system.
 * Events are associated with a specific instance of the
 * {@link com.den_4.inotify_java.NativeInotify} class.
 * 
 * @author Nick Bargnesi
 */
public class InotifyEvent extends BaseEvent {
    private static final long serialVersionUID = 8331727353112279301L;

    /** Contains the mask information associated with this event. */
    private int mask;

    /** Synchronization cookie associating two event instances. */
    private int cookie;

    /** Contains a name value dependent on type of mask. */
    private String name;

    /**
     * Contains a contextual name optionally provided by the
     * {@link com.den_4.inotify_java.NativeInotify NativeInotify} derived type.
     */
    private String contextualName;

    /**
     * Creates a new InotifyEvent from the specific watch descriptor, mask,
     * cookie, and name value.
     * 
     * @param watchDescriptor The watch descriptor associated to the path on
     * which this event occurred
     * @param mask The mask responsible for this event
     */
    public InotifyEvent(int watchDescriptor, int mask) {
        super(Integer.valueOf(watchDescriptor));
        this.mask = mask;
    }

    /**
     * Creates a new InotifyEvent from the specific watch descriptor, mask,
     * cookie, and name value.
     * 
     * @param watchDescriptor The watch descriptor associated to the path on
     * which this event occurred
     * @param mask The mask responsible for this event
     * @param cookie Synchronization cookie associating two instances of events
     */
    public InotifyEvent(int watchDescriptor, int mask, int cookie) {
        super(Integer.valueOf(watchDescriptor));
        this.mask = mask;
        this.cookie = cookie;
    }

    /**
     * Creates a new InotifyEvent from the specific watch descriptor, mask,
     * and name value.
     * 
     * @param watchDescriptor The watch descriptor associated to the path on
     * which this event occurred
     * @param mask The mask responsible for this event
     * @param name Name value specific to this mask
     */
    public InotifyEvent(int watchDescriptor, int mask, String name) {
        super(Integer.valueOf(watchDescriptor));
        this.mask = mask;
        this.name = name;
    }

    /**
     * Creates a new InotifyEvent from the specific watch descriptor, mask,
     * cookie, and name value.
     * 
     * @param watchDescriptor The watch descriptor associated to the path on
     * which this event occurred
     * @param mask The mask responsible for this event
     * @param cookie Synchronization cookie associating two instances of events
     * @param name Name value specific to this mask
     */
    public InotifyEvent(int watchDescriptor, int mask, int cookie, String name) {
        super(Integer.valueOf(watchDescriptor));
        this.mask = mask;
        this.cookie = cookie;
        this.name = name;
    }

    /**
     * Returns the watch descriptor associated to the path on which this event
     * occurred.
     * 
     * @return Watch descriptor responsible for this event
     * @see NativeInotify#addWatch(String, int)
     */
    @Override
    public Integer getSource() {
        return super.getSource();
    }

    /**
     * Gets the mask field of this event.
     * 
     * @return int
     */
    public final int getMask() {
        return mask;
    }

    /**
     * Gets the cookie field of this event.
     * 
     * @return int
     */
    public final int getCookie() {
        return cookie;
    }

    /**
     * Gets the name field of this event, or null if none is set.
     * <p>
     * <cite> The name field is only present when an event is returned for a
     * file inside a watched directory; it identifies the file pathname relative
     * to the watched directory. </cite>
     * </p>
     * 
     * @return String
     */
    public final String getName() {
        return name;
    }

    /**
     * Gets the contextual name field of this event, or null if none is set.
     * 
     * @return String
     */
    public final String getContextualName() {
        return contextualName;
    }

    /**
     * Sets the contextual name field of this event.
     * 
     * @param contextualName The contextual name of this event, typically the
     * full path for which the event occurred
     */
    public final void setContextualName(String contextualName) {
        this.contextualName = contextualName;
    }

    /**
     * Returns {@code true} if {@link com.den_4.inotify_java.enums.Event#Access}
     * is set.
     * 
     * @return {@code true} if {@link com.den_4.inotify_java.enums.Event#Access}
     * is set, {@code false} otherwise
     */
    public boolean isAccess() {
        return (isSet(Access, mask));
    }

    /**
     * Returns {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Close_No_Write} is set.
     * 
     * @return {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Close_No_Write} is set,
     * {@code false} otherwise
     */
    public boolean isCloseNoWrite() {
        return (isSet(Close_No_Write, mask));
    }

    /**
     * Returns {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Close_Write} is set.
     * 
     * @return {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Close_Write} is set,
     * {@code false} otherwise
     */
    public boolean isCloseWrite() {
        return (isSet(Close_Write, mask));
    }

    /**
     * Returns {@code true} if {@link com.den_4.inotify_java.enums.Event#Close}
     * is set.
     * 
     * @return {@code true} if {@link com.den_4.inotify_java.enums.Event#Close}
     * is set, {@code false} otherwise
     */
    public boolean isClose() {
        return (isSet(Close, mask));
    }

    /**
     * Returns {@code true} if {@link com.den_4.inotify_java.enums.Event#Create}
     * is set.
     * 
     * @return {@code true} if {@link com.den_4.inotify_java.enums.Event#Create}
     * is set, {@code false} otherwise
     */
    public boolean isCreate() {
        return (isSet(Create, mask));
    }

    /**
     * Returns {@code true} if {@link com.den_4.inotify_java.enums.Event#Delete}
     * is set.
     * 
     * @return {@code true} if {@link com.den_4.inotify_java.enums.Event#Delete}
     * is set, {@code false} otherwise
     */
    public boolean isDelete() {
        return (isSet(Delete, mask));
    }

    /**
     * Returns {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Delete_Self} is set.
     * 
     * @return {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Delete_Self} is set,
     * {@code false} otherwise
     */
    public boolean isDeleteSelf() {
        return (isSet(Delete_Self, mask));
    }

    /**
     * Returns {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Metadata} is set.
     * 
     * @return {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Metadata} is set, {@code false}
     * otherwise
     */
    public boolean isMetadata() {
        return (isSet(Metadata, mask));
    }

    /**
     * Returns {@code true} if {@link com.den_4.inotify_java.enums.Event#Modify}
     * is set.
     * 
     * @return {@code true} if {@link com.den_4.inotify_java.enums.Event#Modify}
     * is set, {@code false} otherwise
     */
    public boolean isModify() {
        return (isSet(Modify, mask));
    }

    /**
     * Returns {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Move_Self} is set.
     * 
     * @return {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Move_Self} is set,
     * {@code false} otherwise
     */
    public boolean isMoveSelf() {
        return (isSet(Move_Self, mask));
    }

    /**
     * Returns {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Moved_From} is set.
     * 
     * @return {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Moved_From} is set,
     * {@code false} otherwise
     */
    public boolean isMovedFrom() {
        return (isSet(Moved_From, mask));
    }

    /**
     * Returns {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Moved_To} is set.
     * 
     * @return {@code true} if
     * {@link com.den_4.inotify_java.enums.Event#Moved_To} is set, {@code false}
     * otherwise
     */
    public boolean isMovedTo() {
        return (isSet(Moved_To, mask));
    }

    /**
     * Returns {@code true} if {@link com.den_4.inotify_java.enums.Event#Moved}
     * is set.
     * 
     * @return {@code true} if {@link com.den_4.inotify_java.enums.Event#Moved}
     * is set, {@code false} otherwise
     */
    public boolean isMoved() {
        return (isSet(Moved, mask));
    }

    /**
     * Returns {@code true} if {@link com.den_4.inotify_java.enums.Event#Open}
     * is set.
     * 
     * @return {@code true} if {@link com.den_4.inotify_java.enums.Event#Open}
     * is set, {@code false} otherwise
     */
    public boolean isOpen() {
        return (isSet(Open, mask));
    }

    /**
     * Returns {@code true} if this is a named event, {@code false} otherwise.
     * 
     * @return boolean {@code true} if name is present, {@code false} otherwise
     */
    public boolean isNamedEvent() {
        if (name != null)
            return true;
        return false;
    }

    /**
     * Returns {@code true} if the event is about a directory, {@code false}
     * otherwise.
     * 
     * @return boolean {@code true} if the event concerns a directory,
     * {@code false} otherwise
     * @see com.den_4.inotify_java.enums.EventModifier#Is_Directory
     */
    public boolean aboutDirectory() {
        if (EventModifier.isSet(Is_Directory, mask))
            return true;
        return false;
    }

    /**
     * Returns {@code true} if a watch was explicitly or automatically removed
     * due to a deletion or unmount, {@code false} otherwise.
     * 
     * @return boolean
     * @see com.den_4.inotify_java.enums.EventModifier#Ignored
     */
    public boolean isIgnored() {
        if (EventModifier.isSet(Ignored, mask))
            return true;
        return false;
    }

    /**
     * Returns {@code true} if the filesystem on which the file or directory
     * resides was unounted, {@code false} otherwise.
     * 
     * @return boolean {@code true} if underlying filesystem was unmounted,
     * {@code false} otherwise
     * @see com.den_4.inotify_java.enums.EventModifier#Unmount
     */
    public boolean isUnmounted() {
        if (EventModifier.isSet(Unmount, mask))
            return true;
        return false;
    }

    /**
     * Returns {@code true} if the event queue overflowed, {@code false}
     * otherwise.
     * 
     * @return boolean {@code true} if the event queue overflowed, {@code false}
     * otherwise
     * @see com.den_4.inotify_java.enums.EventModifier#Event_Queue_Overflow
     */
    public boolean isOverflowed() {
        if (EventModifier.isSet(Event_Queue_Overflow, mask))
            return true;
        return false;
    }

    /**
     * Returns a String representation of this InotifyEvent object.
     * 
     * @return String representation of this InotifyEvent object
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getSimpleName());
        s.append(" [wd=");
        s.append(getSource());
        s.append(", mask=");
        s.append(mask);

        Event[] evs = maskToEvents(mask);
        if (evs.length != 0)
            s.append(", events [");

        for (int i = 0; i < evs.length; i++) {
            if (i != 0)
                s.append(", " + evs[i]);
            else
                s.append(evs[i]);
        }

        if (evs.length != 0)
            s.append("]");

        EventModifier[] mods = maskToEventModifiers(mask);
        if (mods.length != 0)
            s.append(", mods [");

        for (int i = 0; i < mods.length; i++) {
            if (i != 0) {
                s.append(", ");
                s.append(mods[i]);
            } else
                s.append(mods[i]);
        }

        if (mods.length != 0)
            s.append("]");

        s.append(", cookie=");
        s.append(cookie);

        if (name != null) {
            s.append(", name=");
            s.append(name);
        }

        if (contextualName != null) {
            s.append(", contextualName=");
            s.append(contextualName);
        }

        s.append("]");

        return s.toString();
    }

}

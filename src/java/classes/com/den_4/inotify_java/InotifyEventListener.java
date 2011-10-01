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
 * File: InotifyEventListener.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */

package com.den_4.inotify_java;

/**
 * Interface specifying implementors that can receive
 * {@link com.den_4.inotify_java.InotifyEvent inotify events} from
 * {@link com.den_4.inotify_java.NativeInotify inotify} classes.
 * 
 * @author Nick Bargnesi
 */
public interface InotifyEventListener {

    /**
     * Invoked when an event occurred on a watched file or directory.
     * 
     * @param e InotifyEvent
     */
    public abstract void filesystemEventOccurred(InotifyEvent e);

    /**
     * Invoked when an inotify event queue has reached capacity.
     * 
     * @param e EventQueueFull
     */
    public abstract void queueFull(EventQueueFull e);

}

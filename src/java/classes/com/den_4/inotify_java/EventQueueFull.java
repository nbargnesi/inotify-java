/**
 * Copyright © 2009-2011 Nick Bargnesi <nick@den-4.com>. All rights reserved.
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
 * File: EventQueueFull.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import java.util.EventObject;

/**
 * EventQueueFull is an event notifying listeners that an event queue is full.
 * 
 * @author Nick Bargnesi
 */
public class EventQueueFull extends EventObject {
    private static final long serialVersionUID = -7098616085435214521L;

    /**
     * Creates a new event queue full from the specific file descriptor.
     * 
     * @param fileDescriptor The file descriptor associated with an inotify
     * event queue.
     */
    public EventQueueFull(int fileDescriptor) {
        super(fileDescriptor);
    }

    /**
     * Returns the file descriptor associated with the inotify event queue.
     * 
     * @return {@link Integer}
     */
    @Override
    public Integer getSource() {
        return (Integer) super.getSource();
    }

}

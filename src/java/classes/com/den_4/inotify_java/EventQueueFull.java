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
 * File: EventQueueFull.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

/**
 * EventQueueFull is an event notifying listeners that an event queue is full.
 * 
 * @author Nick Bargnesi
 */
public class EventQueueFull extends BaseEvent {
    private static final long serialVersionUID = -7098616085435214521L;
    private boolean outOfOrder;

    /**
     * Creates a new event queue full from the specific file descriptor.
     * 
     * @param fileDescriptor The file descriptor associated with an inotify
     * event queue.
     */
    public EventQueueFull(int fileDescriptor) {
        super(fileDescriptor);
        outOfOrder = false;
    }

    /**
     * Creates a new event queue full from the specific file descriptor.
     * 
     * @param fileDescriptor The file descriptor associated with an inotify
     * event queue.
     * @param outOfOrder When {@code true}, this parameter indicates this event
     * is occurring out-of-order
     * @see #isOutOfOrder()
     */
    public EventQueueFull(int fileDescriptor, boolean outOfOrder) {
        super(fileDescriptor);
        this.outOfOrder = outOfOrder;
    }

    /**
     * Returns the file descriptor associated with the inotify event queue.
     * 
     * @return {@link Integer}
     * @see NativeInotify#getFileDescriptor()
     */
    @Override
    public Integer getSource() {
        return super.getSource();
    }

    /**
     * Returns {@code true} if this event was generated out-of-order,
     * {@code false} otherwise.
     * <p>
     * Out-of-order queue full events may be sent before all
     * {@link InotifyEvent normal events} have been received. This allows queue
     * full events to take precedence, allowing {@link InotifyEventListener
     * event listeners} to be informed of full event queues in a timely fashion.
     * </p>
     * 
     * @return
     */
    public boolean isOutOfOrder() {
        return outOfOrder;
    }

    /**
     * Sets whether or not this event was generated out-of-order.
     * 
     * @param outOfOrder
     */
    void setOutOfOrder(boolean outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

}

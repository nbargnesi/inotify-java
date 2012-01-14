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
 * File: InvalidWatchDescriptorException.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java.exceptions;

/**
 * Checked exception representing the {@code EINVAL} error, indicating a watch
 * descriptor is invalid, an event mask is invalid, or a file descriptor is not
 * an inotify file descriptor.
 * 
 * @author Nick Bargnesi
 */
public class InvalidWatchDescriptorException extends InotifyException {
    private static final long serialVersionUID = 6467297750405556528L;

    /**
     * Constructs an invalid watch descriptor exception with {@code null} as its
     * detail message. The cause is not initialized, and may be subsequently
     * initialized by a call to {@link #initCause(Throwable)}.
     */
    public InvalidWatchDescriptorException() {
        super();
    }

    /**
     * Constructs an invalid watch descriptor exception with the specified
     * detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause(Throwable)}.
     * 
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public InvalidWatchDescriptorException(String message) {
        super(message);
    }

}

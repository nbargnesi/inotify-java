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
 * File: InsufficientKernelMemoryException.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java.exceptions;

/**
 * Checked exception representing the {@code ENOMEM} error, indicating
 * insufficient kernel memory available during the initialization of a new
 * inotify instance.
 * 
 * @author Nick Bargnesi
 */
public class InsufficientKernelMemoryException extends InotifyException {
    private static final long serialVersionUID = 7461885846141147115L;

    /**
     * Constructs an insufficient kernel memory exception with {@code null} as
     * its detail message. The cause is not initialized, and may be subsequently
     * initialized by a call to {@link #initCause(Throwable)}.
     */
    public InsufficientKernelMemoryException() {
        super();
    }

    /**
     * Constructs a new insufficient kernel memory exception with the specified
     * detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {link #initCause(Throwable)}.
     * 
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method.
     */
    public InsufficientKernelMemoryException(String message) {
        super(message);
    }

}

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
 * File: InotifyException.java
 * Project: inotify-trunk
 * Package: com.den_4.inotify_java.exceptions
 * Author: Nick Bargnesi
 */

package com.den_4.inotify_java.exceptions;

/**
 * Base class of all checked exceptions within <b>inotify-java</b>.
 * 
 * @author Nick Bargnesi
 */
public class InotifyException extends Exception {
    private static final long serialVersionUID = -1619774146957469499L;

    /**
     * Constructs an inotify-java exception, with {@code null} as its message.
     * The cause is not initialized, and may be subsequently initialized by a
     * call to {@link #initCause(Throwable)}.
     */
    public InotifyException() {
        super();
    }

    /**
     * Constructs an inotify-java exception, with the specified message. The
     * cause is not initialized, and may be subsequently initialized by a call
     * to {@link #initCause(Throwable)}.
     * 
     * @param message the detail message, saved for later retrieval by the
     * {@link #getMessage()} method
     */
    public InotifyException(String message) {
        super(message);
    }

    /**
     * Constructs an inotify-java exception, with the specified message. The
     * cause is initialized to the provided {@link java.lang.Throwable
     * throwable}.
     * 
     * @param message the detail message, saved for later retrieval by the
     * {@link #getMessage()} method
     * @param cause the {@link java.lang.Throwable throwable} indicating the
     * cause of the exception ({@code null} values are permitted
     */
    public InotifyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an inotify-java exception, with {@code null} as its message.
     * The cause is initialized to the provided {@link java.lang.Throwable
     * throwable}.
     * 
     * @param cause the {@link java.lang.Throwable throwable} indicating the
     * cause of the exception ({@code null} values are permitted
     */
    public InotifyException(Throwable cause) {
        super(cause);
    }
}

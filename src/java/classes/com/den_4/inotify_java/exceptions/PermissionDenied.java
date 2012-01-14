/**
 * Copyright © 2010-2012 Nick Bargnesi <nick@den-4.com>. All rights reserved.
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
 * File: PermissionDenied.java
 * Project: inotify-java (trunk)
 * Package: com.den_4.inotify_java.exceptions
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java.exceptions;

/**
 * Unchecked exception, indicating permission has been denied during access or
 * modification of a resource.
 * 
 * @author Nick Bargnesi
 * @since Version 2.0.3
 */
public class PermissionDenied extends InotifyError {
    private static final long serialVersionUID = 4184995691542267529L;

    /**
     * Constructs a permission denied error with <code>null</code> as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public PermissionDenied() {
    }

    /**
     * Constructs a permission denied error with the specified detail message
     * and cause. The detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this error's detail message.
     * 
     * @param message the detail message (which is saved for later retrieval by
     * the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     * indicates that the cause is nonexistent or unknown)
     */
    public PermissionDenied(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a permission denied error with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * 
     * @param message the detail message. The detail message is saved for later
     * retrieval by the {@link #getMessage()} method
     */
    public PermissionDenied(String message) {
        super(message);
    }

    /**
     * Constructs a permission denied error with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for errors that are little more than wrappers for
     * other throwables.
     * 
     * @param cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
     * indicates that the cause is nonexistent or unknown)
     */
    public PermissionDenied(Throwable cause) {
        super(cause);
    }

}

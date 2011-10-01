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
 * File: Inotify.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import java.lang.Thread.UncaughtExceptionHandler;

import com.den_4.inotify_java.exceptions.InotifyException;

/**
 * Thread-based native queue reader. This class extends the
 * {@link NativeInotify} base by implicitly servicing the native queue with a
 * daemonized thread.
 * <p>
 * The created daemon thread will be named
 * {@code ConcurrentReader:[fileDescriptor]}, where the file descriptor is
 * associated with the parent native inotify instance.
 * </p>
 *
 * @author Nick Bargnesi
 */
public abstract class ConcurrentReader extends NativeInotify implements UncaughtExceptionHandler {

    private final Thread reader;
    
    /**
     * Constructs a concurrent reader, delaying starting the reader thread until
     * the caller invokes {@link #startReader()}.
     *
     * @throws InotifyException Thrown when the watcher could not be created; a
     *         cause will be provided to the thrown object.
     */
    public ConcurrentReader() throws InotifyException {
        super();
        reader = new Thread(new Runnable() {

            @Override
            public void run() {
                read();
            }

        }, "ConcurrentReader:" + fileDescriptor); //$NON-NLS-1$

        // Thread lives only to serve user threads; thread can be daemonized.
        reader.setDaemon(true);
        reader.setUncaughtExceptionHandler(this);
    }
    
    /**
     * Start the thread that {@link com.den_4.inotify_java.NativeInotify#read()
     * reads from the native instance}. 
     */
    protected final void startReader() {
        reader.start();
    }

    /**
     * Method invoked when the anonymous runnable terminates due to the uncaught
     * exception {@code e}. This handler destroys the Inotify instance.
     *
     * @param t Terminating thread
     * @param e Uncaught exception
     */
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        e.printStackTrace();
        destroy();
    }

}

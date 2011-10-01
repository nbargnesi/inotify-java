/**
 * Copyright © 2010 Nick Bargnesi <nick@den-4.com>.  All rights reserved.
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
 * File: Fluctuator.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static com.den_4.inotify_java.enums.Event.Close_Write;
import static com.den_4.inotify_java.enums.EventModifier.Event_Queue_Overflow;
import static java.lang.Thread.currentThread;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.den_4.inotify_java.enums.Event;
import com.den_4.inotify_java.enums.EventModifier;
import com.den_4.inotify_java.exceptions.InotifyException;

/**
 * The {@code Fluctuator} class monitors a set of files for changes in size.
 * <p>
 * Contextual naming information is included with events sent to listeners.
 * <p>
 * Security constraints - files will be ignored
 * </p>
 * 
 * @author Nick Bargnesi
 * @since Version 2.0.3
 */
public class Fluctuator extends ConcurrentReader {
    private final InotifyContext context;
    /* Map of paths to sizes. */
    private final Map<String, Long> sizes;

    /**
     * simple case
     */
    public Fluctuator(final String file) throws InotifyException {
        context = new InotifyContext();
        sizes = new ConcurrentHashMap<String, Long>(1);
        addFile(file);
        init();
    }

    /**
     * a set of files where internal structures are sized according to the size
     * of the set
     */
    public Fluctuator(final Set<String> files) throws InotifyException {
        super();
        context = new InotifyContext();
        final int capacity = files.size();
        sizes = new ConcurrentHashMap<String, Long>(capacity);
        for (final String s : files)
            addFile(s);
        init();
    }

    /**
     * simple case
     */
    public Fluctuator() throws InotifyException {
        context = new InotifyContext();
        sizes = new ConcurrentHashMap<String, Long>();
        init();
    }
    
    private void init() {
        startReader();
    }
    
    public void addFile(final String path) throws InotifyException {
        if (path == null)
            throw new NullPointerException("path may not be null");
        if (context.validPath(path)) return;

        final File f = new File(path);
        if (!f.isFile())
            throw new IllegalArgumentException("invalid argument");

        sizes.put(path, f.length());
        final int wd = addWatch(path, Event.eventsToMask(Event.All));

        context.addPath(path, wd);
    }

    public void addListener(final FluctuatorListener fl, final String path) {
        final Integer wd = context.getWatch(path);
        if (wd == null)
            throw new IllegalArgumentException("invalid path");
        if (fl == null)
            throw new IllegalArgumentException("listener may not be null");
        context.addListener(fl, wd);
    }

    public void removeListener(final FluctuatorListener fl, final String path) {
        final Integer wd = context.getWatch(path);
        if (wd == null)
            throw new IllegalArgumentException("invalid path");
        if (fl == null)
            throw new IllegalArgumentException("listener may not be null");
        context.removeListener(fl, wd);
    }

    public void removeFile(final String path) throws InotifyException {
        if (path == null)
            throw new NullPointerException("path may not be null");
        if (!context.validPath(path)) return;
        final Integer wd = context.getWatch(path);
        if (wd == null)
            throw new IllegalArgumentException("invalid path");
        context.removePath(path);
        sizes.remove(path);
        removeWatch(wd);
    }

    /**
     * Receives an Inotify event, performing listener notification, file size
     * calculation, and context cleanup.
     */
    @Override
    void eventHandler(InotifyEvent e) {
        
        // Setup contextual name
        final int wd = e.getSource();
        String path = context.getPath(wd);
        if (path == null) return;
        e.setContextualName(path);
        
        // Event queue full
        if (EventModifier.isSet(Event_Queue_Overflow, e.getMask())) {
            final EventQueueFull eqf = new EventQueueFull(fileDescriptor);
            for (final InotifyEventListener l : context.getListeners())
                l.queueFull(eqf);
            return;
        }
        
        // Notify listeners of filesystem event
        Set<InotifyEventListener> lstnrs = context.getListeners(wd);
        for (final InotifyEventListener iel : lstnrs) {
            iel.filesystemEventOccurred(e);
        }
        
        // Cleanup if ignored event
        if (e.isIgnored()) {
            context.removePath(e.getSource());
            sizes.remove(path);
        }
        
        // Determine file's current size
        long new_size = -1L;
        try {
            final File f = new File(path);
            if (f.canRead())
                new_size = f.length();
        } catch (final SecurityException se) {
            // If current size can't be determined due to security constraints,
            // ignore the event.
            return;
        }

        // -1L file size indicates file can't be read
        if (new_size == -1L) return;
        
        // Get last file size, default to 0L
        final Long l = sizes.get(path);
        long last_size = 0L;
        if (l != null)
            last_size = l;
        // Note new file size
        sizes.put(path, new_size);
        
        for (final InotifyEventListener iel : lstnrs) {
            iel.filesystemEventOccurred(e);
        }
        
        long delta = new_size - last_size;
        if (delta == 0) return;
        if (delta > 0) {
            FileSizeIncreasedEvent ev = new FileSizeIncreasedEvent(wd,
                    e.getMask(), e.getName(), new_size, delta);
            ev.setContextualName(path);
            for (final InotifyEventListener iel : lstnrs) {
                ((FluctuatorListener)iel).fileSizeIncreased(ev);
            }
        } else {
            FileSizeDecreasedEvent ev = new FileSizeDecreasedEvent(wd,
                    e.getMask(), e.getName(), new_size, delta);
            ev.setContextualName(path);
            for (final InotifyEventListener iel : lstnrs) {
                ((FluctuatorListener)iel).fileSizeDecreased(ev);
            }
        }
    }
    
}

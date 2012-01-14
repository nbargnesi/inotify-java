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
 * File: Inotify.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import java.util.Set;

import com.den_4.inotify_java.enums.Event;
import com.den_4.inotify_java.enums.WatchModifier;
import com.den_4.inotify_java.exceptions.InotifyException;
import com.den_4.inotify_java.exceptions.InvalidWatchDescriptorException;

/**
 * The {@code Inotify} class is an interface to the inotify API on Linux.
 * <p>
 * This class supports all of the functions available through the inotify API
 * and provides two additional capabilities.
 * <ol>
 * <li>Users of this class may direct events concerning specific paths to any
 * and all interested parties.</li>
 * <li>Contextual naming information is included with events sent to listeners.
 * In effect, for a watched directory: {@code /path/x}, if an event occurs for
 * file {@code y}, the {@link InotifyEvent#getContextualName() contextual name}
 * associated with this event will be {@code /path/x/y}.</li>
 * </ol>
 * </p>
 * <p>
 * This implementation is synchronized to prevent data races between threads
 * manipulating watches and listeners. If some thread <i>A</i> adds a listener
 * for a watch descriptor, the event handler thread (thread <i>B</i> -- see
 * {@link ConcurrentReader}) will notify the new listener when processing
 * events.
 * </p>
 * 
 * @author Nick Bargnesi
 * @since Version 2
 */
public class Inotify extends ConcurrentReader {
    private final InotifyContext context;

    /**
     * Creates a new inotify instance, throwing an {@link InotifyException} on
     * errors.
     * 
     * @throws InotifyException Thrown when an instance could not be created; a
     * cause will be provided to the thrown object.
     */
    public Inotify() throws InotifyException {
        super();
        context = new InotifyContext();
        startReader();
    }

    /**
     * Receives an Inotify event, performing listener notification and context
     * cleanup.
     * 
     * @param e InotifyEvent
     */
    @Override
    void eventHandler(InotifyEvent e) {

        BaseEvent be = e;
        boolean overflow = false;
        if (e.isOverflowed()) {
            be = new EventQueueFull(fileDescriptor);
            overflow = true;
        }

        String path = null;
        if (e.getName() != null) {
            path = context.getPath(e.getSource());
            if (path != null) {
                String name = e.getName();
                if (path.charAt(path.length() - 1) == '/')
                    e.setContextualName(path + name);
                else
                    e.setContextualName(path + '/' + name);
            }
        }

        Set<InotifyEventListener> lstnrs;
        if (overflow) {
            lstnrs = context.getListeners();
            for (final InotifyEventListener l : lstnrs) {
                l.queueFull((EventQueueFull) be);
            }
        } else {
            int wd = e.getSource();
            lstnrs = context.getListeners(wd);
            for (final InotifyEventListener l : lstnrs) {
                l.filesystemEventOccurred(e);
            }
        }

        if (e.isIgnored()) {
            context.removePath(e.getSource());
        }
    }

    /**
     * Returns the path being watched for the provided watch descriptor.
     * 
     * @param watchDescriptor Watch descriptor identifying returning path
     * @return Path associated with provided watch descriptor, or null if the
     * watch descriptor is not valid
     */
    public String getPath(int watchDescriptor) {
        return context.getPath(watchDescriptor);
    }

    /**
     * Returns the watch descriptor identifying the provided path.
     * 
     * @param path Path associated with returning watch descriptor
     * @return Watch descriptor identifying provided path, or {@code -1} if the
     * path is not being watched
     */
    public int getWatchDescriptor(String path) {
        if (path == null) return -1;
        Integer wd = context.getWatch(path);
        if (wd == null) return -1;
        return wd;
    }

    /**
     * Adds the provided event listener as a receiver of {@link InotifyEvent
     * events} for the specified watch descriptor.
     * 
     * @param watchDescriptor Watch descriptor identifying path listener is
     * interested in
     * @param listener Listener to be notified of events occurred for the watch
     * descriptor provided
     * @throws IllegalArgumentException Thrown if the provided watch descriptor
     * is invalid
     */
    public void addListener(final int watchDescriptor,
            final InotifyEventListener listener) {
        if (listener == null)
            throw new NullPointerException("listener may not be null");
        if (!context.validWatch(watchDescriptor))
            throw new IllegalArgumentException("invalid watch descriptor");

        context.addListener(listener, watchDescriptor);
    }

    /**
     * Removes the provided event listener from receiving {@link InotifyEvent
     * events} for the specified watch descriptor.
     * 
     * @param watchDescriptor Watch descriptor identifying path listener was
     * interested in
     * @param listener Listener to be removed
     * @throws NullPointerException Thrown if the provided listener is null
     * @throws IllegalArgumentException Thrown if the provided watch descriptor
     * is invalid
     */
    public void removeListener(final int watchDescriptor,
            final InotifyEventListener listener) {
        if (listener == null)
            throw new NullPointerException("listener may not be null");
        if (!context.validWatch(watchDescriptor))
            throw new IllegalArgumentException("invalid watch descriptor");
        context.removeListener(listener, watchDescriptor);
    }

    /**
     * Adds a watch for the specified path for the provided events.
     * 
     * @param path Path to be watched
     * @param events Events to watch for
     * @return Watch descriptor uniquely identifying this watched path
     * @throws InotifyException Thrown if the native inotify object could not be
     * constructed. The cause of the exception will be provided.
     */
    public int addWatch(final String path, final Event... events)
            throws InotifyException {
        if (path == null)
            throw new NullPointerException("path may not be null");

        int wm_mask = 0;
        int ev_mask = Event.eventsToMask(events);
        if (context.validPath(path)) {
            wm_mask |= WatchModifier.Add.value();
        }

        return add_watch(path, wm_mask, ev_mask);
    }

    /**
     * Adds a watch for the specified path for the provided events and watch
     * modifiers.
     * 
     * @param path Path to be watched
     * @param watchModifiers Watch modifiers to augment addition of watch
     * @param events Events to watch for
     * @return Watch descriptor uniquely identifying this watched path
     * @throws InotifyException Thrown if the native inotify object could not be
     * constructed. The cause of the exception will be provided.
     */
    int addWatch(final String path, final WatchModifier[] watchModifiers,
            final Event[] events) throws InotifyException {
        if (path == null)
            throw new NullPointerException("path may not be null");

        int wm_mask = WatchModifier.watchModifiersToMask(watchModifiers);
        int ev_mask = Event.eventsToMask(events);

        return add_watch(path, wm_mask, ev_mask);
    }

    /**
     * Adds a watch for the specified path using the provided watch modifier and
     * event masks.
     * 
     * @param path Path to be watched
     * @param watchModifiersMask Watch modifier mask to augment addition of
     * watch
     * @param eventMask Events to watch for
     * @return Watch descriptor uniqely identifying this watched path
     * @throws InotifyException Thrown if the native inotify object could not be
     * constructed. The cause of the exception will be provided.
     */
    int addWatch(final String path, final int watchModifiersMask,
            final int eventMask) throws InotifyException {
        if (path == null)
            throw new NullPointerException("path may not be null");

        return add_watch(path, watchModifiersMask, eventMask);
    }

    /**
     * Private delegate method for adding watches.
     * 
     * @param path Path to watch
     * @param wm_mask Watch modifier mask
     * @param ev_mask Event mask
     * @return watch descriptor
     * @throws InotifyException Thrown if the native inotify object could not be
     * constructed. The cause of the exception will be provided.
     */
    private int add_watch(final String path, final int wm_mask,
            final int ev_mask) throws InotifyException {
        int wd = super.addWatch(path, wm_mask | ev_mask);
        context.addPath(path, wd);
        return wd;
    }

    /**
     * Removes the watch associated with the provided watch descriptor.
     * 
     * @param watchDescriptor Unique descriptor returned from
     * {@link #addWatch(String, Event...)}
     * @return {@code true} if a watch was removed, {@code false} otherwise
     * @throws InvalidWatchDescriptorException Thrown indicating an error in the
     * watch removal has occurred
     */
    @Override
    boolean removeWatch(final int watchDescriptor)
            throws InvalidWatchDescriptorException {
        return remove_watch(watchDescriptor);
    }

    /**
     * Private delegate method for removing watches.
     * 
     * @param wd
     * @return {@code true} if a watch was removed, {@code false} otherwise
     * @throws InvalidWatchDescriptorException
     */
    private boolean remove_watch(final int wd)
            throws InvalidWatchDescriptorException {
        context.removePath(wd);
        return super.removeWatch(wd);
    }

    /**
     * Returns a String representation of this Inotify object.
     * 
     * @return String representation of this Inotify object
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getSimpleName());

        s.append(" [fd=");
        s.append(fileDescriptor);

        s.append(", context=");
        s.append(context);

        s.append("]");

        return s.toString();
    }
}

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
 * File: InotifyContext.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.util.Collections.emptySet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * This class is a synchronized means of associating a set of filesystem paths,
 * inotify watch descriptors, and event listeners with classes inheriting from
 * {@link com.den_4.inotify_java.NativeInotify NativeInotify}.
 * <p>
 * It maintains the set of paths, watches, and listeners, the latter of which
 * exist for one or more watches.
 * </p>
 * 
 * @author Nick Bargnesi
 * @since Version 2.0.3
 */
final class InotifyContext {

    /* Maps watch descriptors to paths. */
    private Map<Integer, String> paths;
    /* Maps paths to watch descriptors. */
    private Map<String, Integer> watches;
    /* Maps watch descriptors to listeners. */
    private Map<Integer, Set<InotifyEventListener>> listeners;

    /* Lock held when accessing the context. */
    private final ReadLock rl;
    /* Lock held when manipulating the context. */
    private final WriteLock wl;

    /**
     * A new inotify context with an empty set of paths, watches, and listeners.
     */
    InotifyContext() {
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        rl = lock.readLock();
        wl = lock.writeLock();

        paths = new HashMap<>();
        watches = new HashMap<>();
        listeners = new HashMap<>();
    }

    /**
     * Returns {@code true} if no paths, watches, or listeners are available in
     * this context, {@false} otherwise.
     * 
     * @return boolean
     */
    boolean isEmpty() {
        return paths.isEmpty() && watches.isEmpty() && listeners.isEmpty();
    }

    /**
     * Adds a path/watch pair to the context. This method may block waiting for
     * a write lock.
     * 
     * @param path Path
     * @param wd Watch descriptor
     * @see #removePath(int)
     * @see #removePath(String)
     * @see #validPath(String)
     * @see #validWatch(int)
     */
    void addPath(final String path, final int wd) {
        wl.lock();
        try {
            paths.put(wd, path);
            watches.put(path, wd);
        } finally {
            wl.unlock();
        }
    }

    /**
     * Adds a listener to the context for the specified watch descriptor. This
     * method may block waiting for a write lock.
     * 
     * @param l Listener
     * @param wd Watch descriptor
     * @see #removeListener(InotifyEventListener, int)
     */
    void addListener(final InotifyEventListener l, final int wd) {
        wl.lock();
        try {
            Set<InotifyEventListener> set = listeners.get(wd);
            if (set == null) {
                set = new HashSet<>();
                listeners.put(wd, set);
            }
            set.add(l);
        } finally {
            wl.unlock();
        }
    }

    /**
     * Returns {@code true} if <tt>path</tt> is valid in this context,
     * {@code false} otherwise. This method may block waiting for a read lock.
     * 
     * @param path Path
     * @return boolean
     */
    boolean validPath(final String path) {
        rl.lock();
        try {
            return watches.containsKey(path);
        } finally {
            rl.unlock();
        }
    }

    /**
     * Returns the path within this context for the specified watch
     * descriptor, <tt>null</tt> if the watch descriptor is not valid.
     * 
     * @param wd Watch descriptor
     * @return path, potentially null
     * @see #addPath(String, int)
     * @see #removePath(int)
     * @see #removePath(String)
     */
    String getPath(final int wd) {
        rl.lock();
        try {
            return paths.get(wd);
        } finally {
            rl.unlock();
        }
    }

    /**
     * Returns the watch descriptor within this context for the specified path,
     * <tt>null</tt> if the path is not valid. This method may block waiting
     * for a read lock.
     * 
     * @param path
     * @return Integer, potentiall null
     * @see #addPath(String, int)
     * @see #removePath(int)
     * @see #removePath(String)
     */
    Integer getWatch(final String path) {
        rl.lock();
        try {
            return watches.get(path);
        } finally {
            rl.unlock();
        }
    }

    /**
     * Returns {@code true} if the watch descriptor <tt>wd</tt> is valid in
     * this context, {@code false} otherwise. This method may block waiting for
     * a read lock.
     * 
     * @param wd Watch descriptor
     * @return boolean
     */
    boolean validWatch(final int wd) {
        rl.lock();
        try {
            return paths.containsKey(wd);
        } finally {
            rl.unlock();
        }
    }

    /**
     * Removes a path/watch pair from the context by <tt>path</tt>. This
     * method may block waiting for a write lock.
     * 
     * @param path Path
     * @see #addPath(String, int)
     * @see #removePath(int)
     */
    void removePath(final String path) {
        wl.lock();
        try {
            final Integer wd = watches.remove(path);
            paths.remove(wd);
            listeners.remove(wd);
        } finally {
            wl.unlock();
        }
    }

    /**
     * Removes a path/watch pair from the context by the watch descriptor
     * <tt>wd</tt>. This method may block waiting for a write lock.
     * 
     * @param wd Watch descriptor
     * @see #addPath(String, int)
     * @see #removePath(String)
     */
    void removePath(final int wd) {
        wl.lock();
        try {
            watches.remove(paths.remove(wd));
            listeners.remove(wd);
        } finally {
            wl.unlock();
        }
    }

    /**
     * Removes a listener from the context for the specified watch descriptor
     * <tt>wd</tt>. This method may block waiting for a write lock.
     * 
     * @param l Inotify event listener
     * @param wd Watch descriptor
     * @see #addListener(InotifyEventListener, int)
     */
    void removeListener(final InotifyEventListener l, final int wd) {
        wl.lock();
        try {
            Set<InotifyEventListener> set = listeners.get(wd);
            if (set == null)
                return;
            set.remove(l);
            if (set.isEmpty())
                listeners.remove(wd);
        } finally {
            wl.unlock();
        }
    }

    /**
     * Returns the set of listeners in this context for the specified watch
     * descriptor <tt>wd</tt>. This method may block waiting for a read lock.
     * 
     * @param wd Watch descriptor
     * @return non-null set of listeners, potentially empty
     */
    Set<InotifyEventListener> getListeners(final int wd) {
        rl.lock();
        try {
            Set<InotifyEventListener> set = listeners.get(wd);
            if (set == null) {
                set = emptySet();
            }
            return set;
        } finally {
            rl.unlock();
        }
    }

    /**
     * Returns the set of listeners in this context. This method may block
     * waiting for a read lock.
     * 
     * @return non-null set of listeners, potentially empty
     */
    Set<InotifyEventListener> getListeners() {
        rl.lock();
        try {
            final Set<InotifyEventListener> superset = new HashSet<>();
            for (final Set<InotifyEventListener> set : listeners.values()) {
                superset.addAll(set);
            }
            return superset;
        } finally {
            rl.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        rl.lock();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("InotifyContext [paths=");
            builder.append(paths);
            builder.append(", watches=");
            builder.append(watches);
            builder.append(", listeners=");
            builder.append(listeners);
            builder.append("]");
            return builder.toString();
        } finally {
            rl.unlock();
        }
    }

}

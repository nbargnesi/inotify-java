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
 * File: InotifyUtilities.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.lang.String.valueOf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.den_4.inotify_java.exceptions.PermissionDenied;

/**
 * The {@code InotifyUtilities} class is a collection of utilities helpful in
 * working within the domain of filesystems and the inotify interface.
 * 
 * @author Nick Bargnesi
 * @since Version 2.0.3
 */
public final class InotifyUtilities {

    /**
     * The proc path entry controlling the upper limit on the number of events
     * that can be queued natively to a inotify instance. Events in excess of
     * this limit are dropped which will result in an {@link EventQueueFull
     * event queue full} event being sent.
     */
    public static final String MAX_QUEUED_EVENTS =
            "/proc/sys/fs/inotify/max_queued_events";

    /**
     * The proc path entry controlling the upper limit on the number of inotify
     * instances that can be created per user.
     */
    public static final String MAX_USER_INSTANCES =
            "/proc/sys/fs/inotify/max_user_instances";

    /**
     * The proc path entry controlling the upper limit of the number of watches
     * that can be created per user.
     */
    public static final String MAX_USER_WATCHES =
            "/proc/sys/fs/inotify/max_user_watches";

    /**
     * Executes a breadth-first traversal of directories starting from
     * <tt>path</tt> and returns a queue of the paths.
     * <p>
     * Ordering of directories at any level is not guaranteed due to the
     * no-ordering guarantee specified by {@link java.io.File#listFiles
     * java.io.File}.
     * </p>
     * <p>
     * This algorithm is tailored to prevent visiting a path more than once,
     * defeating self-referencing and cyclic symlinks.
     * </p>
     * 
     * @param path Starting directory
     * @return Queue of paths
     * @throws IOException See {@link java.io.File#getCanonicalPath()}
     */
    public static Queue<String> getPaths(final String path) throws IOException {
        final File root = new File(path);
        final Queue<String> queue = new LinkedList<>();
        if (!root.isDirectory())
            return queue;

        final Set<String> visited = new HashSet<>();
        String can_path = root.getCanonicalPath();
        visited.add(can_path);
        queue.add(can_path);

        Queue<String> next = new LinkedList<>(queue);
        while (!next.isEmpty()) {
            final Queue<String> current = new LinkedList<>();
            for (final String subpath : next) {
                final File f = new File(subpath);
                if (!f.isDirectory())
                    continue;
                final File[] entries = f.listFiles();
                if (entries == null)
                    continue;
                for (final File entry : entries) {
                    can_path = entry.getCanonicalPath();
                    if (!entry.isDirectory() || visited.contains(can_path))
                        continue;
                    visited.add(can_path);
                    current.add(can_path);
                }
            }
            queue.addAll(current);
            next = current;
        }

        return queue;
    }

    /**
     * Set {@link #MAX_QUEUED_EVENTS}.
     * 
     * @param newmax Maximum number of queued events for an inotify instance
     */
    public static void setMaximumQueuedEvents(final long newmax) {
        if (!isMaximumQueuedEventsTunable())
            throw new PermissionDenied("permission denied: "
                    + MAX_QUEUED_EVENTS);
        final File procEntry = new File(MAX_QUEUED_EVENTS);
        write(procEntry, valueOf(newmax));
    }

    /**
     * Get {@link #MAX_QUEUED_EVENTS}.
     * 
     * @return long
     */
    public static long getMaximumQueuedEvents() {
        final File procEntry = new File(MAX_QUEUED_EVENTS);
        final Long ret = read(procEntry);
        return ret;
    }

    /**
     * Returns {@code true} if the current user can tune
     * {@link #MAX_QUEUED_EVENTS}, {@code false} otherwise.
     * 
     * @return boolean
     */
    public static boolean isMaximumQueuedEventsTunable() {
        final File procEntry = new File(MAX_QUEUED_EVENTS);
        return procEntry.canWrite();
    }

    /**
     * Set {@link #MAX_USER_INSTANCES}.
     * 
     * @param newmax Maximum number of inotify instances for a user
     */
    public static void setMaximumUserInstances(final long newmax) {
        if (!isMaximumUserInstancesTunable())
            throw new PermissionDenied("permission denied: " +
                    MAX_QUEUED_EVENTS);
        final File procEntry = new File(MAX_USER_INSTANCES);
        write(procEntry, valueOf(newmax));
    }

    /**
     * Get {@link #MAX_USER_INSTANCES}.
     * 
     * @return long
     */
    public static long getMaximumUserInstances() {
        final File procEntry = new File(MAX_USER_INSTANCES);
        final Long ret = read(procEntry);
        return ret;
    }

    /**
     * Returns {@code true} if the current user can tune
     * {@link #MAX_USER_INSTANCES}, {@code false} otherwise.
     * 
     * @return boolean
     */
    public static boolean isMaximumUserInstancesTunable() {
        final File procEntry = new File(MAX_USER_INSTANCES);
        return procEntry.canWrite();
    }

    /**
     * Set {@link #MAX_USER_WATCHES}.
     * 
     * @param newmax Maximum number of inotify watches for a user
     */
    public static void setMaximumUserWatches(final long newmax) {
        if (!isMaximumUserWatchesTunable())
            throw new PermissionDenied("permission denied: " +
                    MAX_QUEUED_EVENTS);
        final File procEntry = new File(MAX_USER_WATCHES);
        write(procEntry, valueOf(newmax));
    }

    /**
     * Get {@link #MAX_USER_WATCHES}.
     * 
     * @return long
     */
    public static long getMaximumUserWatches() {
        final File procEntry = new File(MAX_USER_WATCHES);
        final Long ret = read(procEntry);
        return ret;
    }

    /**
     * Returns {@code true} if the current user can tune
     * {@link #MAX_USER_WATCHES}, {@code false} otherwise.
     * 
     * @return boolean
     */
    public static boolean isMaximumUserWatchesTunable() {
        final File procEntry = new File(MAX_USER_WATCHES);
        return procEntry.canWrite();
    }

    /*
     * Write a string to a file.
     * @param f File writer constructor argument
     * @param str String to write
     */
    private static void write(final File f, final String str) {
        try {
            final FileWriter fw = new FileWriter(f);
            fw.write(str);
            fw.close();
        } catch (IOException e) {
            throw new PermissionDenied(e);
        }
    }

    /*
     * Read a string from a file.
     * @param f File to read from
     */
    private static Long read(final File f) {
        try {
            final FileReader fr = new FileReader(f);
            final BufferedReader br = new BufferedReader(fr);
            String buf = br.readLine();
            br.close();
            return Long.parseLong(buf);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return -1L;
    }

}

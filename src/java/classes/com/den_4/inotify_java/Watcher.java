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
 * File: Watcher.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import com.den_4.inotify_java.enums.Event;
import com.den_4.inotify_java.exceptions.InotifyException;

/**
 * The {@code Watcher} class is a simple manner in which directories and files
 * can be watched. It is only associated with a single path and event type.
 * <p>
 * This class provides a number of ways to be notified of the event:
 * <dl>
 * <dt>{@link com.den_4.inotify_java.InotifyEventListener Listener}</dt>
 * <dd>A listener (or set of listeners) will be notified of events via
 * {@link com.den_4.inotify_java.InotifyEventListener#filesystemEventOccurred
 * the notification method}.</dd>
 * <dt>{@link java.lang.Runnable Runnable}</dt>
 * <dd>Invoking the {@link java.lang.Runnable#run() run method} of a
 * {@link java.lang.Runnable Runnable}.</dd>
 * <dt>{@link java.util.concurrent.CyclicBarrier CyclicBarrier}</dt>
 * <dd>Invoking await on a {@link java.util.concurrent.CyclicBarrier
 * CyclicBarrier}.</dd>
 * <dt>{@link java.util.concurrent.CountDownLatch}</dt>
 * <dd>Invoking await on a {@code java.util.concurent.CountDownLatch
 * CountDownLatch}. The one-shot nature of the latch is inherited by the
 * {@code Watcher} class. Successive events will not cause a <em>count down</em>
 * of the provided latch.</dd>
 * <dt>{@link java.util.concurrent.Semaphore Semaphore}</dt>
 * <dd>Acquiring a permit provided by the {@code Watcher} with a
 * {@link java.util.concurrent.Semaphore semaphore}.</dd>
 * </dl>
 * </p>
 * <p>
 * Each notification mechanism supports
 * {@link com.den_4.inotify_java.InotifyEventListener listeners} as well.
 * However, no guarantee is made that listeners will be notified of events in a
 * deterministic manner when using a non-listener-based model. It is reasonable
 * to assume a listener may miss generated events, due to the execution of the
 * object's constructor, followed by a successive
 * {@link #addListener(InotifyEventListener)} invocation.
 * </p>
 * 
 * @author Nick Bargnesi
 * @since Version 2
 */
public class Watcher extends ConcurrentReader {

    private String pathName;
    private Event eventType;

    /** Barrier when operating in barrier mode. */
    protected CyclicBarrier barrier;
    /** Latch when operating in latch mode. */
    protected CountDownLatch latch;
    /** Semaphore when operating in semaphore mode. */
    protected Semaphore semaphore;

    /** List of listeners to notify of filesystem events. */
    protected List<InotifyEventListener> listeners;

    /** Enumeration definition defining mode definitions. */
    protected static enum Mode {
        /**
         * Listener mode enumeration.
         */
        LISTENER,

        /**
         * Runnable mode enumeration.
         */
        RUNNABLE,

        /**
         * Barrier mode enumeration.
         */
        BARRIER,

        /**
         * Latch mode enumeration.
         */
        LATCH,

        /**
         * Semaphore mode enumeration.
         */
        SEMAPHORE;
    }

    /** The watcher's mode. */
    protected Mode mode;

    /**
     * Creates a watcher for the supplied path <em>p</em> and event type
     * <em>ev</em>.
     * 
     * @param path Path to watch
     * @param ev Event to watch for
     * @throws InotifyException Thrown when the watcher could not be created; a
     * cause will be provided to the thrown object.
     * @see com.den_4.inotify_java.enums.Event
     */
    public Watcher(String path, Event ev) throws InotifyException {
        super();
        if (path == null || "".equals(path))
            throw new IllegalArgumentException("invalid path");
        mode = Mode.LISTENER;
        init(path, ev);
        startReader();
    }

    /**
     * Creates a watcher for the supplied path <em>p</em>, event type
     * <em>ev</em>, and listener <em>l</em>
     * 
     * @param path Path to watch
     * @param ev Event to watch for
     * @param l InotifyEventListener to receive events
     * @throws InotifyException Thrown when the watcher could not be created; a
     * cause will be provided to the thrown object.
     * @see com.den_4.inotify_java.enums.Event
     */
    public Watcher(String path, Event ev, InotifyEventListener l)
            throws InotifyException {
        super();
        if (path == null || "".equals(path))
            throw new IllegalArgumentException("invalid path");
        mode = Mode.LISTENER;
        addListener(l);
        init(path, ev);
        startReader();
    }

    /**
     * Creates a watcher for the supplied path <em>p</em> and event type
     * <em>ev</em>. The provided {@link java.lang.Runnable runnable} will be
     * invoked when filesystem events occur.
     * 
     * @param path Path to watch
     * @param ev Event to watch for
     * @param r Runnable to invoke when filesystem events occur
     * @throws InotifyException Thrown when the watcher could not be created; a
     * cause will be provided to the thrown object.
     */
    public Watcher(String path, Event ev, Runnable r) throws InotifyException {
        super();
        if (path == null || "".equals(path))
            throw new IllegalArgumentException("invalid path");
        if (r == null)
            throw new IllegalArgumentException("invalid runnable");
        mode = Mode.RUNNABLE;
        barrier = new CyclicBarrier(1, r);
        init(path, ev);
        startReader();
    }

    /**
     * Creates a watcher for the supplied path <em>p</em> and event type
     * <em>ev</em>. The provided {@link java.util.concurrent.CyclicBarrier
     * barrier} will be waited on when filesystem events occur.
     * 
     * @param path Path to watch
     * @param ev Event to watch for
     * @param b CyclicBarrier providing a manner for interested parties to reach
     * a common barrier point with the watcher and filesystem events as they
     * occur.
     * @throws InotifyException Thrown when the watcher could not be created; a
     * cause will be provided to the thrown object.
     */
    public Watcher(String path, Event ev, CyclicBarrier b)
            throws InotifyException {
        super();
        if (path == null || "".equals(path))
            throw new IllegalArgumentException("invalid path");
        if (b == null)
            throw new IllegalArgumentException("invalid barrier");
        mode = Mode.BARRIER;
        barrier = b;
        init(path, ev);
        startReader();
    }

    /**
     * Creates a watcher for the supplied path <em>p</em> and event type
     * <em>ev</em>. The provided {@link java.util.concurrent.CountDownLatch
     * latch} will be counted down when filesystem events occurred.
     * 
     * @param path Path to watch
     * @param ev Event to watch for
     * @param c CountDownLatch providing a one-shot synchronization aid to
     * watcher users
     * @throws InotifyException Thrown when the watcher could not be created; a
     * cause will be provided to the thrown object.
     */
    public Watcher(String path, Event ev, CountDownLatch c)
            throws InotifyException {
        super();
        if (path == null || "".equals(path))
            throw new IllegalArgumentException("invalid path");
        if (c == null)
            throw new IllegalArgumentException("invalid latch");
        mode = Mode.LATCH;
        latch = c;
        init(path, ev);
        startReader();
    }

    /**
     * Creates a watcher for the supplied path <em>p</em> and event type
     * <em>ev</em>. A permit will be obtained from the
     * {@link java.util.concurrent.Semaphore semaphore} when filesystem events
     * occur.
     * 
     * @param path Path to watch
     * @param ev Event to watch for
     * @param s Semaphore to release permits on, when filesystem events occur
     * @throws InotifyException Thrown when the watcher could not be created; a
     * cause will be provided to the thrown object.
     */
    public Watcher(String path, Event ev, Semaphore s) throws InotifyException {
        super();
        if (path == null || "".equals(path))
            throw new IllegalArgumentException("invalid path");
        if (s == null)
            throw new IllegalArgumentException("invalid semaphore");
        mode = Mode.SEMAPHORE;
        semaphore = s;
        init(path, ev);
        startReader();
    }

    private void init(String path, Event ev) throws InotifyException {
        this.pathName = path;
        this.eventType = ev;
        addWatch(pathName, ev.value());
    }

    /**
     * Adds the specified {@link com.den_4.inotify_java.InotifyEventListener
     * listener} to be notified of events.
     * <p>
     * This method is not thread-safe. No guarantee is provided for the
     * resulting set of listeners when this method is invoked in a
     * non-thread-safe manner.
     * </p>
     * 
     * @param l InotifyEventListener - null listeners are ignored
     */
    @NonThreadSafe
    public void addListener(InotifyEventListener l) {
        if (l != null) {
            if (listeners == null)
                listeners = new LinkedList<>();
            listeners.add(l);
        }
    }

    /**
     * Removes the first occurrence of the specified
     * {@link com.den_4.inotify_java.InotifyEventListener listener} from the
     * list of event listeners.
     * 
     * @param l InotifyEventListener - null listeners are ignored
     * @see java.util.List#remove(Object)
     */
    public void removeListener(InotifyEventListener l) {
        if (l != null) {
            if (listeners != null)
                listeners.remove(l);
        }
    }

    /**
     * Removes all {@link com.den_4.inotify_java.InotifyEventListener listeners}
     * from the list of event listeners.
     */
    public void removeAll() {
        if (listeners != null)
            listeners.clear();
    }

    /**
     * Returns the path being watched.
     * 
     * @return String
     */
    public String getPath() {
        return pathName;
    }

    /**
     * Returns the event type being watched.
     * 
     * @return Event
     */
    public Event getEvent() {
        return eventType;
    }

    /**
     * Receives an Inotify event.
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

        if (e.getName() != null) {
            String name = e.getName();
            if (pathName.charAt(pathName.length() - 1) == '/') {
                e.setContextualName(pathName + name);
            } else {
                e.setContextualName(pathName + '/' + name);
            }
        }

        switch (mode) {
        case LISTENER:
            for (InotifyEventListener l : listeners) {
                if (overflow) {
                    l.queueFull((EventQueueFull) be);
                } else {
                    l.filesystemEventOccurred(e);
                }
            }
            return;
        case RUNNABLE:
        case BARRIER:
            try {
                if (!overflow) {
                    barrier.await();
                }
            } catch (InterruptedException ie) {
                destroy();
                final String msg = "watcher destroyed";
                UnsupportedOperationException uoe;
                uoe = new UnsupportedOperationException(msg);
                uoe.initCause(ie);
                throw uoe;
            } catch (BrokenBarrierException bbe) {
                destroy();
                final String msg = "watcher destroyed";
                UnsupportedOperationException uoe;
                uoe = new UnsupportedOperationException(msg);
                uoe.initCause(bbe);
                throw uoe;
            }
            break;
        case LATCH:
            if (!overflow) {
                latch.countDown();
            }
            break;
        case SEMAPHORE:
            if (!overflow) {
                semaphore.release();
            }
            break;
        }

        if (mode != Mode.LISTENER && listeners != null) {
            for (InotifyEventListener l : listeners) {
                if (overflow) {
                    l.queueFull((EventQueueFull) be);
                } else {
                    l.filesystemEventOccurred(e);
                }
            }
        }
    }

    /**
     * Returns a string representation of the watcher.
     * 
     * @return a string representation of the watcher
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getClass().getSimpleName());

        s.append(" [mode=");
        switch (mode) {
        case BARRIER:
            s.append(Mode.BARRIER);
            break;
        case LATCH:
            s.append(Mode.LATCH);
            break;
        case LISTENER:
            s.append(Mode.LISTENER);
            break;
        case RUNNABLE:
            s.append(Mode.RUNNABLE);
            break;
        case SEMAPHORE:
            s.append(Mode.SEMAPHORE);
            break;
        }

        s.append(", fd=");
        s.append(fileDescriptor);

        s.append(", pathName=");
        s.append(pathName);

        s.append(", eventType=");
        s.append(eventType);

        if (listeners != null) {
            s.append(", listeners=");
            s.append(listeners.size());
        }

        s.append("]");

        return s.toString();
    }

    /**
     * Returns the number of listeners.
     * 
     * @return Number of listeners
     */
    int numberOfListeners() {
        if (listeners != null)
            return listeners.size();
        return 0;
    }
}

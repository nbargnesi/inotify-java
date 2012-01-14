/**
 * Copyright © 2010-2012 Nick Bargnesi <nick@den-4.com>.  All rights reserved.
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
 * File: InotifyContextTest.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.lang.String.valueOf;
import static java.lang.System.out;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Inotify context unit tests.
 * 
 * @author Nick Bargnesi
 * @since Version 2.0.3
 */
public class InotifyContextTest {

    InotifyContext ic;
    final Random rnd = new Random();

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        ic = new InotifyContext();
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        ic = null;
    }

    /**
     * Test method for {@link InotifyContext#InotifyContext()}.
     */
    @Test
    public void testInotifyContext() {
        assertTrue(ic.isEmpty());
    }

    /**
     * Test method for {@link InotifyContext#addPath(String, int)}.
     */
    @Test
    public void testAddPath() {
        String path = valueOf(currentTimeMillis());
        int wd = rnd.nextInt();
        ic.addPath(path, wd);
        assertFalse(ic.isEmpty());
        assertEquals(path, ic.getPath(wd));
        assertTrue(wd == ic.getWatch(path));
    }

    /**
     * Test method for
     * {@link InotifyContext#addListener(InotifyEventListener, int)}.
     */
    @Test
    public void testAddListener() {
        int wd = rnd.nextInt();
        final InotifyEventListener i = new InotifyEventListener() {
            @Override
            public void filesystemEventOccurred(InotifyEvent e) {
            }

            @Override
            public void queueFull(EventQueueFull e) {
            }
        };
        ic.addListener(i, wd);
        assertFalse(ic.isEmpty());
        assertTrue(ic.getListeners(wd).contains(i));
    }

    /**
     * Test method for {@link InotifyContext#validPath(String)}.
     */
    @Test
    public void testValidPath() {
        String path = valueOf(currentTimeMillis());
        int wd = rnd.nextInt();
        assertFalse(ic.validPath(path));
        ic.addPath(path, wd);
        assertTrue(ic.validPath(path));
        ic.removePath(path);
        assertFalse(ic.validPath(path));
    }

    /**
     * Test method for {@link InotifyContext#getPath(int)}.
     */
    @Test
    public void testGetPath() {
        String path = valueOf(currentTimeMillis());
        int wd = rnd.nextInt();
        ic.addPath(path, wd);
        assertEquals(path, ic.getPath(wd));
    }

    /**
     * Test method for {@link InotifyContext#getWatch(String)}.
     */
    @Test
    public void testGetWatch() {
        String path = valueOf(currentTimeMillis());
        int wd = rnd.nextInt();
        ic.addPath(path, wd);
        assertTrue(wd == ic.getWatch(path));
    }

    /**
     * Test method for {@link InotifyContext#validWatch(int)}.
     */
    @Test
    public void testValidWatch() {
        String path = valueOf(currentTimeMillis());
        int wd = rnd.nextInt();
        assertFalse(ic.validWatch(wd));
        ic.addPath(path, wd);
        assertTrue(ic.validWatch(wd));
        ic.removePath(wd);
        assertFalse(ic.validWatch(wd));
    }

    /**
     * Test method for {@link InotifyContext#removePath(String)}.
     */
    @Test
    public void testRemovePathString() {
        String path = valueOf(currentTimeMillis());
        int wd = rnd.nextInt();
        ic.removePath(path);
        assertTrue(ic.isEmpty());
        ic.addPath(path, wd);
        assertFalse(ic.isEmpty());
        ic.removePath(path);
        assertTrue(ic.isEmpty());
    }

    /**
     * Test method for {@link InotifyContext#removePath(int)}.
     */
    @Test
    public void testRemovePathInt() {
        String path = valueOf(currentTimeMillis());
        int wd = rnd.nextInt();
        ic.removePath(wd);
        assertTrue(ic.isEmpty());
        ic.addPath(path, wd);
        assertFalse(ic.isEmpty());
        ic.removePath(wd);
        assertTrue(ic.isEmpty());
    }

    /**
     * Test method for
     * {@link InotifyContext#removeListener(InotifyEventListener, int)}.
     */
    @Test
    public void testRemoveListener() {
        int wd = rnd.nextInt();
        final InotifyEventListener i = new InotifyEventListener() {
            @Override
            public void filesystemEventOccurred(InotifyEvent e) {
            }

            @Override
            public void queueFull(EventQueueFull e) {
            }
        };
        ic.removeListener(i, wd);
        assertTrue(ic.isEmpty());
        ic.addListener(i, wd);
        assertFalse(ic.isEmpty());
        assertTrue(ic.getListeners(wd).contains(i));
        ic.removeListener(i, wd);
        assertFalse(ic.getListeners(wd).contains(i));
        assertTrue(ic.isEmpty());
    }

    /**
     * Test method for {@link InotifyContext#getListeners(int)}.
     */
    @Test
    public void testGetListenersInt() {
        int wd = rnd.nextInt();
        final InotifyEventListener i = new InotifyEventListener() {
            @Override
            public void filesystemEventOccurred(InotifyEvent e) {
            }

            @Override
            public void queueFull(EventQueueFull e) {
            }
        };
        assertTrue(ic.getListeners(wd).isEmpty());
        ic.addListener(i, wd);
        assertTrue(ic.getListeners(wd).contains(i));
        Set<InotifyEventListener> set = ic.getListeners(wd);
        assertTrue(1 == set.size());
    }

    /**
     * Test method for {@link InotifyContext#getListeners()}.
     */
    @Test
    public void testGetListeners() {
        int wd1 = rnd.nextInt();
        Set<InotifyEventListener> set = ic.getListeners();
        assertNotNull(set);
        assertTrue(set.isEmpty());

        final InotifyEventListener i1 = new InotifyEventListener() {
            @Override
            public void filesystemEventOccurred(InotifyEvent e) {
            }

            @Override
            public void queueFull(EventQueueFull e) {
            }
        };
        ic.addListener(i1, wd1);
        set = ic.getListeners();
        assertNotNull(set);
        assertTrue(1 == set.size());

        final InotifyEventListener i2 = new InotifyEventListener() {
            @Override
            public void filesystemEventOccurred(InotifyEvent e) {
            }

            @Override
            public void queueFull(EventQueueFull e) {
            }
        };
        ic.addListener(i2, wd1);
        set = ic.getListeners();
        assertNotNull(set);
        assertTrue(2 == set.size());

        final InotifyEventListener i3 = i2;
        ic.addListener(i3, wd1);
        set = ic.getListeners();
        assertNotNull(set);
        assertTrue(2 == set.size());

        int wd2 = wd1 + 1;
        ic.addListener(i1, wd2);
        ic.addListener(i2, wd2);
        ic.addListener(i3, wd2);
        set = ic.getListeners();
        assertNotNull(set);
        assertTrue(2 == set.size());
    }

    /**
     * Test synchronization.
     */
    @Test
    public void testSynchronization() {
        final int runs = 1000;
        class PathPair {
            int wd;
            String path;
        }
        class ListenerPair {
            int wd;
            InotifyEventListener iel;
        }

        final BlockingQueue<PathPair> pathQueue = new LinkedBlockingQueue<>();
        final BlockingQueue<ListenerPair> listenerQueue =
                new LinkedBlockingQueue<>();

        Runnable pathAdder = new Runnable() {
            @Override
            public void run() {
                long startingMS = currentTimeMillis();
                int wd = rnd.nextInt();
                for (int i = 0; i < runs; i++) {
                    PathPair pp = new PathPair();
                    pp.wd = wd++;
                    pp.path = valueOf(startingMS++);
                    ic.addPath(pp.path, pp.wd);
                    pathQueue.add(pp);
                }
            }
        };

        Runnable pathRemover = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    try {
                        PathPair pp = pathQueue.take();
                        if (rnd.nextBoolean())
                            ic.removePath(pp.path);
                        else
                            ic.removePath(pp.wd);
                    } catch (InterruptedException e) {
                        fail(e.toString());
                        exit(1);
                    }
                }
            }
        };

        Runnable listenerAdder = new Runnable() {
            @Override
            public void run() {
                int wd = rnd.nextInt();
                for (int i = 0; i < runs; i++) {
                    InotifyEventListener l = new InotifyEventListener() {
                        @Override
                        public void filesystemEventOccurred(InotifyEvent e) {
                        }

                        @Override
                        public void queueFull(EventQueueFull e) {
                        }
                    };
                    ListenerPair lp = new ListenerPair();
                    lp.wd = wd++;
                    lp.iel = l;
                    ic.addListener(lp.iel, lp.wd);
                    listenerQueue.add(lp);
                }
            }
        };

        Runnable listenerRemover = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < runs; i++) {
                    try {
                        ListenerPair lp = listenerQueue.take();
                        ic.removeListener(lp.iel, lp.wd);
                    } catch (InterruptedException e) {
                        fail(e.toString());
                        exit(1);
                    }
                }
            }
        };

        final Thread paT = new Thread(pathAdder);
        final Thread paR = new Thread(pathRemover);
        final Thread laT = new Thread(listenerAdder);
        final Thread laR = new Thread(listenerRemover);

        paT.start();
        paR.start();
        laT.start();
        laR.start();

        try {
            out.println("(Waiting for synchronization test to finish).");
            paT.join();
            paR.join();
            laT.join();
            laR.join();

            assertTrue(ic.isEmpty());

        } catch (InterruptedException e) {
            fail(e.toString());
            exit(1);
        }

    }

}

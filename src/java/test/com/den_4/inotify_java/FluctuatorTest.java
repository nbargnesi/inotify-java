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
 * File: FluctuatorTest.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static org.junit.Assert.*;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.den_4.inotify_java.Fluctuator;
import com.den_4.inotify_java.exceptions.InotifyException;

/**
 * Fluctuator unit tests.
 * 
 * @author Nick Bargnesi
 * @since Version 2.0.3
 */
public class FluctuatorTest {
    
    File testFile;
    final Random rnd = new Random();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        testFile = File.createTempFile(getClass().getSimpleName(), "tmp");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        testFile.delete();
    }

    /**
     * Test method for {@link Fluctuator#Fluctuator(java.lang.String)}.
     */
    @Test
    public void testFluctuatorString() {
        Fluctuator f = null;
        try {
            f = new Fluctuator(testFile.getCanonicalPath());
            assertTrue(f.isActive());
            f.removeFile(testFile.getCanonicalPath());
            assertTrue(f.isActive());
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Test method for {@link Fluctuator#Fluctuator(java.util.Set)}.
     */
    @Test
    public void testFluctuatorSetOfString() {
        Fluctuator f = null;
        try {
            Set<String> set = new HashSet<String>();
            set.add(testFile.getCanonicalPath());
            f = new Fluctuator(set);
            assertTrue(f.isActive());
            f.removeFile(testFile.getCanonicalPath());
            assertTrue(f.isActive());
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Test method for {@link Fluctuator#Fluctuator()}.
     */
    @Test
    public void testFluctuator() {
        Fluctuator f = null;
        try {
            f = new Fluctuator();
            assertTrue(f.isActive());
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Test method for {@link Fluctuator#addFile(java.lang.String)}.
     */
    @Test
    public void testAddFile() {
        Fluctuator f = null;
        try {
            f = new Fluctuator();
            f.addFile(testFile.getCanonicalPath());
            assertTrue(f.isActive());
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Test method for
     * {@link Fluctuator#addListener(FluctuatorListener, java.lang.String)}.
     */
    @Test
    public void testAddListener() {
        Fluctuator f = null;
        try {
            String path = testFile.getCanonicalPath();
            f = new Fluctuator(path);
            f.addListener(new FluctuatorListener() {
                @Override
                public void queueFull(EventQueueFull e) {}
                @Override
                public void filesystemEventOccurred(InotifyEvent e) {}
                @Override
                public void fileSizeIncreased(FileSizeIncreasedEvent e) {}
                @Override
                public void fileSizeDecreased(FileSizeDecreasedEvent e) {}
            }, path);
            assertTrue(f.isActive());
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Test method for
     * {@link Fluctuator#removeListener(FluctuatorListener, java.lang.String)}.
     */
    @Test
    public void testRemoveListener() {
        Fluctuator f = null;
        try {
            String path = testFile.getCanonicalPath();
            f = new Fluctuator(path);
            final FluctuatorListener fl = new FluctuatorListener() {
                @Override
                public void queueFull(EventQueueFull e) {}
                @Override
                public void filesystemEventOccurred(InotifyEvent e) {}
                @Override
                public void fileSizeIncreased(FileSizeIncreasedEvent e) {}
                @Override
                public void fileSizeDecreased(FileSizeDecreasedEvent e) {}
            };
            f.addListener(fl, path);
            assertTrue(f.isActive());
            f.removeListener(fl, path);
            assertTrue(f.isActive());
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Test method for {@link Fluctuator#removeFile(java.lang.String)}.
     */
    @Test
    public void testRemoveFile() {
        Fluctuator f = null;
        try {
            f = new Fluctuator();
            String path = testFile.getCanonicalPath();
            f.addFile(path);
            assertTrue(f.isActive());
            f.removeFile(path);
            assertTrue(f.isActive());
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    /**
     * Test {@link Fluctuator} functionality.
     */
    @Test
    public void testFunctionality() {
        final int seconds = 5;
        final File testCase = testFile;
        final CyclicBarrier barrier = new CyclicBarrier(2);
        Fluctuator f = null;
        try {
            String path = testCase.getCanonicalPath();
            f = new Fluctuator(path);
            
            // Class invokes await on the barrier after seeing an increase event
            class IncreaseStub implements FluctuatorListener {

                @Override
                public void filesystemEventOccurred(InotifyEvent e) {}

                @Override
                public void queueFull(EventQueueFull e) {
                    fail("queue should not be full: " + e.toString());
                }

                @Override
                public void fileSizeIncreased(FileSizeIncreasedEvent e) {
                    try {
                        barrier.await();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                        fail(ie.toString());
                    } catch (BrokenBarrierException bbe) {
                        bbe.printStackTrace();
                        fail(bbe.toString());
                    }
                }

                @Override
                public void fileSizeDecreased(FileSizeDecreasedEvent e) {
                    fail("unexpected decreased event");
                }
            }
            
            final IncreaseStub is = new IncreaseStub();
            f.addListener(is, path);
            assertTrue(f.isActive());

            // Write junk to the test file
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        FileWriter fw = new FileWriter(testCase);
                        fw.write(valueOf(currentTimeMillis()));
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail(e.toString());
                    }
                }
            }).start();
            
            // Test receipt of the increased event occurs within 5 seconds
            barrier.await(seconds, TimeUnit.SECONDS);
            barrier.reset();
            f.removeListener(is, path);
            
            // Class invokes await on the barrier after seeing an decrease event
            class DecreaseStub implements FluctuatorListener {

                @Override
                public void filesystemEventOccurred(InotifyEvent e) {}

                @Override
                public void queueFull(EventQueueFull e) {
                    fail("queue should not be full: " + e.toString());
                }

                @Override
                public void fileSizeIncreased(FileSizeIncreasedEvent e) {
                    fail("unexpected increased event");
                }

                @Override
                public void fileSizeDecreased(FileSizeDecreasedEvent e) {
                    try {
                        barrier.await();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                        fail(ie.toString());
                    } catch (BrokenBarrierException bbe) {
                        bbe.printStackTrace();
                        fail(bbe.toString());
                    }
                }
            }
            
            final DecreaseStub ds = new DecreaseStub();
            f.addListener(ds, path);
            
            // Truncate the test file
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        FileOutputStream fos = new FileOutputStream(testCase);
                        FileChannel fc = fos.getChannel();
                        fc.truncate(testCase.length());
                        fc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail(e.toString());
                    }
                }
            }).start();
            
            // Test receipt of the increased event occurs within 5 seconds
            barrier.await(seconds, TimeUnit.SECONDS);
            
            f.destroy();
        } catch (InotifyException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.toString());
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
            fail("barrier should not have been broken");
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("did not get file size event within time specified (" +
                    seconds + " seconds)");
        }
    }

}

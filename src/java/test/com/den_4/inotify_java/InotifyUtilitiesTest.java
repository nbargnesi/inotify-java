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
 * File: InotifyUtilitiesTest.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.lang.System.getProperty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Queue;

import org.junit.Test;

import com.den_4.inotify_java.exceptions.PermissionDenied;

/**
 * Inotify utilities unit tests.
 * 
 * @author Nick Bargnesi
 * @since Version 2.0.3
 */
public class InotifyUtilitiesTest {

    /**
     * Test method for {@link InotifyUtilities#getPaths(java.lang.String)}.
     */
    @Test
    public void testGetPaths() {
        final String curdir = getProperty("user.dir");
        File testDir = new File(curdir + "/test_directory");
        testDir.deleteOnExit();
        assertTrue(testDir.mkdir());
        try {
            Queue<String> paths =
                    InotifyUtilities.getPaths(testDir.getCanonicalPath());
            assertTrue(1 == paths.size());
            File subDir1 = new File(testDir + "/subdir1");
            subDir1.deleteOnExit();
            assertTrue(subDir1.mkdir());
            paths = InotifyUtilities.getPaths(testDir.getCanonicalPath());
            assertTrue(2 == paths.size());
            assertTrue(subDir1.delete());
            paths = InotifyUtilities.getPaths(testDir.getCanonicalPath());
            assertTrue(1 == paths.size());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
        assertTrue(testDir.delete());
    }

    /**
     * Test method for {@link InotifyUtilities#setMaximumQueuedEvents(long)}.
     */
    @Test
    public void testSetMaximumQueuedEvents() {
        String user = System.getProperty("user.name");
        long max = InotifyUtilities.getMaximumQueuedEvents();
        assertTrue(max >= 0);
        if (user.equals("root")) {
            InotifyUtilities.setMaximumQueuedEvents(max + 1);
            assertTrue((max + 1) == InotifyUtilities.getMaximumQueuedEvents());
            InotifyUtilities.setMaximumQueuedEvents(max);
            assertTrue(max == InotifyUtilities.getMaximumQueuedEvents());
        } else {
            try {
                InotifyUtilities.setMaximumQueuedEvents(max);
                fail("expected permission denied for user: " + user);
            } catch (PermissionDenied pd) {
                // Success
            }
        }
    }

    /**
     * Test method for {@link InotifyUtilities#getMaximumQueuedEvents()}.
     */
    @Test
    public void testGetMaximumQueuedEvents() {
        long max = InotifyUtilities.getMaximumQueuedEvents();
        assertTrue(max >= 0);
    }

    /**
     * Test method for {@link InotifyUtilities#isMaximumQueuedEventsTunable()}.
     */
    @Test
    public void testIsMaximumQueuedEventsTunable() {
        String user = System.getProperty("user.name");
        if (user.equals("root"))
            assertTrue(InotifyUtilities.isMaximumQueuedEventsTunable());
        else
            assertFalse(InotifyUtilities.isMaximumQueuedEventsTunable());
    }

    /**
     * Test method for {@link InotifyUtilities#setMaximumUserInstances(long)}.
     */
    @Test
    public void testSetMaximumUserInstances() {
        String user = System.getProperty("user.name");
        long max = InotifyUtilities.getMaximumUserInstances();
        assertTrue(max >= 0);
        if (user.equals("root")) {
            InotifyUtilities.setMaximumUserInstances(max + 1);
            assertTrue((max + 1) == InotifyUtilities.getMaximumQueuedEvents());
            InotifyUtilities.setMaximumUserInstances(max);
            assertTrue(max == InotifyUtilities.getMaximumQueuedEvents());
        } else {
            try {
                InotifyUtilities.setMaximumUserInstances(max);
                fail("expected permission denied for user: " + user);
            } catch (PermissionDenied pd) {
                // Success
            }
        }
    }

    /**
     * Test method for {@link InotifyUtilities#getMaximumUserInstances()}.
     */
    @Test
    public void testGetMaximumUserInstances() {
        long max = InotifyUtilities.getMaximumUserInstances();
        assertTrue(max >= 0);
    }

    /**
     * Test method for {@link InotifyUtilities#isMaximumUserInstancesTunable()}.
     */
    @Test
    public void testIsMaximumUserInstancesTunable() {
        String user = System.getProperty("user.name");
        if (user.equals("root"))
            assertTrue(InotifyUtilities.isMaximumUserInstancesTunable());
        else
            assertFalse(InotifyUtilities.isMaximumQueuedEventsTunable());
    }

    /**
     * Test method for {@link InotifyUtilities#setMaximumUserWatches(long)}.
     */
    @Test
    public void testSetMaximumUserWatches() {
        String user = System.getProperty("user.name");
        long max = InotifyUtilities.getMaximumUserWatches();
        assertTrue(max >= 0);
        if (user.equals("root")) {
            InotifyUtilities.setMaximumUserWatches(max + 1);
            assertTrue((max + 1) == InotifyUtilities.getMaximumQueuedEvents());
            InotifyUtilities.setMaximumUserWatches(max);
            assertTrue(max == InotifyUtilities.getMaximumQueuedEvents());
        } else {
            try {
                InotifyUtilities.setMaximumUserWatches(max);
                fail("expected permission denied for user: " + user);
            } catch (PermissionDenied pd) {
                // Success
            }
        }
    }

    /**
     * Test method for {@link InotifyUtilities#getMaximumUserWatches()}.
     */
    @Test
    public void testGetMaximumUserWatches() {
        long max = InotifyUtilities.getMaximumUserWatches();
        assertTrue(max >= 0);
    }

    /**
     * Test method for {@link InotifyUtilities#isMaximumUserWatchesTunable()}.
     */
    @Test
    public void testIsMaximumUserWatchesTunable() {
        String user = System.getProperty("user.name");
        if (user.equals("root"))
            assertTrue(InotifyUtilities.isMaximumQueuedEventsTunable());
        else
            assertFalse(InotifyUtilities.isMaximumQueuedEventsTunable());
    }

}

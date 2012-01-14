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
 * File: Test592201.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static com.den_4.inotify_java.Utilities.loadLibrary;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.den_4.inotify_java.exceptions.InotifyException;

/**
 * Invoking {@code destroy()} does not always terminate threads which prevents
 * JVM from exiting.
 * <p>
 * <a href="https://bugs.launchpad.net/inotify-java/+bug/592201">Bug 592201</a>
 * </p>
 * 
 * @author Nick Bargnesi
 */
public class Test592201 {

    /**
     * Asserts native library is loaded.
     */
    @BeforeClass
    public static void beforeClass() {
        loadLibrary();
    }

    /**
     * Test method for {@link com.den_4.inotify_java.MonitorService#destroy()}.
     */
    @Test
    public void testDestroy() {
        MonitorService m = null;
        try {
            m = new MonitorService();
            assertTrue(m.isActive());
            m.destroy();
        } catch (InotifyException e) {
            fail("error constructing: " + e);
        }
    }

    /**
     * Runs the test case independent of JUnit (the test will always pass if you
     * use the JUnit test harness).
     * 
     * @param args None
     */
    public static void main(String... args) {
        loadLibrary();
        new Test592201().testDestroy();
    }

}

/**
 * Copyright © 2011-2012 Nick Bargnesi <nick@den-4.com>. All rights reserved.
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
 * File: Utilities.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

/**
 * Inotify testing utilities.
 * 
 * @author Nick Bargnesi
 * @since Version 2.1
 */
public final class Utilities {

    /**
     * Loads the native library for tests that require it.
     */
    public static void loadLibrary() {
        if (NativeInotify.isLibraryLoaded()) return;
        String cwd = getProperty("user.dir");
        if (!cwd.endsWith("src/java")) {
            fail("expected path to be [...]/src/java, but its " + cwd);
        }
        String cppsrc = cwd.replaceAll("src/java", "src/cpp");
        String library = cppsrc + "/libinotify-java.so";
        if (!new File(library).canRead()) {
            fail(format("native library not found: %s", library));
        }
        NativeInotify.loadLibrary(library);
        assertTrue(NativeInotify.isLibraryLoaded());
    }

}


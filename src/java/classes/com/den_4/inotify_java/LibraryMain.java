/**
 * Copyright © 2012 Nick Bargnesi <nick@den-4.com>. All rights reserved.
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
 * File: LibraryMain.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.lang.System.out;

/**
 * Java library main class, dumping library information to {@link System#out}.
 */
public class LibraryMain {
    final static String NAME;
    final static String VERSION;
    final static String WEBSITE;
    final static String BUGS;
    final static String DOCUMENTATION;
    final static String BUILD;

    static {
        NAME = "inotify-java";
        VERSION = "2.1";
        WEBSITE = "https://bitbucket.org/nbargnesi/inotify-java";
        BUGS = "https://bitbucket.org/nbargnesi/inotify-java/issues";
        DOCUMENTATION = "https://bitbucket.org/nbargnesi/inotify-java/wiki";
        BUILD = "20120113";
    }

    /**
     * Prints the library name, version, build, website, bug tracker, and
     * documentation information to stdout.
     */
    public static void main(String... args) {
        final StringBuilder bldr = new StringBuilder();
        bldr.append(format("This is %s version %s.", NAME, VERSION));
        bldr.append("\n");
        bldr.append(format("Website: %s", WEBSITE));
        bldr.append("\n");
        bldr.append(format("Bugs: %s", BUGS));
        bldr.append("\n");
        bldr.append(format("Documentation: %s", DOCUMENTATION));
        bldr.append("\n");
        bldr.append(format("Build: %s", BUILD));
        bldr.append("\n");
        out.println(bldr.toString());

        bldr.setLength(0);
        try {
            NativeInotify.loadLibrary(null);
            bldr.append("The native library loads successfully.");
        } catch (UnsatisfiedLinkError e) {
            bldr.append("The native library failed to load.");
            bldr.append("\n");
            String lpath = getProperty("java.library.path");
            bldr.append(format("The current Java library path is: %s", lpath));
            bldr.append("\n");
            String msg = e.getMessage();
            bldr.append(format("(error was: %s)", msg));
        }
        out.println(bldr.toString());

    }

}

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
 * File: Demo.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.lang.Integer.parseInt;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;
import static com.den_4.inotify_java.enums.Event.*;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * inotify-java demo.
 */
public class Demo {

    /**
     * Demo entry point.
     * 
     * @param args Command-line arguments; {@code args[0]} must be a directory
     * and {@code args[1]} must be an integer
     */
    public static void main(String... args) throws Exception {
        if (args == null || args.length != 2) {
            usage();
        }
        String path = args[0];
        int seconds = 0;
        try {
            seconds = parseInt(args[1]);
        } catch (NumberFormatException e) {
            usage();
        }
        File dir = new File(path);
        if (!dir.isDirectory()) {
            usage();
        }
        run(dir, seconds);
    }

    private static void run(File dir, int seconds) throws Exception {
        Inotify i = new Inotify();
        int wd = i.addWatch(dir.getPath(), All);
        final AtomicInteger qevs = new AtomicInteger();
        final AtomicInteger evs = new AtomicInteger();
        i.addListener(wd, new InotifyEventListener() {
            @Override
            public void queueFull(EventQueueFull e) {
                out.println(e);
                qevs.incrementAndGet();
            }
            @Override
            public void filesystemEventOccurred(InotifyEvent e) {
                out.println(e);
                evs.incrementAndGet();
            }
        });
        sleep(seconds * 1000);
        i.destroy();
        
        String fmt = "Received %d filesystem events and %d queue full events";
        fmt = fmt.concat(" in %d seconds");
        out.println(format(fmt, evs.get(), qevs.get(), seconds));
    }

    private static void usage() {
        final StringBuilder bldr = new StringBuilder();
        bldr.append("Usage: <directory> <seconds>\n");
        bldr.append("\n");
        bldr.append("This is a demo application for inotify-java. It will\n");
        bldr.append("print all events occurring in the given directory for\n");
        bldr.append("the number of seconds specified.\n");
        bldr.append("\n");
        bldr.append("Example: /tmp/demo 10");
        out.println(bldr);
        exit(1);
    }

}

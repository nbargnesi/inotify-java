#!/usr/bin/env python2
#
# This file is part of inotify-java.
# 
# The filesystem load simulator is free software: you can redistribute it
# and/or modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the License,
# or (at your option) any later version.
#
# The filesystem load simulator is distributed in the hope that it will be
# useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
# Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with inotify-java.  If not, see <http://www.gnu.org/licenses/>.
#

'''
Simulates filesystem activity occurring within the simulation directory.
Interrupting the simulation will stop it, removing all files from the
simulation directory.
'''
import sys
import argparse
from random import choice, randint
import random
from os import chdir, remove, listdir, chmod, read
from time import sleep, time
from datetime import datetime as dt
from threading import Thread, Lock
from stat import *

class Usage(Exception):
    def __init__(self, msg):
        self.msg = msg

class FileCreator(Thread):
    def __init__(self, sleep_period, lock, set, jitter, bytes):
        Thread.__init__(self)
        self.sleep_period = sleep_period
        self.lock = lock
        self.set = set
        self.jitter = jitter
        self.bytes = bytes
    def run(self):
        while True:
            jitter = random.uniform(self.jitter[0], self.jitter[1])
            sleep(self.sleep_period * jitter)
            filename = "%.05f" % time()
            try:
                with open(filename, 'w+') as dest: 
                    with open('/dev/urandom') as source:
                        bytes = randint(self.bytes[0], self.bytes[1])
                        dest.write(source.read(bytes))
            except:
                try:
                    remove(filename)
                except OSError:
                    pass
                filename = None
            if filename is not None:
                with self.lock:
                    self.set.append(filename)

class FileDeleter(Thread):
    def __init__(self, sleep_period, lock, set, jitter):
        Thread.__init__(self)
        self.sleep_period = sleep_period
        self.lock = lock
        self.set = set
        self.jitter = jitter
    def run(self):
        while True:
            jitter = random.uniform(self.jitter[0], self.jitter[1])
            sleep(self.sleep_period * jitter)
            file = None
            with self.lock:
                if len(self.set) != 0:
                    file = choice(self.set)
                    self.set.remove(file)
                    try:
                        remove(file)
                    except OSError:
                        pass

class MetaChanger(Thread):
    def __init__(self, sleep_period, lock, set, jitter):
        Thread.__init__(self)
        self.sleep_period = sleep_period
        self.lock = lock
        self.set = set
        self.jitter = jitter
    def run(self):
        while True:
            jitter = random.uniform(self.jitter[0], self.jitter[1])
            sleep(self.sleep_period * jitter)
            file = None
            with self.lock:
                if len(self.set) != 0:
                    file = choice(self.set)
                    self.set.remove(file)
            if file is not None:
                try:
                    chmod(file, S_IREAD | S_IWRITE | S_IEXEC)
                except OSError:
                    pass
                with self.lock:
                    self.set.append(file)

class Reader(Thread):
    def __init__(self, sleep_period, lock, set, jitter, bytes):
        Thread.__init__(self)
        self.sleep_period = sleep_period
        self.lock = lock
        self.set = set
        self.jitter = jitter
        self.bytes = bytes
    def run(self):
        while True:
            jitter = random.uniform(self.jitter[0], self.jitter[1])
            sleep(self.sleep_period * jitter)
            file = None
            with self.lock:
                if len(self.set) != 0:
                    file = choice(self.set)
                    self.set.remove(file)
            if file is not None:
                with open(file) as source:
                    bytes = randint(self.bytes[0], self.bytes[1])
                    source.read(bytes)
                with self.lock:
                    self.set.append(file)

class Writer(Thread):
    def __init__(self, sleep_period, lock, set, jitter, bytes):
        Thread.__init__(self)
        self.daemon = True
        self.sleep_period = sleep_period
        self.lock = lock
        self.set = set
        self.jitter = jitter
        self.bytes = bytes
    def run(self):
        while True:
            jitter = random.uniform(self.jitter[0], self.jitter[1])
            sleep(self.sleep_period * jitter)
            file = None
            with self.lock:
                if len(self.set) != 0:
                    file = choice(self.set)
                    self.set.remove(file)
            if file is not None:
                bytes = randint(self.bytes[0], self.bytes[1])
                with open(file, 'w+') as dest: 
                    with open('/dev/urandom') as source:
                        try:
                            dest.write(source.read(bytes))
                        except IOError:
                            remove(file)
                            file = None
            if file is not None:
                with self.lock:
                    self.set.append(file)

def main(argv=None):
    if argv is None:
        argv = sys.argv
    usage = argv[0] + ' [ARGUMENT]...\n' + \
            "Try '" + argv[0] + " --help' for more information."
    ap = argparse.ArgumentParser(description=__doc__, usage=usage)

    ap.add_argument('-d', required = True, metavar='<empty directory>',
            help='simulation directory')

    ap.add_argument('-up', default = 3600, type = int, metavar = '<seconds>',
            help='simulation runtime (default: 3600 seconds)')

    ap.add_argument('-sp', default = 1, type = float, metavar = '<seconds>',
            help='sleep period for each thread (default: 1 second)')

    ap.add_argument('-j', default = [1.0, 1.0], type = float,
            metavar = 'N', nargs = 2,
            help='per-thread jitter factor (default: 1.0, 1.0)')

    ap.add_argument('-cb', default = [4096, 5000000], type = int,
            metavar = 'N', nargs = 2,
            help='filesize range for new files (default: 4k, 5 MB)')

    ap.add_argument('-rb', default = [4096, 5000000], type = int,
            metavar = 'N', nargs = 2,
            help='read bytes (default: 4k, 5 MB)')

    ap.add_argument('-wb', default = [4096, 5000000], type = int,
            metavar = 'N', nargs = 2,
            help='write bytes (default: 4k, 5 MB)')

    ap.add_argument('-ct', default = 1, type=int, metavar='<threads>',
            help='threads creating files (default: 1 thread)')
    ap.add_argument('-dt', default = 1, type=int, metavar='<threads>',
            help='threads deleting files (default: 1 thread)')
    ap.add_argument('-mt', default = 1, type=int, metavar='<threads>',
            help='threads changing file metadata (default: 1 thread)')
    ap.add_argument('-rt', default = 1, type=int, metavar='<threads>',
            help='threads reading files (default: 1 thread)')
    ap.add_argument('-wt', default = 1, type=int, metavar='<threads>',
            help='threads writing to files (default: 1 thread)')

    args = ap.parse_args()
    
    try:
        directory = args.d
        create = args.ct
        delete = args.dt
        meta = args.mt
        read = args.rt
        write = args.wt
        jit = args.j

        cb = args.cb
        rb = args.rb
        wb = args.wb
        
        nap = args.sp
        lck = Lock()
        fset = []

        simstart = dt.today()
        print 'Simulation started at:', simstart
        print '--'
        print 'Directory:', directory
        print 'Runtime (seconds):', args.up
        print 'Thread sleep period (seconds):', nap
        print 'Jitter factor:', jit
        print 'Filesize range for new files (bytes):', cb
        print 'Read range (bytes):', rb
        print 'Write range (bytes):', wb
        print '\nThreads:'
        print '\tCreating:', create
        print '\tDeleting:', delete
        print '\tChanging metadata:', meta
        print '\tReading:', read
        print '\tWriting:', write

        print '\nSimulation running.'

        chdir(directory)

        [FileCreator(nap, lck, fset, jit, cb).start() for x in range(0, create)]
        [FileDeleter(nap, lck, fset, jit).start() for x in range(0, delete)]
        [MetaChanger(nap, lck, fset, jit).start() for x in range(0, meta)]
        [Reader(nap, lck, fset, jit, rb).start() for x in range(0, read)]
        [Writer(nap, lck, fset, jit, wb).start() for x in range(0, write)]

        sleep(args.up)
        raise KeyboardInterrupt()

    except Usage, err:
        print >>sys.stderr, err.msg
        return 2
    except KeyboardInterrupt, err:
        print '\nTerminating simulation.'
        with lck:
            [remove(file) for file in fset]
            [remove(file) for file in listdir(directory)]
        simend = dt.today()
        print 'Simulation ended at:', simend
        print 'Simulation ran for', (simend - simstart).seconds, 'seconds.'
        return 0

if __name__ == "__main__":
    sys.exit(main())


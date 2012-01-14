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
 * File: Inotify.java
 * Project: inotify-java
 * Package: com.den_4.inotify_java
 * Author: Nick Bargnesi
 */
package com.den_4.inotify_java;

import static java.lang.System.load;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import com.den_4.inotify_java.exceptions.InotifyException;
import com.den_4.inotify_java.exceptions.InsufficientKernelMemoryException;
import com.den_4.inotify_java.exceptions.InvalidWatchDescriptorException;
import com.den_4.inotify_java.exceptions.SystemLimitException;
import com.den_4.inotify_java.exceptions.UserInstanceLimitException;
import com.den_4.inotify_java.exceptions.UserWatchLimitException;

/**
 * This class serves as the base class for all Inotify types, providing a bridge
 * to the native implementation and interface.
 * 
 * @author Nick Bargnesi
 * @since Version 2
 */
public abstract class NativeInotify {

    /** Watch descriptor associated with this inotify instance. */
    protected int fileDescriptor;

    /** Flag used to indicate an active file descriptor. */
    private boolean fdActive = false;

    /** Provides thread-safety for {@link #fdActive} */
    private Semaphore activeSem;

    /* Flag indicating whether the native library is loaded. */
    private static boolean nativeLibLoaded;
    /* Guarantee library loading is thread-safe. */
    private static ReentrantLock lock = new ReentrantLock();

    /**
     * Contains the file descriptors for the read/write ends of a pipe, used for
     * IPC purposes.
     */
    private int[] pipes;

    /**
     * Constructs a new Inotify instance.
     * 
     * @throws InotifyException Thrown if the native inotify object could not be
     * constructed. The cause of the exception will be provided.
     */
    public NativeInotify() throws InotifyException {
        try {
            if (!isLibraryLoaded())
                loadLibrary(null);
            fileDescriptor = init();
        } catch (UnsatisfiedLinkError e) {
            final String msg = "unsatisfied link";
            InotifyException ie = new InotifyException(msg, e);
            throw ie;
        } catch (InsufficientKernelMemoryException e) {
            final String msg = "kernel memory exception";
            InotifyException ie = new InotifyException(msg, e);
            throw ie;
        } catch (SystemLimitException e) {
            final String msg = "system limit reached";
            InotifyException ie = new InotifyException(msg, e);
            throw ie;
        } catch (UserInstanceLimitException e) {
            final String msg = "user limit reached";
            InotifyException ie = new InotifyException(msg, e);
            throw ie;
        }
        fdActive = true;
        activeSem = new Semaphore(1, true);
    }

    /**
     * Returns {@code true} if the native library has been loaded, {@code false}
     * if not.
     * <p>
     * This method is {@link com.den_4.inotify_java.ThreadSafe thread-safe}.
     * </p>
     * 
     * @return {@code true} if the native library has been loaded, {@code false}
     * if not
     */
    @ThreadSafe
    public static boolean isLibraryLoaded() {
        lock.lock();
        try {
            return nativeLibLoaded;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Loads the native library specified by the filename. Supplying a
     * {@code filename} value of {@code null} will result in the library being
     * loaded using the library paths provided to the virtual machine.
     * <p>
     * This method is {@link com.den_4.inotify_java.ThreadSafe thread-safe}.
     * </p>
     * 
     * @param filename The file to load
     * @throws SecurityException Thrown if a security manager is preventing
     * loading of the dyanmic library
     * @exception UnsatisfiedLinkError Thrown if the library does not exist
     * @see java.lang.SecurityManager#checkLink(String)
     */
    @ThreadSafe
    public static void loadLibrary(String filename) throws UnsatisfiedLinkError {
        lock.lock();
        try {
            if (filename == null) 
                System.loadLibrary("inotify-java"); //$NON-NLS-1$
            else load(filename);
            nativeLibLoaded = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Invokes {@link #destroy()}.
     */
    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    /**
     * Returns {@code true} if this Inotify instance is active, {@code false} if
     * it has been destroyed.
     * <p>
     * This method is thread-safe and guarantees the returned value is accurate
     * at the time of invocation.
     * </p>
     * 
     * @return {@code true} if this Inotify instance is active, {@code false} if
     * it has been destroyed
     * @see #isInactive()
     * @see #destroy()
     */
    @ThreadSafe
    public final boolean isActive() {
        activeSem.acquireUninterruptibly();
        boolean active = fdActive;
        activeSem.release();
        return active;
    }

    /**
     * Returns {@code true} if this Inotify instance has been destroyed,
     * {@code false} if it is active.
     * <p>
     * This method is thread-safe and guarantees the returned value is accurate
     * at the time of invocation.
     * </p>
     * 
     * @return {@code true} if this Inotify instance has been destroyed,
     * {@code false} if it is active
     * @see #isActive()
     * @see #destroy()
     */
    @ThreadSafe
    public final boolean isInactive() {
        activeSem.acquireUninterruptibly();
        boolean inactive = !fdActive;
        activeSem.release();
        return inactive;
    }

    /**
     * Returns the descriptor associated with this Inotify instance.
     * 
     * @return int
     */
    public final int getFileDescriptor() {
        return fileDescriptor;
    }

    /**
     * Destroys the underlying native instance, thus freeing allocated native
     * resources. This method is a no-op if the
     * {@link com.den_4.inotify_java.NativeInotify NativeInotify} object is not
     * active.
     * 
     * @see #isActive()
     * @see #isInactive()
     */
    public void destroy() {
        activeSem.acquireUninterruptibly();
        try {
            if (fdActive) {
                close(fileDescriptor);
                fdActive = false;
            }
        } finally {
            activeSem.release();
        }
    }

    /**
     * Adds a new watch, or modifies an existing watch for the file or directory
     * whose location is specified by the {@code path} argument. The events to
     * be monitored are specified by the bit-mask, {@code constantMask}.
     * 
     * @param path Path to watch
     * @param constantMask Mask of events
     * @return int The unique watch descriptor associated with the specified
     * {@code path} argument. The watch descriptor is later returned from
     * received events, indicating file system events.
     * @throws InotifyException Thrown if the native inotify object could not be
     * constructed. The cause of the exception will be provided.
     * @see #removeWatch(int)
     */
    int addWatch(String path, int constantMask) throws InotifyException {
        if (isInactive())
            throw new UnsupportedOperationException("not active");
        if (path == null)
            throw new IllegalArgumentException("path cannot be null");

        try {
            int wd = add_watch(fileDescriptor, path, constantMask);
            return wd;
        } catch (UserWatchLimitException e) {
            InotifyException ie = new InotifyException("user limit reached", e);
            throw ie;
        }
    }

    /**
     * Removes an item from the watch list by the specified watch descriptor
     * {@code wd}.
     * 
     * @param wd Unique integer descriptor returned by the {@link #addWatch}
     * method.
     * @return {@code true} if the watch was successfully removed, {@code false}
     * @throws InvalidWatchDescriptorException Thrown when the watch descriptor
     * is not valid for this {@code Inotify} instance
     * @see #addWatch(String, int)
     */
    boolean removeWatch(int wd) throws InvalidWatchDescriptorException {
        if (isInactive())
            throw new UnsupportedOperationException("not active");

        if (rm_watch(fileDescriptor, wd) == 0)
            return true;
        return false;
    }

    /**
     * Services the native event queue until this this object is destroyed.
     * 
     * @see #destroy()
     */
    void read() {
        if (isInactive())
            return;
        read(fileDescriptor);
    }

    /**
     * Receives an {@link InotifyEvent} from the native interface.
     * <p>
     * Implementations should consider minimizing the time to process the
     * {@link InotifyEvent}. The more time this handler takes, the less the
     * native queue is processed. This increases the likelihood of the event
     * queue overflowing.
     * </p>
     * 
     * @param e InotifyEvent from the native interface
     */
    abstract void eventHandler(InotifyEvent e);

    /**
     * Invokes the inotify_init native routine, which in turn initializes a
     * native instance.
     * 
     * @return int File descriptor associated with a new inotify event queue.
     * @throws InsufficientKernelMemoryException Checked exception thrown to
     * indicate an insufficient amount of kernel memory is available for a new
     * native inotify instance.
     * @throws SystemLimitException Checked exception thrown to indicate the
     * system limit of total file descriptors has been reached.
     * @throws UserInstanceLimitException Checked exception thrown to indicate
     * the maximum number of inotify instances has been reached.
     */
    private native int init() throws InsufficientKernelMemoryException,
            SystemLimitException, UserInstanceLimitException;

    /**
     * Destroys the underlying inotify instance by closing the file descriptor.
     */
    private native void close(int fd);

    /**
     * Invokes the inotify_add_watch native routine, adding to or modifying an
     * existing watch.
     * 
     * @param fd The fd argument is a file descriptor referring to the inotify
     * instance whose watch list is to be modified.
     * @param path Path to be watched.
     * @param mask Bit-mask argument
     * @return int Returns the unique watch descriptor associated with the path
     * for this Inotify instance. If path was not previously being watched by
     * this Inotify instance, then the watch descriptor is newly allocated. If
     * path was already being watched, then the descriptor for the existing
     * watch is returned.
     * @throws UserWatchLimitException Checked exception thrown to indicate the
     * maximum number of inotify watches has been reached.
     */
    private native int add_watch(int fd, String path, int mask)
            throws UserWatchLimitException;

    /**
     * @param fd The fd argument is a file descriptor referring to the Inotify
     * instance whose watch list is to be modified.
     * @param wd The wd argument is the watch descriptor to be removed.
     * @return int Returns zero on success or -1 if an error occurred.
     */
    private native int rm_watch(int fd, int wd);

    /**
     * Reads the available events from the inotify event queue.
     * 
     * @param fd File descriptor referring to the inotify instance whose queue
     * is to be read.
     */
    private native void read(int fd);

    /**
     * Sets the read and write file descriptors of the IPC pipe associated with
     * this {@link com.den_4.inotify_java.NativeInotify NativeInotify} object.
     * 
     * @param readEnd File descriptor of pipe read end
     * @param writeEnd File descriptor of pipe write end
     */
    private void setPipes(int readEnd, int writeEnd) {
        pipes = new int[2];
        pipes[0] = readEnd;
        pipes[1] = writeEnd;
    }

    /**
     * Gets the file descriptor of the read end of the IPC pipe.
     * 
     * @return IPC pipe read end file descriptor
     */
    private int getPipeRead() {
        return pipes[0];
    }

    /**
     * Gets the file descriptor of the write end of the IPC pipe.
     * 
     * @return IPC pipe write end file descriptor
     */
    private int getPipeWrite() {
        return pipes[1];
    }
}

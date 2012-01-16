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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with inotify-java.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File: inotify-interface.cpp
 * Project: inotify-java
 * Author: Nick Bargnesi
 */

#include "inotify-interface.h"
#include <sys/epoll.h>

/*
 * Function: JNI_OnLoad
 *
 *     The VM calls JNI_OnLoad when the native library is loaded.
 *
 * Returns:
 *     The constant: JNI_VERSION_1_6 - specified by jni.h
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *j, void *reserved) {
    debug(__func__ << ": loaded on pid " << getpid());
    jvm_ptr = j;
    JNIEnv *e;
    jclass class_inst;
    jmethodID method_id;

    j->GetEnv((void **) &e, JNI_VERSION_1_6);

    /* Classes */
    class_inst = (e)->FindClass("com/den_4/inotify_java/exceptions/UserWatchLimitException");
    if (!class_inst) goto fail;
    user_watch_limit_exception = (jclass) (e)->NewGlobalRef(class_inst);
    (e)->DeleteLocalRef(class_inst);

    class_inst = (e)->FindClass("com/den_4/inotify_java/exceptions/UserInstanceLimitException");
    if (!class_inst) goto fail;
    user_instance_limit_exception = (jclass) (e)->NewGlobalRef(class_inst);
    (e)->DeleteLocalRef(class_inst);

    class_inst = (e)->FindClass("com/den_4/inotify_java/exceptions/SystemLimitException");
    if (!class_inst) goto fail;
    system_limit_exception = (jclass) (e)->NewGlobalRef(class_inst);
    (e)->DeleteLocalRef(class_inst);

    class_inst = (e)->FindClass("com/den_4/inotify_java/exceptions/InsufficientKernelMemoryException");
    if (!class_inst) goto fail;
    insufficient_kernel_memory_exception = (jclass) (e)->NewGlobalRef(class_inst);
    (e)->DeleteLocalRef(class_inst);

    class_inst = (e)->FindClass("com/den_4/inotify_java/exceptions/InotifyException");
    if (!class_inst) goto fail;
    inotify_exception = (jclass) (e)->NewGlobalRef(class_inst);
    (e)->DeleteLocalRef(class_inst);

    class_inst = (e)->FindClass("com/den_4/inotify_java/NativeInotify");
    if (!class_inst) goto fail;
    native_inotify = (jclass) (e)->NewGlobalRef(class_inst);
    (e)->DeleteLocalRef(class_inst);

    class_inst = (e)->FindClass("com/den_4/inotify_java/InotifyEvent");
    if (!class_inst) goto fail;
    inotify_event = (jclass) (e)->NewGlobalRef(class_inst);
    (e)->DeleteLocalRef(class_inst);

    /* Methods */
    method_id = (e)->GetMethodID(native_inotify, "setPipes", "(II)V");
    if (!method_id) goto fail;
    native_inotify_setPipes = (jmethodID) (e)->NewGlobalRef((jobject) method_id);
    (e)->DeleteLocalRef((jobject) method_id);

    method_id = (e)->GetMethodID(native_inotify, "getPipeWrite", "()I");
    if (!method_id) goto fail;
    native_inotify_getPipeWrite = (jmethodID) (e)->NewGlobalRef((jobject) method_id);
    (e)->DeleteLocalRef((jobject) method_id);

    method_id = (e)->GetMethodID(native_inotify, "getPipeRead", "()I");
    if (!method_id) goto fail;
    native_inotify_getPipeRead = (jmethodID) (e)->NewGlobalRef((jobject) method_id);
    (e)->DeleteLocalRef((jobject) method_id);

    method_id = (e)->GetMethodID(native_inotify, "getFileDescriptor", "()I");
    if (!method_id) goto fail;
    native_inotify_getFileDescriptor = (jmethodID) (e)->NewGlobalRef((jobject) method_id);
    (e)->DeleteLocalRef((jobject) method_id);

    method_id = (e)->GetMethodID(native_inotify, "eventHandler", "(Lcom/den_4/inotify_java/InotifyEvent;)V");
    if (!method_id) goto fail;
    native_inotify_eventHandler = (jmethodID) (e)->NewGlobalRef((jobject) method_id);
    (e)->DeleteLocalRef((jobject) method_id);

    method_id = (e)->GetMethodID(inotify_event, "<init>", "(III)V");
    if (!method_id) goto fail;
    inotify_event_init_III_V = (jmethodID) (e)->NewGlobalRef((jobject) method_id);
    (e)->DeleteLocalRef((jobject) method_id);

    method_id = (e)->GetMethodID(inotify_event, "<init>", "(IIILjava/lang/String;)V");
    if (!method_id) goto fail;
    inotify_event_init_III_Ljava_lang_String = (jmethodID) (e)->NewGlobalRef((jobject) method_id);
    (e)->DeleteLocalRef((jobject) method_id);

    // Success
    return JNI_VERSION_1_6;
fail:
    // Failure
    cerr << __func__ << ": failed to cache global references, this will result in unsatisfied link errors!" << endl;
    return -1;
}

/*
 * Function: JNI_OnUnload
 *
 *     The VM calls JNI_OnUnload when the class loader containing the native
 *     library is garbage collected.
 */
JNIEXPORT void JNI_OnUnload(JavaVM *j, void *reserved) { 
    debug(__func__);
    JNIEnv *e;
    j->GetEnv((void **) &e, JNI_VERSION_1_6);

    /* Classes */
    (e)->DeleteGlobalRef(user_instance_limit_exception);
    (e)->DeleteGlobalRef(system_limit_exception);
    (e)->DeleteGlobalRef(insufficient_kernel_memory_exception);
    (e)->DeleteGlobalRef(inotify_exception);
    (e)->DeleteGlobalRef(native_inotify);
    (e)->DeleteGlobalRef(inotify_event);

    /* Methods */
    (e)->DeleteGlobalRef((jobject) native_inotify_setPipes);
    (e)->DeleteGlobalRef((jobject) native_inotify_getPipeWrite);
    (e)->DeleteGlobalRef((jobject) native_inotify_getPipeRead);
    (e)->DeleteGlobalRef((jobject) native_inotify_getFileDescriptor);
    (e)->DeleteGlobalRef((jobject) native_inotify_eventHandler);
    (e)->DeleteGlobalRef((jobject) inotify_event_init_III_V);
    (e)->DeleteGlobalRef((jobject) inotify_event_init_III_Ljava_lang_String);
}

/*
 * Function: Java_com_den_14_inotify_1java_NativeInotify_init
 *
 *     Creates a new Inotify object, returning its file descriptor or -1 on
 *     error.
 *
 * Returns:
 *     Inotify object file descriptor or -1 on error
 */
JNIEXPORT jint JNICALL Java_com_den_14_inotify_1java_NativeInotify_init(JNIEnv *e, jobject j) {
    /* On success, a new file descriptor is returned. */
    int fd = inotify_init();
    /* On error, -1 is returned, and errno is set. */

    if (fd < 0) {
        debug("inotify_init() failed (" << errno << "): " << strerror(errno));
        switch (errno) {
        case EMFILE:
            (e)->ThrowNew(user_instance_limit_exception, strerror(errno));
            break;
        case ENFILE:
            (e)->ThrowNew(system_limit_exception, strerror(errno));
            break;
        case ENOMEM:
            (e)->ThrowNew(insufficient_kernel_memory_exception, strerror(errno));
            break;
        default:
            (e)->ThrowNew(insufficient_kernel_memory_exception, strerror(errno));
            break;
        }
        return -1;
    }

    debug("inotify fd: " << fd);

    int pipe_fds[2];
    if ((pipe(pipe_fds)) < 0) {
        debug("pipe() failed (" << errno << "): " << strerror(errno));
        (e)->ThrowNew(inotify_exception, strerror(errno));
        return -1;
    }

    debug("pipe read/write fds: " << pipe_fds[0] << '/' << pipe_fds[1]);

    // pipe_fds[0] is the read end, pipe_fds[1], the write end
    (e)->CallVoidMethod(j, native_inotify_setPipes, pipe_fds[0], pipe_fds[1]);

    return fd;
}

/*
 * Function: Java_com_den_14_inotify_1java_NativeInotify_close
 *
 *     Closes the inotify instance.
 */
JNIEXPORT void JNICALL Java_com_den_14_inotify_1java_NativeInotify_close(JNIEnv *e, jobject j, jint fd) {
    jint pw = (e)->CallIntMethod(j, native_inotify_getPipeWrite);
    /* Close the write end of the pipe. */
    int ret = close(pw);
    if (ret < 0) {
        char *error = strerror(errno);
        (e)->ThrowNew(inotify_exception, error);
        debug("close(" << pw << ") failed (" << errno << "): " << strerror(errno));
        return;
    }
    debug("closed pipe write end");
}

/*
 * Function: Java_com_den_14_inotify_1java_NativeInotify_add_1watch
 *
 *     Adds the specified mask/path to the Inotify object associated with the
 *     specified file descriptor.
 *
 * Parameters:
 *     fd  - Inotify object file descriptor
 *     path - path to watch
 *     mask - bitmask controlling what to watch
 *
 * Returns:
 *     The watch descriptor or -1 on error
 */
JNIEXPORT jint JNICALL Java_com_den_14_inotify_1java_NativeInotify_add_1watch(
        JNIEnv *e, jobject j, jint fd, jstring path, jint mask) {
    const char *path_chars = (e)->GetStringUTFChars(path, NULL);
    int ret = inotify_add_watch(fd, path_chars, mask);
    (e)->ReleaseStringUTFChars(path, path_chars);
    if (ret < 0) {
        debug("inotify_add_watch() failed (" << errno << "): " << strerror(errno));
        switch (errno) {
        case ENOSPC:
            (e)->ThrowNew(user_watch_limit_exception, strerror(errno));
            break;
        default:
            (e)->ThrowNew(inotify_exception, strerror(errno));
            break;
        }
        return -1;
    }
    debug("inotify wd: " << ret);
    return ret;
}

/*
 * Function: Java_com_den_14_inotify_1java_NativeInotify_rm_1watch
 *     Removes the watch associated with the specified watch descriptor.
 *
 * Parameters:
 *     fd - Inotify object file descriptor
 *     wd - watch descriptor
 *
 * Returns:
 *     Returns 0 or -1 on error
 */
JNIEXPORT jint JNICALL Java_com_den_14_inotify_1java_NativeInotify_rm_1watch(
        JNIEnv *e, jobject j, jint fd, jint wd) {
    int ret = inotify_rm_watch(fd, wd);
    if (ret < 0) {
        debug("inotify_rm_watch() failed (" << errno << "): " << strerror(errno));
        (e)->ThrowNew(inotify_exception, strerror(errno));
        return -1;
    }
    return ret;
}

/*
 * Function: Java_com_den_14_inotify_1java_NativeInotify_read
 *      Services the queue, returning only on errors or instructed by the Java
 *      object.
 *
 * Parameters:
 *     fd - inotify object file descriptor
 */
JNIEXPORT void JNICALL Java_com_den_14_inotify_1java_NativeInotify_read(
        JNIEnv *e, jobject j, jint fd) {
    jint in_fd = (e)->CallIntMethod(j, native_inotify_getFileDescriptor);
    jint pip_fd = (e)->CallIntMethod(j, native_inotify_getPipeRead);
    jthrowable thrwbl;
    char *buf = NULL;
    int nfds;

    // XXX any argument > 0 to epoll_create
    int epfd = epoll_create(1);

    if (epfd == -1) {
        debug("epoll_create() failed (" << errno << "): " << strerror(errno));
        (e)->ThrowNew(inotify_exception, strerror(errno));
        goto END_READ;
    }

    struct epoll_event epev_in, epev_pip;
    struct epoll_event epev;

    epev_in.events = EPOLLIN;
    epev_in.data.fd = in_fd;
    if (epoll_ctl(epfd, EPOLL_CTL_ADD, in_fd, &epev_in) == -1) {
        debug("epoll_ctl() failed (" << errno << "): " << strerror(errno));
        (e)->ThrowNew(inotify_exception, strerror(errno));
        goto END_READ;
    }

    epev_pip.events = EPOLLIN;
    epev_pip.data.fd = pip_fd;
    if (epoll_ctl(epfd, EPOLL_CTL_ADD, pip_fd, &epev_pip) == -1) {
        debug("epoll_ctl() failed (" << errno << "): " << strerror(errno));
        (e)->ThrowNew(inotify_exception, strerror(errno));
        goto END_READ;
    }

    while (true) {
        // Wait indefinitely for an event
        nfds = epoll_wait(epfd, &epev, 1, -1);

        if (nfds == -1) {
            debug("epoll_wait() failed (" << errno << "): " << strerror(errno));
            (e)->ThrowNew(inotify_exception, strerror(errno));
            goto END_READ;
        }

        if (epev.data.fd == pip_fd) {
            debug("close invoked, returning from read");
            goto END_READ;
        }

        int nbytes;
        int ctlval = ioctl(in_fd, FIONREAD, &nbytes);
        if (ctlval < 0) {
            debug("ioctl() failed (" << errno << "): " << strerror(errno));
            (e)->ThrowNew(inotify_exception, strerror(errno));
            goto END_READ;
        }

        buf = (char *) malloc(nbytes);
        if (!buf) {
            fprintf(stderr, "buffer allocation (%d bytes) failed", nbytes);
            fprintf(stderr, "(returning from read due to allocation failure)");
            goto END_READ;
        }

        memset(buf, 0, nbytes);

        int rval = read(in_fd, buf, nbytes);
        if (rval < 0) {
            debug("read() failed (" << errno << "): " << strerror(errno));
            (e)->ThrowNew(inotify_exception, strerror(errno));
            goto END_READ;
        }

        int offset = 0;
        while (rval != 0) {
            /* Take an event out of the buffer. */
            INOTIFY_EVENT *ev = (INOTIFY_EVENT *) &buf[offset];
            jobject inotifyEvent;
            int evSize, sSize = sizeof(INOTIFY_EVENT);

            /* How big was the event? */
            evSize = sSize;

            if (ev->len != 0) {
                jstring fname = (e)->NewStringUTF(ev->name);
                if (!fname) {
                    thrwbl = (e)->ExceptionOccurred();
                    debug("NewStringUTF failed, returning from read");
                    goto EXCEPTION_OCCURRED;
                }

                inotifyEvent = (e)->NewObject(inotify_event,
                               inotify_event_init_III_Ljava_lang_String,
                               ev->wd, ev->mask, ev->cookie, fname);
                (e)->DeleteLocalRef(fname);
                evSize += ev->len;
            } else {
                inotifyEvent = (e)->NewObject(inotify_event,
                               inotify_event_init_III_V,
                               ev->wd, ev->mask, ev->cookie);
            }

            if (!inotifyEvent) {
                thrwbl = (e)->ExceptionOccurred();
                debug("NewObject failed, returning from read");
                goto EXCEPTION_OCCURRED;
            }

            /* Decrement the number of available bytes. */
            rval -= evSize;

            /* Send the event off to the Java listener. */
            (e)->CallVoidMethod(j, native_inotify_eventHandler, inotifyEvent);
            if ((e)->ExceptionCheck()) {
                debug("exception in eventHandler, returning from read");
                thrwbl = (e)->ExceptionOccurred();
                goto EXCEPTION_OCCURRED;
            }

            /*
             * Delete remaining local references so the native code doesn't hang
             * on to objects.
             */
            (e)->DeleteLocalRef(inotifyEvent);

            /* Calculate the location of the next event. */
            offset += evSize;
        }
        free(buf);
    }

EXCEPTION_OCCURRED:
    debug(__func__ << ": exception occurred" << endl);
    if (thrwbl) {
        (e)->ExceptionDescribe();
        (e)->ExceptionClear();
        (e)->DeleteLocalRef(thrwbl);
    }

END_READ:
    if (buf) free(buf);
    close(pip_fd);
    close(in_fd);
    return;
}


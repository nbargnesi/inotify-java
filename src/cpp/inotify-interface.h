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
 * File: inotify-interface.h
 * Project: inotify-java
 * Author: Nick Bargnesi
 */

#ifndef INOTIFY_INTERFACE_H
#define INOTIFY_INTERFACE_H

#include <iostream>

#include <linux/jni_md.h>
#include <jni.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/inotify.h>
#include <sys/ioctl.h>
#include <sys/errno.h>

#include "jni-header.h"
#include "typedefs.h"

#ifdef ENABLE_DEBUGGING
#include <assert.h>
#endif

using std::cout;
using std::cerr;
using std::endl;

/* Macro definitions */
#define max(x,y) ((x) > (y) ? (x) : (y))
#ifdef ENABLE_DEBUGGING
#define DEBUG(x) cout << __FILE__ << ':' << __LINE__ << ": " << x << endl;
#endif
#ifdef ENABLE_DEBUGGING
#define debug(x) DEBUG(x)
#else
#define debug(x)
#endif

/*
 * Variable: *jvm_ptr
 *     Global pointer to the Java(TM) virtual machine.
 */
JavaVM *jvm_ptr;

/*
 * Variable: user_instance_limit_exception
 *     Global reference to com.den_4.inotify_java.exceptions.UserInstanceLimitException
 */
jclass user_instance_limit_exception;

/*
 * Variable: user_watch_limit_exception
 *     Global reference to com.den_4.inotify_java.exceptions.UserWatchLimitException
 */
jclass user_watch_limit_exception;

/*
 * Variable: system_limit_exception
 *     Global reference to com.den_4.inotify_java.exceptions.SystemLimitException
 */
jclass system_limit_exception;

/*
 * Variable: insufficient_kernel_memory_exception
 *     Global reference to com.den_4.inotify_java.exceptions.InsufficientKernelMemoryException
 */
jclass insufficient_kernel_memory_exception;

/*
 * Variable: inotify_exception
 *     Global reference to com.den_4.inotify_java.exceptions.InotifyException
 */
jclass inotify_exception;

/*
 * Variable: native_inotify
 *     Global reference to com.den_4.inotify_java.NativeInotify
 */
jclass native_inotify;

/*
 * Variable: inotify_event
 *     Global reference to com.den_4.inotify_java.InotifyEvent
 */
jclass inotify_event;

/*
 * Variable: native_inotify_setPipes
 *     Global reference to com.den_4.inotify_java.NativeInotify.setPipes
 */
jmethodID native_inotify_setPipes;

/*
 * Variable: native_inotify_getPipeWrite
 *     Global reference to com.den_4.inotify_java.NativeInotify.getPipeWrite
 */
jmethodID native_inotify_getPipeWrite;

/*
 * Variable: native_inotify_getPipeRead
 *     Global reference to com.den_4.inotify_java.NativeInotify.getPipeRead
 */
jmethodID native_inotify_getPipeRead;

/*
 * Variable: native_inotify_getFileDescriptor
 *     Global reference to com.den_4.inotify_java.NativeInotify.getFileDescriptor
 */
jmethodID native_inotify_getFileDescriptor;

/*
 * Variable: native_inotify_eventHandler
 *     Global reference to com.den_4.inotify_java.NativeInotify.eventHandler
 */
jmethodID native_inotify_eventHandler;

/*
 * Variable: inotify_event_init_III_V
 *     Global reference to com.den_4.inotify_java.InotifyEvent.<init>(III)
 */
jmethodID inotify_event_init_III_V;

/*
 * Variable: inotify_event_init_III_Ljava_lang_String
 *     Global reference to com.den_4.inotify_java.InotifyEvent.<init>(IIILjava.lang.String)
 */
jmethodID inotify_event_init_III_Ljava_lang_String;

/*
 * Function: JNI_OnLoad
 *     The VM calls JNI_OnLoad when the native library is loaded.
 *
 * Returns:
 *     The constant: JNI_VERSION_1_6 - specified by jni.h
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *);

/*
 * Function: JNI_OnUnload
 *     The VM calls JNI_OnUnload when the class loader containing the native
 *     library is garbage collected.
 */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *, void *);

#endif

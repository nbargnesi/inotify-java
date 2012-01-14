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
 * File: typedefs.h
 * Project: inotify-java
 * Author: Nick Bargnesi
 */

#ifndef TYPEDEFS_H
#define TYPEDEFS_H

#include <sys/inotify.h>

/*
 * Typedef: MASK
 *     Unsigned 32-bit integer type definition: uint32_t
 */
typedef uint32_t MASK;

/*
 * Typedef: WATCH_DESCRIPTOR
 *     Unsigned 32-bit integer type definition: uint32_t
 */
typedef uint32_t WATCH_DESCRIPTOR;

/*
 * Typedef: INOTIFY_EVENT
 *     Struct type definition: struct inotify_event
 */
typedef struct inotify_event INOTIFY_EVENT;

#endif

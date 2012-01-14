# Version information
MAJOR_VERSION = 2
MINOR_VERSION = 1
PATCH_VERSION = 0

# Shared object variable definitions
SO_PREFIX = lib@PACKAGE_NAME@
SO_SUFFIX = so.$(MAJOR_VERSION).$(MINOR_VERSION).$(PATCH_VERSION)
SO_FILE = $(SO_PREFIX).$(SO_SUFFIX)
SO_NAME = $(SO_PREFIX).so.$(MAJOR_VERSION)
LD_SYMLINK = $(SO_PREFIX).so

# Compiling/linking flags.
INCLUDES = -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
CXXFLAGS = -Wall -shared -fPIC -lc $(INCLUDES)
LDFLAGS = -Wl,--as-needed,-soname,$(SO_NAME)

JAVA_H = jni-header.h
JAVA_DIR = ../java/classes
JAVA_H_SOURCE = $(JAVA_DIR)/com/den_4/inotify_java/NativeInotify.java
JAVA_H_CLASS = com.den_4.inotify_java.NativeInotify

BUILT_SOURCES = $(JAVA_H) $(SO_FILE) symlink

$(SO_FILE): typedefs.h \
	    inotify-interface.h \
	    inotify-interface.cpp
	    $(CXX) $(CXXFLAGS) @DEBUG@ @DEFINES@ -o $@ $(LDFLAGS) inotify-interface.cpp

symlink:
	@if [ ! -L '$(SO_NAME)' ]; then \
	    ln -sv $(SO_FILE) $(SO_NAME); \
	fi
	@if [ ! -L '$(LD_SYMLINK)' ]; then \
	    ln -sv $(SO_NAME) $(LD_SYMLINK); \
	fi

install-exec-local: symlink
	mkdir -p @libdir@
	@INSTALL@ --mode=755 $(SO_FILE) @libdir@
	cp -P $(SO_NAME) $(LD_SYMLINK) @libdir@

$(JAVA_H): $(JAVA_H_SOURCE)
	@JAVAH@ -classpath $(JAVA_DIR) -o $(JAVA_H) $(JAVA_H_CLASS)

clean-local:
	-rm $(JAVA_H) $(LD_SYMLINK) $(SO_FILE) $(SO_NAME)


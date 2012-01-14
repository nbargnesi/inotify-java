pkgconfigdir = $(libdir)/pkgconfig
nodist_pkgconfig_DATA = @PACKAGE_NAME@-2.1.pc

EXTRA_DIST = inotify-java-2.1.pc.in \
			 helpers \
			 lib \
			 specs

# Language-specific source location
CPP_SOURCE = src/cpp
JAVA_SOURCE = src/java

SUBDIRS = src

mc: maintainer-clean

clean-local:
	-rm inotify-java*tar*

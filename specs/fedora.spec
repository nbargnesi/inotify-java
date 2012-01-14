# The summary tag is used to define a one-line description of the packaged
# software. Unlike %description, summary is restricted to one line. RPM uses it
# when a succinct description of the package is needed.
Summary: Monitoring file system events in Java
Vendor: Den 4 Software

# The name tag is used to define the name of the software being packaged. In
# most (if not all) cases, the name used for a package should be identical in
# spelling and case to the software being packaged. The name cannot contain
# any whitespace: If it does, RPM will only use the first part of the name (up
# to the first space).
Name: inotify-java

# The version tag defines the version of the software being packaged.
Version: 2.1.0

# The copyright tag is used to define the copyright terms applicable to the
# software being packaged. In many cases, this might be nothing more than "GPL",
# for software distributed under the terms of the GNU General Public License, or
# something similar.
License: LGPLv3

# The release tag can be thought of as the package's version. The release is
# traditionally an integer — for example, when a specific piece of software at
# a particular version is first packaged, the release should be "1". If it is
# necessary to repackage that software at the same version, the release should
# be incremented. When a new version of the software becomes available, the
# release should drop back to "1" when it is first packaged.
Release: 2

# The group tag is used to group packages together by the types of functionality
# they provide. The group specification looks like a path and is similar in
# function, in that it specifies more general groupings before more detailed
# ones.
Group: Development/Libraries/Java

# The url tag is used to define a Uniform Resource Locator that can be used to
# obtain additional information about the packaged software. At present, RPM
# doesn't actively make use of this tag. The data is stored in the package
# however, and will be written into RPM's database when the package is
# installed.
URL: https://bitbucket.org/nbargnesi/inotify-java

# The packager tag is used to hold the name and contact information for the
# person or persons who built the package. Normally, this would be the person
# that actually built the package, or in a larger organization, a public
# relations contact. In either case, contact information such as an e-mail
# address or phone number should be included, so customers can send either money
# or hate mail, depending on their satisfaction with the packaged software.
Packager: Nick Bargnesi <nick@den-4.com>

# The prefix tag is used when a relocatable package is to be built. A
# relocatable package can be installed normally or can be installed in a
# user-specified directory, by using RPM's --prefix install-time option. The
# data specified after the prefix tag should be the part of the package's path
# that may be changed during installation.
Prefix: /usr

# The source tag is central to nearly every spec file. Although it has only one
# piece of data associated with it, it actually performs two functions:
#
# 1. It shows where the software's developer has made the original sources
#    available.
# 2. It gives RPM the name of the original source file.
#
# While there is no hard and fast rule, for the first function, it's generally
# considered best to put this information in the form of a Uniform Resource
# Locator (URL). The URL should point directly to the source file itself. This
# is due to the source tag's second function.
#
# As mentioned above, the source tag also needs to direct RPM to the source file
# on the build system. The source filename must be at the end of the line as the
# final element in a path.
Source: https://bitbucket.org/nbargnesi/inotify-java/get/%name-%version.tar.bz2 

# Indicates that the package can be built (installed into and packaged from) a
# user-definable directory. This helps package building by normal users.
#
# Simply use:
# Buildroot: <dir>
#
# in your spec file. The actual buildroot used by RPM during the build will be
# available to you (and your prep, build, and install sections) as the
# environment variable RPM_BUILD_ROOT.
BuildRoot: %{_tmppath}/%{name}-%{version}-build
BuildRequires: gcc-c++
BuildRequires: java-devel >= 1:1.7.0
BuildRequires: ant
BuildRequires: jpackage-utils

# The %description tag is used to provide an in-depth description of the
# packaged software. The description should be several sentences describing,
# to an uninformed user, what the software does.
#
# The %description tag is a bit different than the other tags in the preamble.
# For one, it starts with a percent sign. The other difference is that the data
# specified by the %description tag can span more than one line. In addition,
# a primitive formatting capability exists. If a line starts with a space, that
# line will be displayed verbatim by RPM. Lines that do not start with a space
# are assumed to be part of a paragraph and will be formatted by RPM. It's even
# possible to mix and match formatted and unformatted lines.
%description
The inotify-java library provides bindings and an API for monitoring the
filesystem on Linux platforms. The library uses the inotify interface provided
by glibc (versions 2.4 and up) and the Linux kernel (from 2.6.13 on).

%files
/%_prefix/share/inotify-java
/%_prefix/share/inotify-java/lib
/%_prefix/share/inotify-java/lib/inotify-java-2.1.0.jar
/%_prefix/%_lib/libinotify-java.so
/%_prefix/%_lib/libinotify-java.so.2
/%_prefix/%_lib/libinotify-java.so.2.1.0
/%_prefix/%_lib/pkgconfig/inotify-java-2.1.pc

%prep
%setup -q

%build
JAVA_HOME=%java_home ./configure --prefix=$RPM_BUILD_ROOT/%_prefix --libdir=$RPM_BUILD_ROOT/%_prefix/%_lib 
make
sed -i "s@$RPM_BUILD_ROOT@@g" inotify-java-2.1.pc
sed -i "s@//@/@g" inotify-java-2.1.pc

%install
mkdir -p $RPM_BUILD_ROOT/%_prefix/%_lib
make install
mkdir -p $RPM_BUILD_ROOT/%_prefix/%_lib

%clean
rm -fr  $RPM_BUILD_ROOT

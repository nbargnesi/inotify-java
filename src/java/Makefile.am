JAVAC_ARGS = -g -Xlint:-serial
JAR_DIST = @PACKAGE_NAME@-@VERSION@.jar

all-local: $(CLASSES)
	@@ANT@ -quiet -Dversion=@VERSION@

clean-local: clean-local-check
.PHONY: clean-local-check
clean-local-check:
	@@ANT@ clean
	-find -name *.class -exec rm {} \;
	-rm *.jar

uninstall-local:
	-rm $(pkgdatadir)/lib/$(JAR_DIST)

install-data-local: install-data-local-check
.PHONY: install-data-local-check
install-data-local-check:
	-mkdir -p $(pkgdatadir)/lib
	@INSTALL@ --mode=644 $(JAR_DIST) $(pkgdatadir)/lib


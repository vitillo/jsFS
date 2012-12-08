KEYSTORE = pKeyStore
KEYNAME = self
APPLET = JsFsApplet
JAR = $(APPLET).jar
SRCDIR = applet
SRC = $(SRCDIR)/$(APPLET).java
CLASS = $(SRCDIR)/$(APPLET)*.class

all: $(JAR)

$(KEYSTORE): 
	keytool -genkey -keystore pKeyStore -alias self
	keytool -selfcert -keystore pKeyStore -alias self

$(JAR): $(KEYSTORE) $(SRC)
	javac $(SRC)
	jar -cf $(JAR) $(CLASS)
	jarsigner -keystore $(KEYSTORE) $(JAR) $(KEYNAME)

.PHONY: clean
clean:
	rm -rf $(CLASS) $(JAR)

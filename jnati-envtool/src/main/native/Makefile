#
# Makefile for EnvTool
#

ARTEFACT := envtool
VERSION := 0.1

JAVA_CLASS := net.sf.jnati.envtool.EnvTool
OBJECT_FILES = EnvTool.o


# Check JAVA_HOME configured -- required to run javah, and possibly maven

ifndef JAVA_HOME
f := $(error JAVA_HOME is not defined)
endif

JAVAH = "$(JAVA_HOME)/bin/javah"
# f := $(error Cannot find javah. Ensure JAVA_HOME is correctly set.)



# Detect OS/Architecture
LIB_OS := $(shell mvn -q -f ../../../pom.xml exec:java -Dexec.mainClass=net.sf.jnati.SystemTool -Dexec.args=os)
LIB_ARCH := $(shell mvn -q -f ../../../pom.xml exec:java -Dexec.mainClass=net.sf.jnati.SystemTool -Dexec.args=arch)

# Include OS specific configuration
# Platform specific files must set:
#   LIB_PREFIX
#   LIB_SUFFIX
ifeq ($(LIB_OS),WINDOWS)
include Makefile.windows
else
ifeq ($(LIB_OS),LINUX)
include Makefile.linux
else
ifeq ($(LIB_OS),MAC)
include Makefile.mac
else
f := $(error Platform $(LIB_OS) not supported)
endif
endif
endif



LIB_OS_ARCH = $(LIB_OS)-$(LIB_ARCH)
LIB_OSVERSION = $(VERSION)-$(LIB_OS_ARCH)

TARGET_PATH = ../../../target/native
TARGET_FILE = $(LIB_PREFIX)$(ARTEFACT)-$(VERSION)$(LIB_SUFFIX)
TARGET = $(TARGET_PATH)/$(TARGET_FILE)

JAR_WORKDIR = $(TARGET_PATH)/jar
JAR = $(TARGET_PATH)/$(ARTEFACT)-$(LIB_OSVERSION).jar

OBJDIR = $(TARGET_PATH)/o/$(LIB_OS_ARCH)
OBJECTS = $(addprefix $(OBJDIR)/,$(OBJECT_FILES))

JAVA_CLASSPATH = ../../../target/classes
JAVA_SOURCE = ../java/$(subst .,/,$(JAVA_CLASS)).java
JAVA_CLASS_FILE = $(subst .,/,$(JAVA_CLASS)).class

JNI_HEADERS = $(subst .,_,$(JAVA_CLASS)).h

JAVAH_FLAGS += -classpath $(JAVA_CLASSPATH)
JAVAH_CMD = $(JAVAH) $(JAVAH_FLAGS) $(OUTPUT_OPTION)

RESOURCE_PATH = ../resources
ARTIFACT_PATH = META-INF/$(ARTEFACT)/$(VERSION)/$(LIB_OS_ARCH)

LDFLAGS += -s
CXXFLAGS += -I"${JAVA_HOME}/include" -I$(SRCDIR)
CFLAGS += -Os

SRCDIR = .




all: copies
.PHONY: all

.PHONY: clean
clean:
	$(RM) $(TARGET) $(OBJECTS) $(JNI_HEADER)
	$(RM) -r $(OBJDIR)

.PHONY: jar
jar: buildresource
	-@ jar -cMf $(JAR) -C $(JAR_WORKDIR) META-INF

.PHONY: buildresource
buildresource: build
	mkdir -p $(JAR_WORKDIR)/$(ARTIFACT_PATH)
	-@ echo "<manifest id=\"$(ARTEFACT)\" version=\"$(VERSION)\" platform=\"$(LIB_OS)\" architecture=\"$(LIB_ARCH)\">" > $(JAR_WORKDIR)/$(ARTIFACT_PATH)/MANIFEST.xml
	-@ echo "  <file library=\"true\">$(TARGET_FILE)</file>" >> $(JAR_WORKDIR)/$(ARTIFACT_PATH)/MANIFEST.xml
	-@ echo "</manifest>" >> $(JAR_WORKDIR)/$(ARTIFACT_PATH)/MANIFEST.xml
	-@ cp $(TARGET) $(JAR_WORKDIR)/$(ARTIFACT_PATH)/$(TARGET_FILE)

.PHONY: copies
copies: resourcecopy buildcopy

.PHONY: resourcecopy 
resourcecopy: buildresource
	-@ echo Copying to $(RESOURCE_PATH)
	-@ cp -r $(JAR_WORKDIR)/META-INF $(RESOURCE_PATH)

.PHONY: buildcopy
buildcopy: buildresource
	-@ echo Copying to $(JAVA_CLASSPATH)
	-@ cp -r $(JAR_WORKDIR)/META-INF $(JAVA_CLASSPATH)

.PHONY: build
build: nativedirs $(TARGET)

.PHONY: nativedirs
nativedirs:
	mkdir -p $(TARGET_PATH)
	mkdir -p $(OBJDIR)

$(TARGET): $(OBJECTS)
	$(LINK.cc) $^ -o $@

$(OBJDIR)/%.o: $(SRCDIR)/%.cc $(JNI_HEADERS)
	$(COMPILE.cc) $< -o $@

$(JNI_HEADERS): $(JAVA_SOURCE) $(JAVA_CLASSPATH)/$(JAVA_CLASS_FILE)
	$(JAVAH_CMD) -o $@ $(JAVA_CLASS)

$(JAVA_CLASSPATH)/$(JAVA_CLASS_FILE):
	mvn -f ../../../pom.xml compile

.PHONY: debug
debug:
	-@ echo "OS: $(LIB_OS)"
	-@ echo "ARCH: $(LIB_ARCH)"

.PHONY: archives
VERSIONS = $(shell ls ../resources/META-INF/$(ARTEFACT)/$(VERSION) )
archives: $(addprefix $(ARTEFACT)-$(VERSION)-, $(addsuffix .jar, $(VERSIONS)))

$(ARTEFACT)-$(VERSION)-%.jar:
	$(RM) -r $(TARGET_PATH)/jartemp
	mkdir -p $(TARGET_PATH)/jartemp/META-INF/$(ARTEFACT)/$(VERSION)/$*
	cp ../resources/META-INF/$(ARTEFACT)/$(VERSION)/$*/* $(TARGET_PATH)/jartemp/META-INF/$(ARTEFACT)/$(VERSION)/$*
	jar -cMf ../../../$(ARTEFACT)-$(VERSION)-$*.jar -C $(TARGET_PATH)/jartemp META-INF

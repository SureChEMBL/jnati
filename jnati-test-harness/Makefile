
# Current native code version
LIB_NAME = jnati_test
VERSION = 1.01


## Check Java is configured

ifndef JAVA_HOME
f := $(error JAVA_HOME is not defined, ensure that it points to a JDK)
endif

ifeq (, $(wildcard $(JAVA_HOME)/bin/javah))
f := $(error javah not found, check that JAVA_HOME is pointing to a JDK)
endif

JAVAH = "$(JAVA_HOME)/bin/javah"


## Configure variables

LIB_VERSION = $(VERSION)-$(PLATFORM)

SRC_DIR = src/test/native/c
OUT_DIR = target/native/$(LIB_VERSION)

OBJ_DIR = $(OUT_DIR)/o

TARGET_FILE = $(LIB_PREFIX)$(LIB_NAME)-$(LIB_VERSION)$(LIB_SUFFIX)
TARGET = $(OUT_DIR)/$(TARGET_FILE)

JAVA_SRC_DIR = src/test/java
JAVA_CLASSPATH = target/test-classes

JAVA_CLASS = net.sf.jnati.testharness.NativeWrapper
JAVA_SOURCE = $(JAVA_SRC_DIR)/$(subst .,/,$(JAVA_CLASS)).java
JAVA_CLASS_FILE = $(subst .,/,$(JAVA_CLASS)).class

JAVAH_FLAGS += -classpath $(JAVA_CLASSPATH)
JAVAH_CMD = $(JAVAH) $(JAVAH_FLAGS) $(OUTPUT_OPTION)

RESOURCE_PATH = src/test/resources
ARTEFACT_PATH = META-INF/$(LIB_NAME)/$(VERSION)/$(PLATFORM)

OBJECTS =	$(OBJ_DIR)/nativewrapper.o

JNI_HEADER_DIR = target/generated-sources
JNI_HEADERS = $(JNI_HEADER_DIR)/net_sf_jnati_testharness_NativeWrapper.h

MANIFEST_FILE = $(OUT_DIR)/MANIFEST.XML

## Define default target

.PHONY: all
all: info copies

.PHONY: info
info:
	-@ echo "JAVA_HOME: $(JAVA_HOME)"
	-@ echo "TARGET: $(TARGET)"

.PHONY: clean
clean:
	$(RM) $(TARGET) $(OBJECTS) $(JNI_HEADERS)
	$(RM) -r $(OBJ_DIR)


## Include OS specific configuration (TARGET must be configured first)

# PLATFORM environment variable must be set
ifndef PLATFORM
f := $(error PLATFORM is not defined)
endif

ifneq (,$(findstring WINDOWS, $(PLATFORM)))
include src/test/native/Makefile.windows
else
ifneq (,$(findstring LINUX, $(PLATFORM)))
 include src/test/native/Makefile.linux
else
 ifneq (,$(findstring MAC, $(PLATFORM)))
  include src/test/native/Makefile.mac
 else
  ifneq (,$(findstring FREEBSD, $(PLATFORM)))
   include src/test/native/Makefile.freebsd
  else
   ifeq ($(PLATFORM),SUNOS-X86)
    include src/test/native/Makefile.solaris.x86
   else
    ifeq ($(PLATFORM),SUNOS-AMD64)
     include src/test/native/Makefile.solaris.amd64
    else
     f := $(error Platform '$(PLATFORM)' not supported)
    endif
   endif
  endif
 endif
endif
endif

ifneq (,$(findstring WINDOWS-AMD64, $(PLATFORM)))
 include src/test/native/Makefile.vs
else
 include src/test/native/Makefile.gcc
endif



## Define targets


$(MANIFEST_FILE):
	-@ echo "<manifest id=\"$(LIB_NAME)\" version=\"$(VERSION)\" platform=\"$(PLATFORM)\" >" > $(MANIFEST_FILE)
	-@ echo "  <file library=\"true\">$(TARGET_FILE)</file>" >> $(MANIFEST_FILE)
	-@ echo "</manifest>" >> $(MANIFEST_FILE)

.PHONY: copies
copies: buildcopy

.PHONY: buildcopy
buildcopy: build
	-@ echo Copying to $(JAVA_CLASSPATH)
	-@ mkdir -p $(JAVA_CLASSPATH)/$(ARTEFACT_PATH)
	-@ cp -r $(TARGET) $(JAVA_CLASSPATH)/$(ARTEFACT_PATH)
	-@ cp -r $(MANIFEST_FILE) $(JAVA_CLASSPATH)/$(ARTEFACT_PATH)

.PHONY: build
build: nativedirs $(TARGET) $(MANIFEST_FILE)

.PHONY: nativedirs
nativedirs:
	mkdir -p $(OUT_DIR)
	mkdir -p $(OBJ_DIR)

$(JNI_HEADERS): $(JAVA_SOURCE) $(JAVA_CLASSPATH)/$(JAVA_CLASS_FILE) $(JNI_HEADER_DIR)
	$(JAVAH_CMD) -o $@ $(JAVA_CLASS)

$(JNI_HEADER_DIR):
	mkdir -p $(JNI_HEADER_DIR)

$(JAVA_CLASSPATH)/$(JAVA_CLASS_FILE):
	mvn -f pom.xml test-compile


$(TARGET): $(OBJECTS)
	link.exe /DLL /OUT:$@ $^

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c $(JNI_HEADERS)
	cl.exe /c /Ox /I"${JAVA_HOME}/include" /I"${JAVA_HOME}/include/win32" /I$(JNI_HEADER_DIR)  /Fo$@ $<

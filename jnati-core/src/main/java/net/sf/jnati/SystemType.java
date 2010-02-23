package net.sf.jnati;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author sea36
 */
public class SystemType {

	private static final Map<String,SystemType> MAP = new HashMap<String,SystemType>();

	public static final SystemType WINDOWS_X86 = get("WINDOWS-X86");
	public static final SystemType WINDOWS_X86_64 = get("WINDOWS-X86_64");

	public static final SystemType LINUX_X86 = get("LINUX-X86");
	public static final SystemType LINUX_X86_64 = get("LINUX-X86_64");
	public static final SystemType LINUX_AMD64 = get("LINUX-AMD64");

	public static final SystemType MAC_X86 = get("MAC-X86");
	public static final SystemType MAX_PPC = get("MAC-PPC");

    public static final SystemType UNKNOWN = get("UNKNOWN");
    

	private final String name;

	/**
	 * Private constructor.
	 */
	private SystemType(String name) {
		this.name = name;
	}


	public boolean isWindows() {
		return name.startsWith("WINDOWS-");
	}

	public boolean isLinux() {
		return name.startsWith("LINUX-");
	}

	public boolean isMac() {
		return name.startsWith("MAC-");
	}

    public boolean isUnknown() {
        return UNKNOWN.equals(this);
    }

	public String getName() {
		return name;
	}


	@Override
	public int hashCode() {
		return 37*name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SystemType) {
			return name.equals(((SystemType)obj).name);
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public static synchronized SystemType get(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Null argument: name");
		}
		SystemType type = MAP.get(name);
		if (type == null) {
			type = new SystemType(name);
			MAP.put(name, type);
		}
		return type;
	}


}

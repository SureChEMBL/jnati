/*
 * Copyright 2008 Sam Adams <sea36 at users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA
 * or see <http://www.gnu.org/licenses/>.
 */
package net.sf.jnati;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * <p><b>SystemTool detects the computer's operating system and architecture.</b></p>
 * <p>Platform and architecture definitions are read from definition files:
 * <i>META-INF/jnati/platform.defs</i> and <i>META-INF/jnati/architecture.defs</i>,
 * respectively. In these files lines starting '#' and empty lines are ignored.
 * Other lines consist of two fields separated by whitespace: the first field
 * contains the architecture name, and the second a regular expression to be
 * matched against the 'os.name' or 'os.arch' system property. Patterns are case
 * insensitive, and must match the entire value. The first match made is
 * returned, so more specific patterns should be placed before more lax ones.</p>
 * 
 * @author Sam Adams &lt;sea36@users.sourceforge.net&gt;
 */
public class SystemTool {
	
	private static final Logger LOG = Logger.getLogger(SystemTool.class);

	private static final String ARCHITECTURE_DEFS = "META-INF/jnati/architecture.defs";
	private static final String PLATFORM_DEFS = "META-INF/jnati/platform.defs";
	
	private static String platform;
	private static String architecture;
	
	private static boolean init = false;
	
	private static void init() {
		if (!init) {
			String osname = System.getProperty("os.name", "");
			String osarch = System.getProperty("os.arch", "");
			
			LOG.info("Detecting platform/architecture");

			// Detect platform
			LOG.debug("Itentifying platform (os.name=" + osname + ")");
			try {
				platform = searchDefinitionFiles(osname, PLATFORM_DEFS);
			} catch (IOException e) {
				throw new RuntimeException("Error detecting platform", e);
			}
			LOG.info("Platform: " + platform);
			
			// Detect architecture
			LOG.debug("Itentifying architecture (os.arch=" + osarch + ")");
			try {
				architecture = searchDefinitionFiles(osarch, ARCHITECTURE_DEFS);
			} catch (IOException e) {
				throw new RuntimeException("Error detecting architecture", e);
			}
			LOG.info("Architecture: " + architecture);
			
			init = true;
		}
	}
	
	
	private static String searchDefinitionFiles(String name, String filepath) throws IOException {
		
		// Locate definition files
		Enumeration<URL> e = SystemTool.class.getClassLoader().getResources(filepath);
		if (!e.hasMoreElements()) {
			throw new IOException("Definition file missing: " + filepath);
		}
		
		// Iterate through files
		for (URL u : Collections.list(e)) {
			LOG.debug("Reading definitions file: " + u);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(u.openStream()));
			try {
				int n = 0;
				for (String l = in.readLine(); l != null; l = in.readLine()) {
					n++;
					l = l.trim();
					if (l.length() == 0 || l.startsWith("#")) {
						continue;
					}
					String[] s = l.split("\\s+");
					if (s.length != 2) {
						LOG.warn("Ignoring bad line (" + n + "): " + l);
						continue;
					}
					LOG.trace("Testing pattern: " + s[1] + " (" + s[0] + ")");
					Pattern p = Pattern.compile(s[1], Pattern.CASE_INSENSITIVE);
					if (p.matcher(name).matches()) {
						return s[0];
					}
				}
				
			} finally {
				in.close();
			}
		}
		
		return null;
	}

	/**
	 * Returns the name of the detected platform (operating system), or null if
	 * the platform is not recognised.
	 * @return
	 * @throws 	IOException
	 * 			if an error occurs while reading the definition files
	 */
	public static String getPlatform() throws IOException {
        init();
		return platform;
    }

    /**
     * Returns the name of the detected architecture, or null if the
     * architecture is not recognised.
     * @return
     * @throws	IOException
     * 			if an error occurs while reading the definition files
     */
    public static String getArchitecture() {
    	init();
    	return architecture;
    }

    
    public static void main(String[] args) throws Exception {
    	int arg = 0;
    	if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
	    	ConsoleAppender a = new ConsoleAppender();
	    	a.setWriter(new OutputStreamWriter(System.err));
	    	a.setLayout(new SimpleLayout());
	    	LOG.addAppender(a);
    	}
    	if (args.length > 0 && "-v".equals(args[0])) {
    		LOG.setLevel(Level.DEBUG);
    		arg++;
    	} else if (args.length > 0 && "-vv".equals(args[0])) {
    		LOG.setLevel(Level.TRACE);
    		arg++;
    	} else {
    		LOG.setLevel(Level.WARN);
    	}
    	if (args.length > arg) {
    		String c = args[args.length-1];
    		if ("os".equals(c)) {
				System.out.println(getPlatform());
    		} else if ("arch".equals(c)) {
    			System.out.println(getArchitecture());
    		} else {
    			System.out.println("Unknown command: " + c);
    		}
    	} else {
	    	String osarch = getPlatform() + "-" + getArchitecture();
	    	System.out.println(osarch);
    	}
	}
	
}

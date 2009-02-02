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
package net.sf.jnati.envtool;

import net.sf.jnati.NativeCodeException;

import org.junit.Assert;
import org.junit.Test;


public class EnvToolTest {

	private static final String PATH = "PATH";
	private static final String TEST_PATH = "/test/path/";
	
	@Test
	public void testEnvTool() {
		
		try {
			EnvTool et = EnvTool.getInstance();
			
			String oldpath = et.getEnvVar(PATH);
			Assert.assertNotSame("Old path matches test path", TEST_PATH, oldpath);
			et.setEnvVar(PATH, TEST_PATH);
			String newpath = et.getEnvVar(PATH);
			Assert.assertEquals("Path not set", TEST_PATH, newpath);
			
		} catch (NativeCodeException e) {
			Assert.fail("Failed to load native code: " + e.getMessage());
		}
		
	}
}

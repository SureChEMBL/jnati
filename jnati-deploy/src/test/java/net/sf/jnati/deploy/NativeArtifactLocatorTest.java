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
package net.sf.jnati.deploy;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import net.sf.jnati.ArtefactDescriptor;
import net.sf.jnati.FileUtils;
import net.sf.jnati.NativeCodeException;

import org.junit.Test;


public class NativeArtifactLocatorTest {

	// This test is turned off for Debian build,
	// have no idea why it doesn't work;
	// but anyway it's not a good idea for such test as it
	// tries to fetch data from internet
	//@Test
	public void testDownload() throws NativeCodeException, IOException {
		
		File tmpdir = new File("tmpdir-testDownload").getAbsoluteFile();
		tmpdir.mkdir();
		
		try {
			System.setProperty("jnati.localRepository", tmpdir.getPath());
			System.setProperty("test-download.1.0.osarch", "NOOS-NOARCH");
			System.setProperty("test-download.1.0.repositoryUrls", "http://jnati.sourceforge.net/jnati-testrepo");
			ArtefactDescriptor loc = NativeArtefactLocator.findArtefact("test-download", "1.0");
			
			File dir = new File(tmpdir, "test-download/1.0/NOOS-NOARCH");
			assertEquals(dir, loc.getPath().getAbsoluteFile());
			assertTrue(new File(dir, "testfile.txt").exists());
		} finally {
			FileUtils.delTree(tmpdir);
		}
		
	}
	
	
	@Test
	public void testLocateInRepository() throws NativeCodeException, IOException {
		
		File tmpdir = new File("tmpdir-testLocate").getAbsoluteFile();
		tmpdir.mkdir();
		
		try {
			System.setProperty("jnati.localRepository", tmpdir.getAbsolutePath());
			System.setProperty("test-locate.1.0.osarch", "NOOS-NOARCH");
			System.setProperty("test-locate.1.0.repositoryUrls", "false");
			ArtefactDescriptor loc = NativeArtefactLocator.findArtefact("test-locate", "1.0");
			
			// Fixed path here for Debian build
			File target = new File("tmpdir-testLocate/test-locate/1.0/NOOS-NOARCH").getAbsoluteFile();
			assertEquals(target, loc.getPath().getAbsoluteFile());
			assertTrue(new File(target, "testfile.txt").exists());
		} finally {
			FileUtils.delTree(tmpdir);
		}
		
	}
	
	
	@Test
	public void testDeploy() throws NativeCodeException, IOException {
		File tmpdir = new File("tmpdir-testDeploy").getAbsoluteFile();
		tmpdir.mkdir();
		
		try {
			System.setProperty("jnati.localRepository", tmpdir.getPath());
			System.setProperty("test-deploy.1.0.osarch", "NOOS-NOARCH");
			System.setProperty("jnati.allowDownload", "false");
			System.setProperty("jnati.allowDirectLoad", "false");
			ArtefactDescriptor loc = NativeArtefactLocator.findArtefact("test-deploy", "1.0");
			
			File dir = new File(tmpdir, "test-deploy/1.0/NOOS-NOARCH");
			assertEquals(dir, loc.getPath().getAbsoluteFile());
			assertTrue(new File(dir, "testfile.txt").exists());
		} finally {
			FileUtils.delTree(tmpdir);
		}
	}
	
	
}

/*
 * Copyright 2008-2011 Sam Adams <sea36 at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jnati.deploy;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import net.sf.jnati.ArtefactDescriptor;
import net.sf.jnati.FileUtils;
import net.sf.jnati.NativeCodeException;

import org.junit.Test;

/**
 * @author Sam Adams
 */
public class NativeArtifactLocatorTest {

	@Test
	public void testDownload() throws NativeCodeException, IOException {
		
		File tmpdir = new File("tmpdir-testDownload").getAbsoluteFile();
		tmpdir.mkdir();
		
		try {
            Properties config = new Properties();
			config.setProperty("jnati.localRepository", tmpdir.getPath());
			config.setProperty("jnati.osarch.test-download.1.0", "NOOS-NOARCH");
			config.setProperty("jnati.repositoryUrls.test-download.1.0", "http://jnati.sourceforge.net/jnati-testrepo");
			ArtefactDescriptor loc = NativeArtefactLocator.findArtefact("test-download", "1.0", config);
			
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
            Properties config = new Properties();
			config.setProperty("jnati.localRepository", tmpdir.getAbsolutePath());
			config.setProperty("jnati.osarch.test-locate.1.0", "NOOS-NOARCH");
			config.setProperty("jnati.repositoryUrls.test-locate.1.0", "false");
			ArtefactDescriptor loc = NativeArtefactLocator.findArtefact("test-locate", "1.0", config);
			
			File target = new File("target/test-classes/META-INF/test-locate/1.0/NOOS-NOARCH").getAbsoluteFile();
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
            Properties config = new Properties();
			config.setProperty("jnati.localRepository", tmpdir.getPath());
			config.setProperty("jnati.osarch.test-deploy.1.0", "NOOS-NOARCH");
			config.setProperty("jnati.allowDownload", "false");
			config.setProperty("jnati.allowDirectLoad", "false");
			ArtefactDescriptor loc = NativeArtefactLocator.findArtefact("test-deploy", "1.0", config);
			
			File dir = new File(tmpdir, "test-deploy/1.0/NOOS-NOARCH");
			assertEquals(dir, loc.getPath().getAbsoluteFile());
			assertTrue(new File(dir, "testfile.txt").exists());
		} finally {
			FileUtils.delTree(tmpdir);
		}
	}
	
	
}

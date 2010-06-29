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
package net.sf.jnati.deploy.source;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.artefact.ManifestReader;

public abstract class ArtefactSource {
	
	private static final String MANIFEST_FILE = "MANIFEST.xml";
	
	public abstract InputStream openFile(String path) throws IOException;
	
	public abstract void close() throws IOException;

	public abstract boolean containsFile(String path) throws IOException;

	public void loadManifest(Artefact artefact) throws IOException {
		ManifestReader in = new ManifestReader();
		InputStream is = new BufferedInputStream(openFile(MANIFEST_FILE));
		in.read(is, artefact);
	}
	
	public abstract boolean isLocal();

	public abstract File getPath();
	
}

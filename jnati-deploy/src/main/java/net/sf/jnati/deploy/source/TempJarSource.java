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

import java.io.File;
import java.io.IOException;

import net.sf.jnati.deploy.artefact.Artefact;

/**
 * @author Sam Adams
 */
public class TempJarSource extends JarSource {

	private File file;
	
	public TempJarSource(File file, Artefact artefact) throws IOException {
		super(file, artefact);
		this.file = file;
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		if (!file.delete()) {
			throw new IOException("Failed to delete temp file: " + file);
		}
	}

}

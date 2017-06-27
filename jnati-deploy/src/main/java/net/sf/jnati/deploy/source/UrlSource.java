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
package net.sf.jnati.deploy.source;

import net.sf.jnati.deploy.artefact.Artefact;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Sam Adams
 */
public class UrlSource extends ArtefactSource {

    private static final Logger LOG = Logger.getLogger(UrlSource.class);

    private final URL manifestUrl;
    private final String rootPath;
    private final Artefact artefact;

    public UrlSource(URL url, Artefact artefact, String artefactPath) {
        this.manifestUrl = url;
        this.rootPath = artefactPath;
        this.artefact = artefact;
    }

    @Override
    public InputStream openFile(String path) throws IOException {
        URL u = findUrl(path);
        if (u == null) {
            throw new FileNotFoundException("File not found: "+path);
        }        
        return u.openStream();
    }

    private URL findUrl(String path) {
        String file = rootPath+path;
        URL u = Thread.currentThread().getContextClassLoader().getResource(file);
        if (u != null) {
            LOG.debug("found '"+file+"' : "+u);
        }
        return u;
    }

    @Override
    public void close() throws IOException {
        ;
    }

    @Override
    public boolean containsFile(String path) throws IOException {
        URL u = findUrl(path);
        return u != null;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public File getPath() {
        return null;
    }
    
}

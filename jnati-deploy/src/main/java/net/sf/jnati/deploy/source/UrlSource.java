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

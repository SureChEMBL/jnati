package net.sf.jnati.testharness;

import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.LibraryLoader;
import net.sf.jnati.deploy.NativeLibraryLoader;

import java.util.Properties;

/**
 * @author Sam Adams
 */
public class NativeWrapper implements Wrapper, LibraryLoader {

    public void loadLibrary(String file) {
        System.load(file);
    }

    public void init(boolean redeploy) throws NativeCodeException {
        Properties config = new Properties();
        config.setProperty("jnati.localRepository", "target/repo");
        config.setProperty("jnati.allowDirectLoad", "false");
        config.setProperty("jnati.enableMultideploy.jnati_test", redeploy ? "true":"false");

        NativeLibraryLoader.loadLibrary("jnati_test", "1.01", config, this);
    }

    public native int getAnswer();

}

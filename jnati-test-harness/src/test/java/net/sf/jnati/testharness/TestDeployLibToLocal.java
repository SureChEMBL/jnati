package net.sf.jnati.testharness;

import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.CustomClassLoader;
import net.sf.jnati.deploy.NativeLibraryLoader;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Sam Adams
 */
public class TestDeployLibToLocal extends AbstractTest {

    @Test
    public void testDeployLibToLocalRepo() throws NativeCodeException {
        Properties config = new Properties();
        config.setProperty("jnati.localRepository", "target/repo");
        config.setProperty("jnati.allowDirectLoad", "false");

        NativeLibraryLoader.loadLibrary("jnati_test", "1.01", config);
        NativeWrapper wrapper = new NativeWrapper();
        assertEquals(42, wrapper.getAnswer());
    }

}

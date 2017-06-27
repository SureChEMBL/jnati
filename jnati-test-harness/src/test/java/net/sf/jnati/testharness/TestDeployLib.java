package net.sf.jnati.testharness;

import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.CustomClassLoader;
import net.sf.jnati.deploy.NativeLibraryLoader;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Sam Adams
 */
public class TestDeployLib extends AbstractTest {

    @Test
    public void testDeployLib() throws NativeCodeException {
        NativeLibraryLoader.loadLibrary("jnati_test", "1.01");
        NativeWrapper wrapper = new NativeWrapper();
        assertEquals(42, wrapper.getAnswer());
    }

}

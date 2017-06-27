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
public class TestMultiClassloader extends AbstractTest {

    @Test
    public void testMultipleClassloaders() throws Exception {
        Wrapper wrapper1 = load(false);
        assertEquals(42, wrapper1.getAnswer());
        Wrapper wrapper2;
        try {
            wrapper2 = load(false);
            fail("Should fail to load");
        } catch (InvocationTargetException e) {
            // Get wrapped exception
            Throwable cause1 = e.getCause();
            assertEquals(UnsatisfiedLinkError.class, cause1.getClass());
            String message = cause1.getMessage();
            assertTrue("Message: "+message, message.endsWith("already loaded in another classloader"));
        }
    }

}

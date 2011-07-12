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
public class TestMultiDeploy extends AbstractTest {

    @Test
    public void testMultiDeploy() throws Exception {
        Wrapper wrapper1 = load(true);
        assertEquals(42, wrapper1.getAnswer());
        Wrapper wrapper2 = load(true);
        assertEquals(42, wrapper2.getAnswer());
    }

}

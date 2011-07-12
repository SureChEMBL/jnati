package net.sf.jnati.testharness;

import net.sf.jnati.deploy.CustomClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Sam Adams
 */
public abstract class AbstractTest {

    protected static Wrapper load(boolean redeploy) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        CustomClassLoader loader = new CustomClassLoader(TestDeployLib.class.getClassLoader());
        Class c = loader.findClass("net.sf.jnati.testharness.NativeWrapper");
        Object o = c.newInstance();

        Method m = c.getMethod("init", boolean.class);
        m.invoke(o, redeploy);

        return (Wrapper) o;
    }

}

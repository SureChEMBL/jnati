package net.sf.jnati.deploy;

import java.io.*;

public class CustomClassLoader extends ClassLoader {

    public CustomClassLoader() {
        super(CustomClassLoader.class.getClassLoader());
    }

    public CustomClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class findClass(String name) {
        String file = name.replace('.', File.separatorChar) + ".class";
        try {
            byte[] b = loadClassData(file);
            Class c = defineClass(name, b, 0, b.length);
            resolveClass(c);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] loadClassData(String name) throws IOException {
        InputStream in = getResourceAsStream(name);
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] b = new byte[4096];
            for (int n = in.read(b); n != -1; n = in.read(b)) {
                buffer.write(b, 0, n);
            }
            return buffer.toByteArray();
        } finally {
            in.close();
        }
    }

}

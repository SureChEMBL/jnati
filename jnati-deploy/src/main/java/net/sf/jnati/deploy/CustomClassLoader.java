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
package net.sf.jnati.deploy;

import java.io.*;

/**
 * @author Sam Adams
 */
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

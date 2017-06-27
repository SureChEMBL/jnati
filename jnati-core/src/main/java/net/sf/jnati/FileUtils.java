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
package net.sf.jnati;

import java.io.*;

/**
 * @author Sam Adams
 */
public class FileUtils {

    private static final int BUFFER_SIZE = 4096;

	/**
     * Creates a temporary directory in the default system location.
     * @return
     * @throws IOException
     */
    public static File getTmpDir() throws IOException {
        return getTmpDir((File) null);
    }

    /**
     * Creates a temporary directory in the specified location, or the default
     * system location if the specified location is set to null.
     * @param path
     * @return
     * @throws IOException
     */
    public static File getTmpDir(String path) throws IOException {
        return getTmpDir(path == null ? null : new File(path));
    }

    /**
     * Creates a temporary directory in the specified location, or the default
     * system location if the specified location is set to null.
     * @param path
     * @return
     * @throws IOException
     */
    public static File getTmpDir(File root) throws IOException {
        // Create temporary file with prefix j_tmp, no suffix
        File tmpdir = File.createTempFile("j_tmp", "", root);

        // Delete file, and create directory with same name
        tmpdir.delete();
        tmpdir.mkdir();

        return tmpdir;
    }

    /**
     * Deletes a directory and its contents.
     * @param dir
     * @return  True on success, false on failure.
     */
    public static boolean delTree(File dir) {
        // Delete files in directory
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                delTree(f);
            } else {
                f.delete();
            }
        }
        dir.delete();

        return !dir.exists();
    }


    /**
     * Writes the contents of an input stream to a file. If the file already
     * exists, it will be overwritten.
     *
     * @param in    Input stream providing file contents
     * @param file  File to write to
     * @throws IOException
     */
    public static void writeStreamToFile(InputStream in, File file) throws IOException {        
        OutputStream out = new FileOutputStream(file);
        copyStreamToStream(in, out);	// closes stream
    }
    
    /**
     * Copys the entire contents of an InputStream to an OutputStream.
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copyStreamToStream(InputStream in, OutputStream out) throws IOException {
    	try {
    		try {
	        	byte[] b = new byte[BUFFER_SIZE];
	        	for (int n = in.read(b); n >= 0; n = in.read(b)) {
	                out.write(b, 0, n);
	            }
	        } finally {
	        	out.close();
	        }
    	} finally {
    		in.close();
    	}
    }

    /**
     * Reads the entire contents of an InputStream into an array of bytes.
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(final InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyStreamToStream(in, out);
        return out.toByteArray();
    }

    /**
     * Reads entire contents of reader into a string.
     * @param in
     * @return
     * @throws IOException
     */
    public static String readString(final Reader in) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int n;

        StringBuilder sb = new StringBuilder();
        try {
            while ((n = in.read(buffer)) > -1) {
                sb.append(buffer, 0, n);
            }
        } finally {
            in.close();
        }

        return sb.toString();
    }

    /**
     * Writes the contents of a string to a file. If the file already exists
     * it is over-written.
     * @param file
     * @param string
     * @throws IOException
     */
    public static void writeString(final File file, String string) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(string.getBytes());
        } finally {
            fos.close();
        }
    }

}

/*
 * Copyright 2008 Sam Adams <sea36 at users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA
 * or see <http://www.gnu.org/licenses/>.
 */
package net.sf.jnati.proc;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;


public class StreamGobblerTest {

	
	@Test
	public void testStreamGobblerCache() throws InterruptedException {
		ByteArrayInputStream in1 = new ByteArrayInputStream("foo-bar-baz".getBytes());
		ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		StreamGobbler sg1 = new StreamGobbler(in1, out1);
		sg1.setCacheSize(3);
		sg1.start();
		sg1.join();
		assertEquals("foo-bar-baz", out1.toString());
		assertArrayEquals("baz".getBytes(), sg1.getCachedBytes());
	}
	
	@Test
	public void testStreamGobbler() {
		
		String data = "test data... 1, 2, 3... done";
		
		ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		StreamGobbler sg = new StreamGobbler(in, out);
		sg.start();
		try {
			sg.join(1000);
		} catch (InterruptedException e) {
			fail("interrupted");
		}
		if (sg.isAlive()) {
			fail("StreamGobbler still alive");
		}
		
		assertEquals(data, out.toString());
		
	}
	
	@Test
	public void testNullOutput() {
		String data = "test data... 1, 2, 3... done";
		
		ByteArrayInputStream in = new ByteArrayInputStream(data.getBytes());
		
		StreamGobbler sg = new StreamGobbler(in);
		sg.start();
		try {
			sg.join(1000);
		} catch (InterruptedException e) {
			fail("interrupted");
		}
		if (sg.isAlive()) {
			fail("StreamGobbler still alive");
		}
		
	}
	
	
	@Test
	public void test10MBData() {
		
		byte[] data = new byte[10 * 1024 * 1024];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i % 256);
		}
		
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		StreamGobbler sg = new StreamGobbler(in, out);
		sg.start();
		try {
			sg.join(5000);
		} catch (InterruptedException e) {
			fail("interrupted");
		}
		if (sg.isAlive()) {
			fail("StreamGobbler still alive");
		}
		assertTrue(Arrays.equals(data, out.toByteArray()));
		
	}
	
	@Test
	@Ignore
	public void testOutOfMemory() {
		
		InfiniteInputStream in = new InfiniteInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		StreamGobbler sg = new StreamGobbler(in, out);
		sg.start();
		try {
			sg.join();
		} catch (InterruptedException e) {
			fail("interrupted");
		}
		if (sg.isAlive()) {
			fail("StreamGobbler still alive");
		}
		
	}
	
	
	
	
	
	static class InfiniteInputStream extends InputStream {

		private static final byte[] bytes = new byte[8192];
		
		static {
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) (i % 256);
			}
		}
		
		Random rand = new Random();
		
		@Override
		public int read() throws IOException {
			return rand.nextInt(256);
		}
		
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int n = 1 + rand.nextInt(Math.min(len, bytes.length));
			System.arraycopy(bytes, 0, b, off, n);
			return n;
		}
	}
	
}

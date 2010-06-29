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

import java.io.*;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.jnati.proc.ProcessMonitor.ProcessState;

import org.junit.BeforeClass;
import org.junit.Test;


public class MonitoredProcessTest {
	
	private static String classpath;
	
	private ProcessMonitor getHelloWorldCommand() {
		String[] command = new String[] {
				"java",
				"-cp",
				classpath,
				"HelloWorld"
		};
		
		ProcessMonitor c = new ProcessMonitor(command);
		return c;
	}
	
	private ProcessMonitor getSleepCommand() {
		String[] command = new String[] {
				"java",
				"-cp",
				classpath,
				"Sleep"
		};
		
		ProcessMonitor c = new ProcessMonitor(command);
		return c;
	}

    private ProcessMonitor getEchoCommand() {
        String[] command = new String[] {
				"java",
				"-cp",
				classpath,
				"Echo"
		};

		ProcessMonitor c = new ProcessMonitor(command);
		return c;
    }
    
	@BeforeClass
	public static void getClasspath() {
		ClassLoader cl = MonitoredProcessTest.class.getClassLoader();
		URL u = cl.getResource(".");
		File f = new File(u.getPath());
		classpath = f.toString();
	}
	
	@Test
	public void testHelloWorld() throws ExecutionException, InterruptedException, TimeoutException {
		ProcessMonitor c = getHelloWorldCommand();
		ProcessOutput out = c.runProcess();
		assertEquals(0, out.getExitValue());
		assertEquals("Hello world!", out.getOutput());
		assertEquals("starting... done", out.getMessages());
	}
	
	@Test
	public void testCancel() throws InterruptedException, ExecutionException, TimeoutException {
		ProcessMonitor c = getSleepCommand();
		c.start();
		Thread.sleep(100);
		assertSame(ProcessState.RUNNING, c.getState());
		c.cancel();
		assertSame(ProcessState.CANCELLED, c.getState());
		try {
			c.getExitValue();
			fail();
		} catch (CancellationException e) {
			// pass
		}
	}
	
	@Test
	public void testTimeout() throws ExecutionException, InterruptedException {
		ProcessMonitor c = getSleepCommand();
		c.setTimeout(500, TimeUnit.MILLISECONDS);
		try {
			c.execute();
			fail();
		} catch (TimeoutException e) {
			// pass
		}
		assertEquals(ProcessState.TIMEDOUT, c.getState());
		try {
			c.getExitValue();
			fail();
		} catch (TimeoutException e) {
			// pass
		}
	}
	
	
}

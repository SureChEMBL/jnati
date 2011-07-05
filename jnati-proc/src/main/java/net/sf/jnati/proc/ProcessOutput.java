/*
 * Copyright 2008-2010 Sam Adams <sea36 at users.sourceforge.net>
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
package net.sf.jnati.proc;

/**
 * @author Sam Adams
 */
public class ProcessOutput {

	private byte[] output;
	private byte[] error;
	
	private int exitValue;
	
	public int getExitValue() {
		return exitValue;
	}
	
	public byte[] getStdOutBytes() {
		int len = output.length;
		byte[] temp = new byte[len];
		System.arraycopy(output, 0, temp, 0, len);
		return temp;
	}
	
	public byte[] getStdErrBytes() {
		int len = error.length;
		byte[] temp = new byte[len];
		System.arraycopy(error, 0, temp, 0, len);
		return temp;
	}
	
	public String getOutput() {
		return new String(output);
	}
	
	public String getMessages() {
		return new String(error);
	}
	
	void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
	
	void setError(byte[] error) {
		int len = error.length;
		this.error = new byte[len];
		System.arraycopy(error, 0, this.error, 0, len);
	}
	
	void setOutput(byte[] output) {
		int len = output.length;
		this.output = new byte[len];
		System.arraycopy(output, 0, this.output, 0, len);
	}
	
	
}

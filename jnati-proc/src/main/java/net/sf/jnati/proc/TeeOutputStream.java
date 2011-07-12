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
package net.sf.jnati.proc;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Sam Adams
 */
public class TeeOutputStream extends OutputStream {
	
	private final OutputStream[] streams;
	
	public TeeOutputStream(final OutputStream... streams) {
		this.streams = streams.clone();
	}

	@Override
	public void close() throws TeeStreamException {
		TeeStreamException ex = null;
		for (int i = 0; i < streams.length; i ++) {
			try {
				streams[i].close();
			} catch (IOException e) {
				if (ex == null) {
					ex = new TeeStreamException(e);
				} else {
					ex.addCause(e);
				}
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

	@Override
	public void flush() throws TeeStreamException {
		TeeStreamException ex = null;
		for (int i = 0; i < streams.length; i ++) {
			try {
				streams[i].flush();
			} catch (IOException e) {
				if (ex == null) {
					ex = new TeeStreamException(e);
				} else {
					ex.addCause(e);
				}
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws TeeStreamException {
		TeeStreamException ex = null;
		for (int i = 0; i < streams.length; i ++) {
			try {
				streams[i].write(b, off, len);
			} catch (IOException e) {
				if (ex == null) {
					ex = new TeeStreamException(e);
				} else {
					ex.addCause(e);
				}
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

	@Override
	public void write(byte[] b) throws TeeStreamException {
		TeeStreamException ex = null;
		for (int i = 0; i < streams.length; i ++) {
			try {
				streams[i].write(b);
			} catch (IOException e) {
				if (ex == null) {
					ex = new TeeStreamException(e);
				} else {
					ex.addCause(e);
				}
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

	@Override
	public void write(int b) throws TeeStreamException {
		TeeStreamException ex = null;
		for (int i = 0; i < streams.length; i ++) {
			try {
				streams[i].write(b);
			} catch (IOException e) {
				if (ex == null) {
					ex = new TeeStreamException(e);
				} else {
					ex.addCause(e);
				}
			}
		}
		if (ex != null) {
			throw ex;
		}
	}

}

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
package net.sf.jnati.deploy.resolver;

import net.sf.jnati.NativeCodeException;

/**
 * @author Sam Adams
 */
public class ArtefactNotFoundException extends NativeCodeException {

	private static final long serialVersionUID = 1L;

	public ArtefactNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArtefactNotFoundException(String message) {
		super(message);
	}

	public ArtefactNotFoundException(Throwable cause) {
		super(cause);
	}
	
}

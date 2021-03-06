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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sam Adams
 */
public class TeeStreamException extends IOException {

    private static final long serialVersionUID = 1L;

    private List<IOException> causes = new ArrayList<IOException>();

    public TeeStreamException(IOException ex) {
        super();
        initCause(ex);
        causes.add(ex);
    }

    public void addCause(IOException ex) {
    	if (causes.isEmpty()) {
    		initCause(ex);
    	}
        causes.add(ex);
    }

    public IOException[] getCauses() {
        return causes.toArray(new IOException[causes.size()]);
    }
    
}

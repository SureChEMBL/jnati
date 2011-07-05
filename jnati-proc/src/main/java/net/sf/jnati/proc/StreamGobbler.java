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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * Class that gobbles content from (optionally saving it) an input stream.
 * @author Sam Adams
 *
 */
public class StreamGobbler extends Thread {

    private static final Logger LOG = Logger.getLogger(StreamGobbler.class);

    private static final int BUFFER_SIZE = 8192;
    private static final int CACHE_SIZE = 1024;
    
    private InputStream source;
    private OutputStream sink;

    private byte[] buffer;
    private int cache = CACHE_SIZE;
    private int len = 0;
    
    /**
     * Records whether run() has been called.
     */
    private boolean started = false;;

    /**
     * Records any exception thrown during execution.
     */
    private Exception failCause;

    /**
     * Constructs StreamGobbler, and starts thread.
     * @param in
     * @param save
     * @param threadName
     */
    public StreamGobbler(InputStream in, OutputStream out) {
    	if (in == null) {
    		throw new NullPointerException("null InputStream");
    	}
        this.source = in;
        this.sink = out;

        // Thread will not prevent VM from exiting
        setDaemon(true);
    }
    
    public StreamGobbler(InputStream in) {
    	this(in, null);
    }
    
    public void setCacheSize(int n) {
    	if (n < 0) {
    		throw new IllegalArgumentException("Cache size must be positive");
    	}
    	synchronized (this) {
    		if (started) {
    			throw new IllegalStateException("StreamGobbler has started");
    		}
    		cache = n;
		}
    }

    /**
     * Reads content from input stream, until either the end of the stream is
     * reached, or the thread is interrupted.
     */
    public void run() {
    	
    	synchronized (this) {
	    	if (started) {
	    		throw new IllegalThreadStateException("Run method has already been called");
	    	}
	    	started = true;
    	}
    	
        LOG.trace("StreamGobbler starting");
        
        buffer = new byte[BUFFER_SIZE+cache];
        try {
        	
        	int n = 0;
        	while (n >= 0 && !isInterrupted()) {
        		synchronized(this) {
	        		n = source.read(buffer, len, buffer.length-len);
	        		if (LOG.isTraceEnabled()) {
	        			LOG.trace("read " + n);
        			}
	        		if (n == -1) {
	        			break;
	        		}
        			if (sink != null) {
    					synchronized (sink) {
    						try {
    							sink.write(buffer, len, n);
    						} catch (IOException e) {
    							LOG.error("Error writing to sink", e);
    							sink = null;
    							failCause = e;
    						}
        				}
					}
	        		if (cache > 0) {
	        			len += n;
	        			if (len >= cache) {
	        				// Reset buffer
		        			System.arraycopy(buffer, len-cache, buffer, 0, cache);
		        			len = cache;
	        			}
	        		}
        		}
        		
        		Thread.yield();		
        	}
        	
        } catch (IOException e) {
            LOG.error("Error reading input", e);
            failCause = e;
        }

        if (isInterrupted()) {
            LOG.info("Interrupted: stopping");
        }
        
        if (sink != null) {
        	try {
        		sink.flush();
        	} catch (IOException e) {
        		LOG.error("Error flushing sink", e);
        	}
        }
       
        LOG.trace("StreamGobbler finished");
    }

    /**
     * Returns true if the StreamGobbler has been started (even if it has now
     * finished), and otherwise false.
     * @return
     */
    public boolean isStarted() {
		return started;
    }
    
    public byte[] getCachedBytes() {
    	if (!started) {
    		throw new IllegalStateException("StreamGobbler not started");
    	}
    	synchronized (this) {
    		byte[] b = new byte[len];
    		System.arraycopy(buffer, 0, b, 0, len);
    		return b;
		}
    }
    
    public boolean isError() {
    	return failCause != null;
    }
    
    public Exception getError() {
    	return failCause;
    }

}

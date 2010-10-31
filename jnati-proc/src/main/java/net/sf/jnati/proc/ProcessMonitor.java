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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

/**
 * <p>This class is used to create and monitor the running of an operating
 * system process.</p>
 * 
 * <p>
 * 
 * Class for running a native process in a separate thread.
 * <pre>
 * ProcessMonitor proc = new ProcessMonitor("/usr/bin/babel", "-ismiles", "-ocml");
 * proc.setInput("CN(C)CCC1=CNC2=C1C=C(C=C2)CC3COC(=O)N3");
 * ByteArrayOutputStream out = new ByteArrayOutputStream();
 * proc.setOutputStream(out);
 * proc.setErrorStream(System.err);
 * proc.execsute();
 * </pre>
 * @author Sam Adams &lt;sea36 at users.sourceforge.net&gt;
 */
public class ProcessMonitor {

	private static final Logger LOG = Logger.getLogger(ProcessMonitor.class);

	private static final long STREAMGOBBLER_CLEANUP_TIMEOUT_MS = 500;

	private static final int DEFAULT_TIMEOUT_MS = 60000;
	

	public static enum ProcessState {
		/**
		 * Process has not been started.
		 */
		READY,
		
		/**
		 * Process is running.
		 */
		RUNNING,
		
		/**
		 * Process has successfully finished.
		 */
		FINISHED,
		
		/**
		 * Process was cancelled.
		 */
		CANCELLED,
		
		/**
		 * Process timed-out, and was terminated.
		 */
		TIMEDOUT,
		
		/**
		 * Process failed.
		 */
		FAILED
	}
	
	private ProcessBuilder builder;
	private Monitor runner;
    
    private long timeout = DEFAULT_TIMEOUT_MS;
    
    private InputStream input;
    private OutputStream stdout;
    private OutputStream stderr;
    
    private ProcessState state = ProcessState.READY;
    private Integer exitValue;
    private ExecutionException ex;
    

    /**
     * <p>Constructs a monitored process with the specified operating system
     * program and arguments.  It is not checked whether <code>command</code>
     * corresponds to a valid operating system command.</p>
     *
     * @param   command  A string array containing the program and its arguments
     * @throws  NullPointerException
     *          If the argument is <code>null</code>
     */
    public ProcessMonitor(String... command) {
    	if (command == null) {
    	    throw new NullPointerException();
    	}
        this.builder = new ProcessBuilder(command);
    }
    
    /**
     * <p>Constructs a monitored process with the specified operating system
     * program and arguments.  It is not checked whether <code>command</code>
     * corresponds to a valid operating system command.</p>
     *
     * @param   command  A list containing the program and its arguments
     * @throws  NullPointerException
     *          If the argument is <code>null</code>
     */
    public ProcessMonitor(List<String> command) {
    	if (command == null) {
    	    throw new NullPointerException();
    	}
    	// copy command so changes to list do not change process builder
    	List<String> copy = new ArrayList<String>(command);
        this.builder = new ProcessBuilder(copy);
    }
    
    /**
     * <p>Returns this monitored process's operating system program and
     * arguments.  The returned list is a copy. Subsequent updates to the list
     * will not be reflected in the state of this monitored process.</p>
     *
     * @return  This monitored process's program and its arguments
     * @see	java.lang.ProcessBuilder#command()
     */
    public List<String> getCommand() {
    	return new ArrayList<String>(builder.command());
    }
    
    /**
     * <p>Sets this monitored process's operating system program and arguments.
     * It is not checked whether <code>command</code> corresponds to a valid
     * operating system command.</p>
     *
     * @param   command  A string array containing the program and its arguments
     * @throws  NullPointerException
     *          If the argument is <code>null</code>
     * @throws  IllegalStateException
     * 			If this monitored process has been started
     * @see	java.lang.ProcessBuilder#command(String...)
     */
    public synchronized void setCommand(String... command) {
    	if (command == null) {
    	    throw new NullPointerException();
    	}
    	checkNotStarted();
    	builder.command(command);
    }
    
    /**
     * <p>Sets this monitored process's operating system program and arguments.
     * It is not checked whether <code>command</code> corresponds to a valid
     * operating system command.</p>
     *
     * @param   command  A list containing the program and its arguments
     * @throws  NullPointerException
     *          If the argument is <code>null</code>
     * @throws  IllegalStateException
     * 			If this monitored process has been started
     * @param command
     * @see	java.lang.ProcessBuilder#command(List)
     */
    public synchronized void setCommand(List<String> command) {
    	if (command == null) {
    	    throw new NullPointerException();
    	}
    	checkNotStarted();
    	// copy command so changes to list do not change process builder
    	List<String> copy = new ArrayList<String>(command);
        this.builder = new ProcessBuilder(copy);
    	builder.command(command);
    }
    
   
    public Map<String,String> getEnvironment() {
    	return new HashMap<String,String>(builder.environment());
    }
    
    public synchronized void setEnvironmentVariable(String key, String value) {
    	checkNotStarted();
    	builder.environment().put(key, value);
    }
    
    public synchronized void unsetEnvironmentVariable(String key) {
    	checkNotStarted();
    	builder.environment().remove(key);
    }
    
    /**
     * <p>Returns this monitored process's working directory. The returned value
     * may be <code>null</code> -- this means to use the working directory of
     * the current Java process, usually the directory named by the system
     * property <code>user.dir</code>, as the working directory.</p>
     * 
     * @return	This monitored process's working directory
     * @see java.lang.ProcessBuilder#directory()
     */
    public File getWorkingDirectory() {
    	return builder.directory();
    }

    
    
    
	/**
     * <p>Sets this process' working directory.  The argument may be null --
     * this means to use the working directory of the current Java process,
     * usually the directory named by the system property user.dir, as the
     * working directory of the child process.</p>
     * 
     * @param 	dir		The new working directory
	 * @throws	IllegalStateException
	 * 			If the process has been started
     * @see java.lang.ProcessBuilder#directory(File)
     */
    public synchronized void setWorkingDirectory(File dir) {
    	checkNotStarted();
    	builder.directory(dir);
    }

    /**
     * Sets input for process.
     * @param input
     * @throws NullPointerException  - if input is null.
     * @throws IllegalStateException - if process has been started, or input
     * is already set.
     */
    public void setInput(byte[] input) {
    	setInput(input == null ? null : new ByteArrayInputStream(input));
    }

    /**
     * Sets input for process. Makes call to setInput(final byte[] input), and
     * will pass down any exception which that method throws.
     * @param input
     * @throws NullPointerException  - if input is null.
     */
    public void setInput(String input) {
        setInput(input == null ? null : input.getBytes());
    }
    
    public synchronized void setInput(InputStream input) {
    	checkNotStarted();
        this.input = input;
    }
    
    
    /**
     * <p>Sets the OutputStream to which bytes written to the process' STDERR
     * will be directed.  If this is set to null, then the output of the
     * process will be discarded.</p>
     * 
     * @param stderr
     * @throws	IllegalStateException
     * 			If the process has been started
     */
    public synchronized void setErrorTarget(OutputStream stderr) {
    	checkNotStarted();
    	this.stderr = stderr;
	}
    
    /**
     * <p>Sets the OutputStream to which bytes written to the process' STDOUT
     * will be directed.  If this is set to null, then the output of the
     * process will be discarded.</p>
     * 
     * @param stdout
     * @throws	IllegalStateException
     * 			If the process has been started
     */
    public synchronized void setOutputTarget(OutputStream stdout) {
    	checkNotStarted();
    	this.stdout = stdout;
	}

    
    public synchronized void setRedirectErrorStream(boolean redirect) {
    	checkNotStarted();
    	builder.redirectErrorStream(redirect);
    }
    
    public boolean getRedirectErrorStream() {
    	return builder.redirectErrorStream();
    }
    
    /**
     * Sets timeout after which process will be stopped. A timeout of 0 means
     * wait forever.
     * @param timeout  - timeout in milliseconds.
     * @throws IllegalStateException - if thread has been started.
     */
    public synchronized void setTimeout(long timeout, TimeUnit timeoutUnit) {
    	checkNotStarted();
    	if (timeout < 0) {
    		throw new IllegalArgumentException("Negative time: " + timeout);
    	}
    	if (timeoutUnit == null) {
    		throw new IllegalArgumentException("Null TimeUnit");
    	}
        this.timeout = timeoutUnit.toMillis(timeout);
    }
    
    public long getTimeout(TimeUnit unit) {
		return unit.convert(timeout, TimeUnit.MILLISECONDS);
	}


    /**
     * 
     * @return
     * @throws	IllegalStateException
     * 			If process has not been started, or is still running
     * @throws  ExecutionException
     * 			If this process threw an exception
     * @throws 	TimeoutException
     * 			If this process timed out
     * @throws	CancellationException
     * 			If this process was cancelled
     */
    public Integer getExitValue() throws ExecutionException, TimeoutException {
    	switch (state) {
    	case READY:
    		throw new IllegalStateException("Not started");
    	case RUNNING:
    		throw new IllegalStateException("Running");
    	case TIMEDOUT:
    		throw new TimeoutException();
    	case CANCELLED:
    		throw new CancellationException();
    	case FAILED:
    		throw ex;
    	case FINISHED:
    		return exitValue;
    	default:
    		throw new RuntimeException("Unknown state: " + state);
    	}
	}
    

    /**
     * Runs the process, waits for it to complete, and returns its exit status.
     * @return
     * @throws InterruptedException 
     * @throws IllegalStateException  - If process already started
     * @throws InterruptedException     If this thread was interrupted before
     *                                  the process had finished.
     * @throws TimeoutException 
     * @throws ExecutionException       If the process failed. This exception
     *                                  contains the output up to the point of
     *                                  failure, and the cause of failure.
     */
    public int execute() throws ExecutionException, InterruptedException, TimeoutException {
    	start();
    	int result = waitFor();
    	return result;
    }
    
    
    /**
     * Runs the process, waits for it to complete, and returns its output. This
     * method captures the complete output (STDOUT/STDERR) from the process,
     * along with its exit value. If the process produces a very large output
     * then this may cause an OutOfMemoryError.
     * @return
     * @throws InterruptedException 
     * @throws IllegalStateException  - If process already started
     * @throws InterruptedException     If this thread was interrupted before
     *                                  the process had finished.
     * @throws TimeoutException 
     * @throws ExecutionException       If the process failed. This exception
     *                                  contains the output up to the point of
     *                                  failure, and the cause of failure.
     */
    public synchronized ProcessOutput runProcess() throws ExecutionException, InterruptedException, TimeoutException {
    	checkNotStarted();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	ByteArrayOutputStream err = new ByteArrayOutputStream();
    	if (this.stdout == null) {
    		setOutputTarget(out);
    	} else {
    		setOutputTarget(new TeeOutputStream(this.stdout, out));
    	}
    	if (this.stderr == null) {
    		setErrorTarget(err);
    	} else {
    		setErrorTarget(new TeeOutputStream(this.stderr, err));
    	}
    	
    	int exitValue = execute();
    	
    	ProcessOutput result = new ProcessOutput();
    	result.setExitValue(exitValue);
    	result.setOutput(out.toByteArray());
    	result.setError(err.toByteArray());
    	
    	return result;
    }
    
    /**
     * <p>Waits for the process to terminate, and returns the process' exit
     * value.</p>
     * 
     * @return	The process' exit value
     * @throws	IllegalStateException
     * 			If the process has not been started
     * @throws 	ExecutionException
     * 			If an error occurs while the process is executing
     * @throws	CancellationException
     * 			If the process is cancelled
     * @throws 	TimeoutException
     * 			If the process timed out
     * @throws 	InterruptedException
     * 			If this thread is interrupted while waiting for the process to
     * 			finish
     */
    public Integer waitFor() throws ExecutionException, TimeoutException, InterruptedException {
    	synchronized (this) {
    		if (state == ProcessState.READY) {
    			throw new IllegalStateException("Not run");
    		}
    	}
    	while (state == ProcessState.RUNNING) {
			runner.join();
			Thread.yield();
		}
    	synchronized (this) {
			if (state == ProcessState.CANCELLED) {
				throw new CancellationException();
			}
			if (state == ProcessState.TIMEDOUT) {
				throw new TimeoutException();
			}
			if (state == ProcessState.FAILED) {
				throw ex;
			}
    	}
    	return exitValue;
    }
    
    /**
     * <p>Starts the new process to be monitored by this object.</p>
     * 
     * @throws	IllegalStateException
     * 			If the process has been started.
     */
    public void start() {
    	
    	synchronized (this) {
    		checkNotStarted();
        	state = ProcessState.RUNNING;	
		}
    	
    	runner = new Monitor(builder);
      
    	// Run process
    	new Thread("exec") {
    		@Override
    		public void run() {
    			try {
    				if (LOG.isInfoEnabled()) {
    		    		LOG.info("Executing command: " + getCommand().get(0));
    		    	}
    				long t0 = System.currentTimeMillis();
    				runner.start();
    				runner.join(timeout);
    				long t1 = System.currentTimeMillis();
    				
    				synchronized (this) {
    					if (state == ProcessState.CANCELLED) {
    						throw new CancellationException();
    					}
    					if (runner.isAlive()) {
    						LOG.warn("Timed-out: cancelling command (" + (t1-t0) + "ms)");
    						state = ProcessState.TIMEDOUT;
    						runner.interrupt();
    						throw new TimeoutException();
    					}
    					if (runner.ex != null) {
    						state = ProcessState.FAILED;
    						ex = new ExecutionException(runner.ex);
    						throw ex;
    					}
    					
        				if (LOG.isInfoEnabled()) {
        					LOG.info("Command complete (" + (t1-t0) + "ms)");
        				}
        				exitValue = runner.exitValue;
    					state = ProcessState.FINISHED;
    				}
    				
    			} catch (Exception e) {
    				LOG.warn("Error", e);
    				// ignore
    			}
    		}
    	}.start();
    }
		
	
    /**
     * <p>Cancel the process monitored by this object.  If the process has
     * already terminated, nothing is done.</p>
     * 
     * @throws	IllegalStateException
     * 			If the process has not started
     */
    public void cancel() {
    	synchronized (this) {
    		if (state == ProcessState.READY) {
    			throw new IllegalStateException("Not started");
    		}
    		if (state == ProcessState.RUNNING) {
	    		LOG.info("Cancelling command");
	    		state = ProcessState.CANCELLED;
	    		runner.interrupt();
    		}
    	}
    }

    /**
     * <p>Returns an array containing the last bytes the process monitored by
     * this object has written to STDOUT.</p>
     * 
     * @return	The last bytes the process has written to STDOUT.
     * @throws	IllegalArgumentException
     * 			If the process has not started
     */
    public byte[] getCachedOutput() {
    	if (state == ProcessState.READY) {
    		throw new IllegalStateException("Not started");
    	}
    	return runner.gobbleIn.getCachedBytes();
    }
    
    /**
     * <p>Returns an array containing the last bytes the process monitored by
     * this object has written to STDERR.</p>
     * 
     * @return	The last bytes the process has written to STDERR.
     * @throws	IllegalArgumentException
     * 			If the process has not started
     */
    public byte[] getCachedError() {
    	if (state == ProcessState.READY) {
    		throw new IllegalStateException("Not started");
    	}
    	return runner.gobbleErr.getCachedBytes();
    }
    
    
    
    private void checkNotStarted() {
    	synchronized (this) {
	    	if (state != ProcessState.READY) {
	    		throw new IllegalStateException("Process started");
	    	}
    	}
    }
    
    /**
     * Returns the current state of this process.
     * @return
     */
    public ProcessState getState() {
		return state;
	}

    /**
     * Returns any exception while the process is running, or null if no
     * exception was thrown.
     * @return
     */
    public Throwable getFailCause() {
		return ex == null ? null : ex.getCause();
	}



    
    private class Monitor extends Thread {

    	/**
    	 * ProcessBuilder used to start process.
    	 */
    	private final ProcessBuilder processBuilder;
    	
    	
    	/**
    	 * STDOUT from process.
    	 */
    	private StreamGobbler gobbleIn;
    	
    	/**
    	 * STDERR from process.
    	 */
        private StreamGobbler gobbleErr;
        
        private Integer exitValue;
        private Exception ex;
        

        /**
         * Constructor.
         * @param builder
         */
        public Monitor(ProcessBuilder builder) {
        	super("pmon");
            this.processBuilder = builder;
        }

        @Override
        public void run() {
        	try {
       	
	        	// Start process
	    		LOG.debug("Starting process");
	    		Process process = processBuilder.start();
	    		
	            // Initialise stream gobblers
	    		LOG.debug("Staring StreamGobblers");
	            gobbleIn = new StreamGobbler(process.getInputStream(), stdout);
	            gobbleIn.setName("sg.in");
	            gobbleErr = new StreamGobbler(process.getErrorStream(), stderr);
	            gobbleErr.setName("sg.err");
	            
	            // Start threads
	            gobbleIn.start();
	            gobbleErr.start();
	            
	            try {
	            	// Wait for stream gobblers to start
	                LOG.debug("Waiting for StreamGobblers");
	                while (!(gobbleIn.isStarted() && gobbleErr.isStarted())) {
	                	Thread.yield();
	                }
	                
	                // Write input, and close stream
	                LOG.debug("Sending input");
	                OutputStream out = process.getOutputStream();
	                try {
		            	if (input != null) {
		            		try {
		            			byte[] b = new byte[8192];
			            		for (int n = input.read(b); n != -1; n = input.read(b)) {
			            			if (LOG.isTraceEnabled()) {
			            				LOG.trace("sent: " + n + " bytes");
			            			}
			            			out.write(b, 0, n);	            			
			            		}
		            		} finally {
		            			input.close();
		            		}
		            	}
	                } finally {
	                	out.close();
	                }
		
	                // Wait for process to finish
	                LOG.debug("Waiting for process");
	                process.waitFor();
		
	                // Wait for StreamGobblers to finish
	                LOG.debug("Process finished");
	                gobbleIn.join();
	                gobbleErr.join();
	                LOG.debug("StreamGobblers finished");
	                
	                int exitValue = process.exitValue();
	                LOG.trace("Exit value: " + exitValue);
	                this.exitValue = Integer.valueOf(exitValue);
	                
	            } catch (InterruptedException e) {
	            	LOG.info("Interrupted: destroying process");
	            	process.destroy();
	                throw e;
	        	} catch (Exception e) {
	                LOG.warn("Exception occured - destroying process", e);
	                process.destroy();
	                throw e;
	        	} finally {
	        		
	        		// Ensure process is finished
	        		process.destroy();
	        		
	            	// May need to tidy up StreamGobblers
		            if (gobbleIn.isAlive() || gobbleErr.isAlive()) {
		            	LOG.trace("Cleaning up StreamGobblers");
			            if (gobbleIn.isAlive()) {
			                gobbleIn.interrupt();
			            }
			            if (gobbleErr.isAlive()) {
			                gobbleErr.interrupt();
			            }
			            long t0 = System.currentTimeMillis() + STREAMGOBBLER_CLEANUP_TIMEOUT_MS;
			            while ((gobbleIn.isAlive() || gobbleErr.isAlive()) && System.currentTimeMillis() < t0) {
			            	Thread.yield();
			            }
			            
			            if (gobbleIn.isAlive() || gobbleErr.isAlive()) {
			            	LOG.error("StreamGobblers failed to terminate");
			            }
		            }
	            }
        	} catch (Exception e) {
        		ex = e;
        	}
    	}
   
         
    }
}

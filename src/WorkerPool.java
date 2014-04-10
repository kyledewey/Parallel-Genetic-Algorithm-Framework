/*
 * WorkerPool.java
 *
 * Version:
 *     $Id: WorkerPool.java,v 1.1 2010/05/15 17:46:40 kyledewey Exp $
 *
 * Revisions:
 *      $Log: WorkerPool.java,v $
 *      Revision 1.1  2010/05/15 17:46:40  kyledewey
 *      Initial revision
 *
 *      Revision 1.1  2009/11/03 04:24:11  kyle
 *      Initial revision
 *
 *
 */

import java.util.*;
import java.util.concurrent.*;

/**
 * A pool that jobs are submitted to.
 * It will then do the jobs in a queue fashion,
 * doing some number in parallel (equal to the
 * number of available processors).
 *
 * @author Kyle Dewey
 */
public class WorkerPool {
    // begin instance variables
    private Map< Runnable, Future > submittedJobs;
    private int numProcessors; // number of processors available
    private ExecutorService pool;
    // end instance variables

    /**
     * Creates a new worker pool.
     */
    public WorkerPool() {
        numProcessors = Runtime.getRuntime().availableProcessors();
        submittedJobs = new HashMap< Runnable, Future >();
        submittedJobs = Collections.synchronizedMap( submittedJobs );
        pool = Executors.newFixedThreadPool( numProcessors );
    }

    /**
     * Submits a job to the worker pool.
     *
     * @param job The job to add to the worker pool
     */
    public synchronized void addJob( final Runnable job ) {
        Future future;

        //future = pool.submit( job );
	future = pool.submit( new Runnable() {
		public int hashCode() {
		    return job.hashCode();
		}
		public void run() {
		    job.run();
		    submittedJobs.remove( job );
		}
	    } );
        submittedJobs.put( job, future );
    }

    /**
     * Causes the calling thread to wait for the
     * given job to complete
     *
     * @param job The job to wait for
     */
    public void waitForJob( Runnable job ) {
        if ( submittedJobs.containsKey( job ) ) {
            Future wait;

            wait = submittedJobs.get( job );
            try {
                wait.get();
            } catch( Exception e ) {
            }
        }
    }

    /**
     * Performs internal cleanup.  To be called at GA end.
     */
    public void cleanup() {
        pool.shutdownNow();
    }
}

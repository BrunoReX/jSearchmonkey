package com.embeddediq.searchmonkey;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelExecutor {

    private final ExecutorService myPool;
    private final LinkedBlockingDeque<Runnable> myTasksQueue = new LinkedBlockingDeque<>();
    private final AtomicBoolean myCancelToken = new AtomicBoolean( false);
    private final ConcurrentLinkedQueue<Exception> myExceptions = new ConcurrentLinkedQueue<>();
    private final AtomicInteger myTasksCount = new AtomicInteger( 0);
    private final AtomicInteger myTasksCompleteCount = new AtomicInteger( 0);

    public ParallelExecutor() {
        int threads = Runtime.getRuntime().availableProcessors();
        myPool = new ThreadPoolExecutor( threads, threads, 60L, TimeUnit.SECONDS, myTasksQueue );
    }

    public ParallelExecutor(int minPoolSize, int maxPoolSize, double coreScaling) {
        int threads = (int) Math.ceil(Runtime.getRuntime().availableProcessors() * coreScaling);
        threads = Math.min( threads, maxPoolSize );
        threads = Math.max( minPoolSize, threads );
        myPool = new ThreadPoolExecutor( threads, threads, 60L, TimeUnit.SECONDS, myTasksQueue );
    }

    public void execute( Callable<Void> task ) {

        if( isCancelled() ) {
            return;
        }

        if( myPool.isShutdown() || myPool.isTerminated() ){
            throw new IllegalStateException("Pool has been shutdown. No new tasks can be added");
        }

        myTasksCount.incrementAndGet();

        myPool.execute( () -> {

            if(isCancelled()){
                return;
            }

            try {
                task.call();
            }
            catch(Exception e){
                myExceptions.add( e );
                cancel();
                shutdown();
            }
            finally {
                if(myTasksCompleteCount.incrementAndGet() == getTasksCount() && isTasksQueueEmpty()){
                    shutdown();
                }
            }
        } );
    }

    public void waitAll(long timeout, TimeUnit timeoutTimeUnit) {

        try {
            if(!myPool.awaitTermination( timeout, timeoutTimeUnit )){
                myExceptions.add( new TimeoutException() );
            }
        } catch ( InterruptedException e ) {
            myExceptions.add( e );
        }

        if(myExceptions.size() > 0){
            throw new AggregateException(myExceptions);
        }

    }

    private int getTasksCount() {
        return myTasksCount.get();
    }

    private int getTasksCompleteCount() {
        return myTasksCompleteCount.get();
    }

    public int getTasksRemaining() {
        return getTasksCount() - getTasksCompleteCount();
    }

    public boolean isTasksQueueEmpty() {
        return myTasksQueue.size() == 0;
    }

    private void shutdown() {
        if ( myPool.isShutdown() ) {
            return;
        }
        myPool.shutdown();
    }

    public boolean isCancelled() {
        return myCancelToken.get();
    }

    public void cancel() {
        myTasksQueue.clear();
        myCancelToken.set( true );
    }
}

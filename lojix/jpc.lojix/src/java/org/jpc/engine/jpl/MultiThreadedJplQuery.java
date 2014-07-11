package org.jpc.engine.jpl;
 
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jpc.Jpc;
import org.jpc.query.QueryAdapter;
import org.jpc.query.Solution;
import org.jpc.term.Term;
import org.minitoolbox.concurrent.ExecutionResult;

import com.google.common.base.Optional;
 
/**
 * This class wraps a JPL query in such a way that it does not have the JPL limitations regarding multithreading support.
 * @author sergioc
 *
 */
//IMPLEMENTATION NOTES: This is still experimental. It crashes from time to time. Rather difficult to debug.
//The class basically overrides all the public methods in the ancestor classes and executes them (with a super call) in the context of an executor service (into a dedicated thread).
//It is important that the overridden methods in this class are not synchronized, otherwise it will cause a deadlock when executing the super (typically synchronized) methods.
public class MultiThreadedJplQuery extends QueryAdapter {
 
    /**
     * The executor below allows to provide a JPC query (encapsulating a JPL query) that does not have the constraint limiting certain cursor operations to happen in the same thread (as in JPL).
     * This is done executing relevant JPL operations in the context of a single threaded executor (an executor backed up with only one thread).
     * Although we gain on simplifying the usage contract of JPL Query objects (specially in heavy multi-threaded scenarios), 
     * a disadvantage of this approach is that it implies to maintain one separate thread per each JPC query wrapping a JPL query.
     * Although the thread is destroyed when the query is closed, this could imply a certain additional demand of resources (threads) that does not occur in plain JPL.
     * This implementation should be modified (i.e., the executor removed) if JPL eventually removes existing constraints regarding cursor operations occurring in the same/different thread.
     */
    private ExecutorService executor;
    
    public MultiThreadedJplQuery(JplEngine prologEngine, Term goal, boolean errorHandledQuery, Jpc context) {
    	super(new SingleThreadedJplQuery(prologEngine, goal, errorHandledQuery, context));
    }
 
    private ExecutorService getExecutor() {
        if(executor == null)
            executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "JPL thread");
                    t.setDaemon(true);
                    return t;
                }
            });
        return executor;
    }

    /**
     * Releases the executor.
     * The current version just shut it down. A more advanced implementation should use an executor pool.
     */
    private void releaseExecutor() {
    	executor.shutdownNow();
        executor = null;
        resetJplQuery();
    }

    private SingleThreadedJplQuery getQuery() {
    	return (SingleThreadedJplQuery)query;
    }
    
    private void resetJplQuery() {
    	getQuery().resetJplQuery();
    }
    
    @Override
    public void close() {
        try {
        	submit(new Runnable() {
			    @Override
			    public void run() {
			    	MultiThreadedJplQuery.super.close();
			    }
			});
        } finally {
        	releaseExecutor();
        }
    }
    
    @Override
    public boolean hasNext() {
    	try {
    		Boolean hasNext = submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                	return MultiThreadedJplQuery.super.hasNext();
                }
            });
    		if(!hasNext)
    			releaseExecutor();
    		return hasNext;
    	} catch (Exception e) {
        	releaseExecutor();
        	return rethrow(e);
        }
    }
    
    @Override
    public Solution next() {
        try {
            return submit(new Callable<Solution>() {
                @Override
                public Solution call() throws Exception {
                	return MultiThreadedJplQuery.super.next();
                }
            });
        } catch (NoSuchElementException e) {
        	releaseExecutor();
        	throw(e);
        } catch (Exception e) {
        	releaseExecutor();
        	throw(e);
        }
    }
    
    @Override
    public long numberOfSolutions() {
    	Long numberOfSolutions;
    	try {
    		numberOfSolutions = submit(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                	return MultiThreadedJplQuery.super.numberOfSolutions();
                }
            });
    	} catch (IllegalStateException e) {
    		throw(e);
        } catch (Exception e) {
        	releaseExecutor();
        	throw(e);
        }
        releaseExecutor();
        return numberOfSolutions;
    }
    
    @Override
    public boolean hasSolution() {
    	Boolean hasSolution;
    	try {
    		hasSolution = submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                	return MultiThreadedJplQuery.super.hasSolution();
                }
            });
    	} catch (IllegalStateException e) {
    		throw(e);
        } catch (Exception e) {
        	releaseExecutor();
        	throw(e);
        }
        releaseExecutor();
        return hasSolution;
    }
	
    @Override
    public Optional<Solution> oneSolution() {
    	Optional<Solution> solutionOpt;
        try {
        	solutionOpt = submit(new Callable<Optional<Solution>>() {
                @Override
                public Optional<Solution> call() throws Exception {
                	return MultiThreadedJplQuery.super.oneSolution();
                }
            });
        } catch (IllegalStateException e) {
        	throw(e);
        } catch (Exception e) {
        	releaseExecutor();
        	throw(e);
        }
        releaseExecutor();
        return solutionOpt;
    }
    
    @Override
    public Solution oneSolutionOrThrow() {
        Solution solution;
        try {
        	solution = submit(new Callable<Solution>() {
                @Override
                public Solution call() throws Exception {
                	return MultiThreadedJplQuery.super.oneSolutionOrThrow();
                }
            });
        } catch (IllegalStateException e) {
        	throw(e);
        } catch (Exception e) {
        	releaseExecutor();
        	throw(e);
        }
        releaseExecutor();
        return solution;
    }
    
    @Override
    public List<Solution> allSolutions() {
        List<Solution> solutions;
        try {
        	solutions = submit(new Callable<List<Solution>>() {
                @Override
                public List<Solution> call() throws Exception {
                	return MultiThreadedJplQuery.super.allSolutions();
                }
            });
        } catch (IllegalStateException e) {
        	throw(e);
        } catch (Exception e) {
        	releaseExecutor();
        	throw(e);
        }
        releaseExecutor();
        return solutions;
    }
    
    public List<Solution> nSolutions(final long n) {
    	List<Solution> solutions;
        try {
        	solutions = submit(new Callable<List<Solution>>() {
                @Override
                public List<Solution> call() throws Exception {
                	return MultiThreadedJplQuery.super.nSolutions(n);
                }
            });
        } catch (IllegalStateException e) {
        	throw(e);
        } catch (Exception e) {
        	releaseExecutor();
        	throw(e);
        }
        releaseExecutor();
        return solutions;
    }
    
    
    @Override
    public List<Solution> solutionsRange(final long from, final long to) {
        List<Solution> solutions;
        try {
        	solutions = submit(new Callable<List<Solution>>() {
                @Override
                public List<Solution> call() throws Exception {
                	return MultiThreadedJplQuery.super.solutionsRange(from, to);
                }
            });
        } catch (IllegalStateException e) {
        	throw(e);
        } catch(Exception e) {
        	releaseExecutor();
        	throw(e);
        }
        releaseExecutor();
        return solutions;
    }
    
    


    private void submit(final Runnable runnable) {
    	submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
    	});
    }

    private <T> T submit(final Callable<T> callable) {
    	Callable<ExecutionResult<T>> executionResultCallable = new Callable<ExecutionResult<T>>() {
			@Override
			public ExecutionResult<T> call() throws Exception {
				try {
					return new ExecutionResult<>(callable.call());
				} catch(Exception e) {
					return new ExecutionResult<>(e);
				}
			}
    	};
    	try {
    		return getExecutor().submit(executionResultCallable).get().getResult();
    	} catch(Exception e) {
    		return rethrowExecutorException(e);
    	}
    }
    
    private <T> T rethrow(Throwable e) {
    	if(e instanceof RuntimeException) {
    		throw (RuntimeException)e;
    	} else {
    		throw new RuntimeException(e);
    	}
    }
    
    private <T> T rethrowExecutorException(Throwable e) {
    	if(!(e instanceof ExecutionException)) {
    		return rethrow(e);
    	} else {
    		return rethrowExecutorException(e.getCause());
    	}
    }
    
}

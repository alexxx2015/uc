package de.tum.in.i22.uc.cm.distribution;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A class for managing {@link Executors} within the PDP.
 *
 * @author Florian Kelbert
 *
 */
public class Threading {
	private static ExecutorService _instance = null;

	public static ExecutorService instance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all
		 * of it. Yet, it is the best way to implement a thread-safe singleton,
		 * cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes
		 * -with-example-code -FK-
		 */
		if (_instance == null) {
			synchronized (Threading.class) {
				if (_instance == null)
					_instance = Executors.newCachedThreadPool();
			}
		}
		return _instance;
	}

	/**
	 * A wrapper method for {@link Future#get()},
	 * essentially taking care of the Exceptions.
	 * This method throws a {@link RuntimeException}
	 * if any Exception occurs.
	 *
	 * @param future
	 * @return
	 */
	public static <T> T resultOf(Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * A wrapper method for {@link ExecutorCompletionService#take()},
	 * essentially taking care of the Exceptions.
	 * This method throws a {@link RuntimeException}
	 * if any Exception occurs.
	 *
	 * @param cs
	 * @return
	 */
	public static <T> Future<T> take(CompletionService<T> cs) {
		try {
			return cs.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}


	/**
	 * Similar to {@link Threading#take(CompletionService)}, this
	 * method waits for the next results within the specified
	 * {@link CompletionService}. However, this method will wait
	 * for the specified number of results to be retrieved and
	 * ignore their return value.
	 *
	 * @param num for how many to wait
	 * @param cs
	 */
	public static <T> void waitFor(int num, CompletionService<T> cs) {
		try {
			while (num-- > 0) {
				cs.take();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * A method performing {@link ExecutorCompletionService#take()}
	 * on the specified parameter. After taking the Future object
	 * out of the {@link ExecutorCompletionService},
	 * {@link Threading#resultOf(Future)} is applied to get the
	 * actual result.
	 *
	 * Throws a {@link RuntimeException} if any Exception occurs.
	 *
	 * @param future
	 * @return
	 */
	public static <T> T takeResult(CompletionService<T> cs) {
		try {
			return resultOf(cs.take());
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}

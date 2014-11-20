package de.tum.in.i22.uc.pdp;

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
public class PdpThreading {
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
			synchronized (PdpThreading.class) {
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
	 * @param future
	 * @return
	 */
	public static <T> Future<T> take(ExecutorCompletionService<T> ecs) {
		try {
			return ecs.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * A method performing {@link ExecutorCompletionService#take()}
	 * on the specified parameter. After taking the Future object
	 * out of the {@link ExecutorCompletionService},
	 * {@link PdpThreading#resultOf(Future)} is applied to get the
	 * actual result.
	 *
	 * Throws a {@link RuntimeException} if any Exception occurs.
	 *
	 * @param future
	 * @return
	 */
	public static <T> T takeResult(ExecutorCompletionService<T> ecs) {
		try {
			return resultOf(ecs.take());
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}

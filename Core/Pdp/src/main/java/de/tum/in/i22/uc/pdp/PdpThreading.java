package de.tum.in.i22.uc.pdp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

	public static <T> T resultOf(Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static <T> Future<T> take(ExecutorCompletionService<T> ecs) {
		try {
			return ecs.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static <T> T takeResult(ExecutorCompletionService<T> ecs) {
		try {
			return resultOf(ecs.take());
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}

/*
 * Sébastien Eon 2016 / CC0-1.0
 */
package sew.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class SimpleExecutor implements Executor {

	private final Queue<Runnable> tasks;
	private final Thread runner;

	public SimpleExecutor() {
		this.tasks = new ArrayDeque<Runnable>();
		this.runner = new Thread("Executor") {
			@Override
			public void run() {
				while (poll()) {
					;
				}
			}

		};
		this.runner.start();
	}

	private boolean poll() {
		if (this.tasks.isEmpty()) {
			synchronized (this.runner) {
				try {
					this.runner.wait();
				} catch (InterruptedException e) {
					return false;
				}
			}
		} else {
			Runnable runnable = this.tasks.poll();
			try {
				runnable.run();
			} catch (Throwable t) {
				System.out.println("Uncaught exception in executor: " + t.toString());
				t.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void execute(Runnable command) {
		this.tasks.add(command);
		synchronized (this.runner) {
			this.runner.notify();
		}
	}

}

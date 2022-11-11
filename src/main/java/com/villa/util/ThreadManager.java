package com.villa.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 封装的线程池 方便调用
 */
public class ThreadManager {
	/**
	 * 线程池中线程数量
	 */
	private static ThreadManager instance = new ThreadManager();
	private static ExecutorService pool;

	public static ThreadManager getInstance() {
		return instance;
	}

	/**
	 * 设置的线程数可能会失效 -> 如果在调用此方法前调用无参的getInstance 那线程数固定为10
	 * @param fixedThread 固定的线程数
	 */
	public static ThreadManager getInstance(int fixedThread) {
		if(pool==null){
			pool = Executors.newFixedThreadPool(fixedThread);
		}
		return instance;
	}
	private ThreadManager() {
		pool = Executors.newFixedThreadPool(10);
	}

	public void execute(Runnable r) {
		pool.execute(r);
	}

	/**
	 * 与execute作用一致 只是有返回值 可以在返回值中获取返回结果或异常
	 * future.get() 抛出的异常就是线程执行过程中抛出的 不再输出而是get时再捕获
	 */
	public Future submit(Runnable task) {
		return pool.submit(task);
	}

	public ExecutorService getPool() {
		return pool;
	}

	/**
	 * 打印线程池状态
	 */
	public void printPoolStatus() {
		//当前线程池中活动的线程数
		int activeCount = ((ThreadPoolExecutor) ThreadManager.getInstance().getPool()).getActiveCount();
		//已经执行完成的任务数
		long completeTaskCount = ((ThreadPoolExecutor) ThreadManager.getInstance().getPool()).getCompletedTaskCount();
		//线程池中的任务总量
		long taskCount = ((ThreadPoolExecutor) ThreadManager.getInstance().getPool()).getTaskCount();
		System.out.println(String.format("当前活动线程数:%d\t\t完成的任务数:%d\t\t线程池任务总量:%d",activeCount,completeTaskCount,taskCount));
	}

	public void shutdown() {
		pool.shutdown();
	}
}

package com.openplatform.adas.threadpool;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SingleThreadExecutor extends ThreadPoolExecutor {
	
	public SingleThreadExecutor() {
		super(1, 1, 0L, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<Runnable>(1), new NameThreadFactory("Single"), new DiscardPolicy());
	}
	
	public SingleThreadExecutor(String name) {
		super(1, 1, 0L, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<Runnable>(1), new NameThreadFactory(name), new DiscardPolicy());
	}
	
	public SingleThreadExecutor(RejectedExecutionHandler handler) {
		super(1, 1, 0L, TimeUnit.MILLISECONDS,  new LinkedBlockingQueue<Runnable>(1), new NameThreadFactory("Single"), handler);
	}
}

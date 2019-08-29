package com.openplatform.adas.threadpool;

import java.util.concurrent.ThreadFactory;

public class NameThreadFactory implements ThreadFactory{

	private final String name;
	private int count;
	
	
	public NameThreadFactory(String name) {
		super();
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName("T:"+name+"-"+count++);
		return thread;
	}
}

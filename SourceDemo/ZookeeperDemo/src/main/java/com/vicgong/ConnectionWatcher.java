package com.vicgong;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ConnectionWatcher implements Watcher{
	private static final int SESSION_TIME = 5000;
	protected ZooKeeper zk;
	private CountDownLatch connectedSignal = new CountDownLatch(1); 
	public void connect(String host) throws IOException, InterruptedException{	
		zk = new ZooKeeper(host, SESSION_TIME, this);
		connectedSignal.await();
	}
	@Override
	public void process(WatchedEvent event) {
		if(event.getState() == KeeperState.SyncConnected){
			connectedSignal.countDown();
		}
	}
	
	public void close() throws InterruptedException {
		zk.close();
	}
}

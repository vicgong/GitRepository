package com.vicgong;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

public class CreateGroup implements Watcher {
	
	private static final int SESSION_TIME = 5000;
	private ZooKeeper zk;
	private CountDownLatch connectedSignal = new CountDownLatch(1); 
	public void connect(String host) throws IOException, InterruptedException{	
		/**
		 * ZooKeeper类是客户端API的主要类，用于维护客户端和zookeeper服务之间的连接
		 * 参数1：zookeeper服务的主机地址，可指定端口，默认为2181
		 * 参数2：以毫秒为单位的会话超时参数
		 * 参数3：Watcher对象的实例，该对象接收来自zookeeper的回调，以获得各种事件的通知
		 */
		//CreateGroup是一个watcher对象，将它传递给zookeeper对象
		//当一个zookeeper实例被创建时，会启动一个线程连接到zookeeper服务
		zk = new ZooKeeper(host, SESSION_TIME, this);
		//由于对构造函数的调用是立即返回的，因此在使用新建的zookeeper对象前一定要等待其与zookeeper服务之间成功建立连接
		//使用java.util.concurrent包的CountDownLatch类来阻止使用新建的zookeeper
		//直到这个zookeeper对象已经准备就绪
		connectedSignal.await();
	}
	/**
	 * Watcher接口用于获取zookeeper对象是否准备就绪的信息
	 * 该结构只有一个方法process()
	 * 当客户端已经与zookeeper服务建立连接后,Watcher的process()方法会被调用
	 * 参数event：用于表示该连接的事件
	 */
	@Override
	public void process(WatchedEvent event) {
		if(event.getState() == KeeperState.SyncConnected){
			//在接收到一个连接事件：以Watcher.Event.KeeperState的枚举类型值SyncConnected来表示
			//调用countDown()方法来递减它的计数器
			//锁存器(latch)创建时带有一个值为1的计数器
			//1表示在它释放所有等待线程之前需要发生的事件数
			//这里调用countDown()方法之后，计数器的值变为0,则await()方法返回
			connectedSignal.countDown();
		}
	}
	
	public void create(String groupName) throws KeeperException, InterruptedException{
		String path = "/" + groupName;
		/**
		 * create()方法用来创建一个新的Zookeeper的znode
		 * 参数1：路径
		 * 参数2：znode的内容数据(字节数组，这里用空值)
		 * 参数3：访问控制列表(简称ACL，这里使用完全开发的ACL，允许任何客户端对znode进行读/写)
		 * 参数4：创建znode的类型,包括 PERSISTENT：持久的znode、EPHEMERAL：短暂的znode
		 * 返回：zookeeper所创建的节点路径
		 */
		String createPath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("Created " + createPath);
	}
	
	public void close() throws InterruptedException {
		zk.close();
	}
	
	public static void main(String[] args) throws Exception { 
		CreateGroup createGroup = new CreateGroup();
		createGroup.connect(args[0]);
		createGroup.create(args[1]);
		createGroup.close();
	}
	
}

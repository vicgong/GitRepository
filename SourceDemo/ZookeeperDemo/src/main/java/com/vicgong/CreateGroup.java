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
		 * ZooKeeper���ǿͻ���API����Ҫ�࣬����ά���ͻ��˺�zookeeper����֮�������
		 * ����1��zookeeper�����������ַ����ָ���˿ڣ�Ĭ��Ϊ2181
		 * ����2���Ժ���Ϊ��λ�ĻỰ��ʱ����
		 * ����3��Watcher�����ʵ�����ö����������zookeeper�Ļص����Ի�ø����¼���֪ͨ
		 */
		//CreateGroup��һ��watcher���󣬽������ݸ�zookeeper����
		//��һ��zookeeperʵ��������ʱ��������һ���߳����ӵ�zookeeper����
		zk = new ZooKeeper(host, SESSION_TIME, this);
		//���ڶԹ��캯���ĵ������������صģ������ʹ���½���zookeeper����ǰһ��Ҫ�ȴ�����zookeeper����֮��ɹ���������
		//ʹ��java.util.concurrent����CountDownLatch������ֹʹ���½���zookeeper
		//ֱ�����zookeeper�����Ѿ�׼������
		connectedSignal.await();
	}
	/**
	 * Watcher�ӿ����ڻ�ȡzookeeper�����Ƿ�׼����������Ϣ
	 * �ýṹֻ��һ������process()
	 * ���ͻ����Ѿ���zookeeper���������Ӻ�,Watcher��process()�����ᱻ����
	 * ����event�����ڱ�ʾ�����ӵ��¼�
	 */
	@Override
	public void process(WatchedEvent event) {
		if(event.getState() == KeeperState.SyncConnected){
			//�ڽ��յ�һ�������¼�����Watcher.Event.KeeperState��ö������ֵSyncConnected����ʾ
			//����countDown()�������ݼ����ļ�����
			//������(latch)����ʱ����һ��ֵΪ1�ļ�����
			//1��ʾ�����ͷ����еȴ��߳�֮ǰ��Ҫ�������¼���
			//�������countDown()����֮�󣬼�������ֵ��Ϊ0,��await()��������
			connectedSignal.countDown();
		}
	}
	
	public void create(String groupName) throws KeeperException, InterruptedException{
		String path = "/" + groupName;
		/**
		 * create()������������һ���µ�Zookeeper��znode
		 * ����1��·��
		 * ����2��znode����������(�ֽ����飬�����ÿ�ֵ)
		 * ����3�����ʿ����б�(���ACL������ʹ����ȫ������ACL�������κοͻ��˶�znode���ж�/д)
		 * ����4������znode������,���� PERSISTENT���־õ�znode��EPHEMERAL�����ݵ�znode
		 * ���أ�zookeeper�������Ľڵ�·��
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

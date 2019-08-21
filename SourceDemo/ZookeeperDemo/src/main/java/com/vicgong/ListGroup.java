package com.vicgong;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

public class ListGroup extends ConnectionWatcher {

	public void list(String groupName) throws KeeperException, InterruptedException{
		String path = "/" + groupName;
		try{
			/**
			 * getChildren()�������������znode���ӽڵ�
			 * ����1��znode��·��
			 * ����2���۲��־
			 * �����znode�����ù۲��־����ôһ����znode��״̬�ı䣬�����Ĺ۲�(Watcher)�ᱻ����
			 * 
			 */
			List<String> childrens = zk.getChildren(path, false);
			if(childrens.isEmpty()){
				System.out.printf("No members in group %s\n",groupName);
				System.exit(1);;
			}
			for(String children : childrens){
				System.out.println(children);
			}
			/**
			 * ��׽NoNodeException�쳣���������znode������ʱ�����쳣�׳�
			 */
		} catch (KeeperException.NoNodeException e) {
			System.out.printf("Group %s does not exist\n",groupName);
			System.exit(1);;
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		ListGroup listGroup = new ListGroup();
		listGroup.connect(args[0]);
		listGroup.list(args[1]);
		listGroup.close();
	}

}

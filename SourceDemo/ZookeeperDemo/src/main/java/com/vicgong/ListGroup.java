package com.vicgong;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

public class ListGroup extends ConnectionWatcher {

	public void list(String groupName) throws KeeperException, InterruptedException{
		String path = "/" + groupName;
		try{
			/**
			 * getChildren()方法检索并输出znode的子节点
			 * 参数1：znode的路径
			 * 参数2：观察标志
			 * 如果在znode上设置观察标志，那么一旦该znode的状态改变，关联的观察(Watcher)会被触发
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
			 * 捕捉NoNodeException异常，代表组的znode不存在时，该异常抛出
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

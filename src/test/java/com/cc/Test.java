package com.cc;

import com.cc.zookeeper.ZookeeperClient;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * User: chenchong
 * Date: 2019/1/15
 * description:
 */
public class Test {

	public static void main(String[] args) throws InterruptedException {
		String zkConnect = "172.18.1.0:2181,172.18.1.2:2181,172.18.1.3:2181";
		try {
			ZooKeeper zk = new ZooKeeper(zkConnect,10000,null);
		} catch (IOException e) {
			System.out.println("has errrr");
		}

		while(true) {
			System.out.println(".....");
			Thread.sleep(3000);
		}
	}
}

package com.cc;

import com.cc.zookeeper.ZkClient;
import com.cc.zookeeper.ZookeeperClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.ZooDefs;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * User: chenchong
 * Date: 2019/1/15
 * description:
 */
public class ZkClientTest {

	private ZkClient zk;
	private String zkConnect = "172.18.1.0:2181,172.18.1.2:2181,172.18.1.3:2181";

	@Before
	public void setUp()  {
		try {
			zk = new ZkClient(zkConnect,10000,10000,100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void existTest() {
		zk.existNode("/011");
	}

	@Test
	public void recursiveCreateTest() {
		zk.createRecursive("/cc/demo/data/demmo","hello".getBytes());
	}

	@Test
	public void recursiveDeleteTest() {
		zk.deleteRecursive("/cc",-1);
	}

	@Test
	public void transTest() {
//		zk.createAndSetNodeData();
	}

	@Test
	public void retryTest() throws InterruptedException {
		while(true) {
			System.out.println("start");
			Thread.sleep(5000);
			zk.existNode("/demo2");
		}
	}

}

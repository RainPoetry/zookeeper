package com.cc;

import com.cc.zookeeper.ZookeeperClient;
import com.cc.zookeeper.handler.ZkHandler;
import com.cc.zookeeper.request.*;
import com.cc.zookeeper.response.AsyncResponse;
import com.cc.zookeeper.response.CreateResponse;
import com.cc.zookeeper.response.GetChildrenResponse;
import com.cc.zookeeper.response.GetDataResponse;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * User: chenchong
 * Date: 2019/1/14
 * description:
 */
public class ZookeeperClientTest {

	private ZookeeperClient zookeeper;
	private String zkConnect = "172.18.1.0:2181,172.18.1.2:2181,172.18.1.3:2181";

	@Before
	public void setUp() throws IOException {
		zookeeper = new ZookeeperClient(zkConnect,1000,1000,100);
	}

	@After
	public void tearDown() {
		if (zookeeper != null)
			zookeeper.close();
	}

	@Test
	public void testUnresolvableConnectString() throws InterruptedException {
		Thread.sleep(5000);
		System.out.println("over!!");
	}

	@Test
	public void testDeleteNonExistentZNode() {
		AsyncResponse response = zookeeper.handleRequest(new DeleteRequest("/cc",null,-1));
	}

	@Test
	public void testCreateNode() {
		AsyncResponse response = zookeeper.handleRequest(new CreateRequest("hello word".getBytes(), "/cc","789",
				CreateMode.PERSISTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE));
	}

	@Test
	public void testCreateAndDeleteNode() {
		testCreateNode();
		testDeleteNonExistentZNode();
	}

	@Test
	public void testGetData() {
//		testCreateNode();
		AsyncResponse response = zookeeper.handleRequest(new GetDataRequest("/cc",null));
		GetDataResponse res = (GetDataResponse)response;
		System.out.println("data: " + new String(res.data()));
	}

	@Test
	public void testGetChildren() {
		AsyncResponse response = zookeeper.handleRequest(new GetChildrenRequest("/"));
		GetChildrenResponse res = (GetChildrenResponse)response;
		System.out.println(res.children().toString());
	}

	@Test
	public void testPipelinedGetData() {
		List<AsyncRequest> requests = new ArrayList<>();
		for(int i=0; i < 5; i++) {
			requests.add(new CreateRequest(("i:"+i).getBytes(),"/"+i,CreateMode.PERSISTENT,ZooDefs.Ids.OPEN_ACL_UNSAFE));
		}
		List<AsyncResponse> responses = new ArrayList<>();
		requests.stream().forEach(data->{
			AsyncResponse response = zookeeper.handleRequest(data);
//			mayThrow(response);
			responses.add(response);
		});
		responses.forEach(data->System.out.println(data.apiKeys() +" | " + data.path() +" | " + data.code()));
		responses.stream().map(data->zookeeper.handleRequest(new GetDataRequest(data.path())))
				.forEach(data-> {
					GetDataResponse response = (GetDataResponse)data;
					System.out.println(data.apiKeys() +" | " + data.path() +" | " + data.code() +" |" +new String(response.data()));
				});

	}

	@Test
	public void zNodeChangeandlerForCreate() {
		CountDownLatch latch = new CountDownLatch(1);
		String path = "/demo/demo2";
		ZkHandler handler = new ZkHandler() {
			@Override
			public String path() {
				return path;
			}

			@Override
			public void handleCreation() {
				System.out.println("handle: create");
			}

			@Override
			public void handleDeletion() {
				System.out.println("handle: delete");
			}

			@Override
			public void handleDataChange() {
				System.out.println("handle: data");
			}

			@Override
			public void handleChildChange() {
				System.out.println("handle: children");
			}
		};
		zookeeper.registerZkHandler(handler);
		ExistsRequest request0 = new ExistsRequest(path);
		CreateRequest request1 = new CreateRequest("data:demo".getBytes(),path,CreateMode.PERSISTENT,ZooDefs.Ids.OPEN_ACL_UNSAFE);
		zookeeper.handleRequests(Arrays.asList(request0,request1)).forEach(it->{
			System.out.println(it.code() +" | " + it.path() +" | " + it.apiKeys());
		});
	}

	@Test
	public void testBlockOnRequestCompletionFromStateChangeHandler() throws InterruptedException {
		Thread t1 = new Thread(()->{
			String path = "/test";
			CreateRequest request1 = new CreateRequest("data:demo".getBytes(),path,CreateMode.PERSISTENT,ZooDefs.Ids.OPEN_ACL_UNSAFE);
			zookeeper.handleRequest(request1);
		});
		Thread t2 = new Thread(()->{
			zookeeper.foreReinitialize();
		});

		t2.start();
		Thread.sleep(100);
		t1.start();

		t2.join();
		t1.join();
	}


	@Test
	public void testSessionExpiry() throws InterruptedException {


		String path = "/test2";
		CreateRequest request1 = new CreateRequest("data:demo".getBytes(),path,CreateMode.PERSISTENT,ZooDefs.Ids.OPEN_ACL_UNSAFE);
		zookeeper.handleRequest(request1);

		Thread.sleep(10000);

		testDeleteNonExistentZNode();
	}



}

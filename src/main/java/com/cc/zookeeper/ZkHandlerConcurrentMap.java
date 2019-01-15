package com.cc.zookeeper;

import com.cc.zookeeper.handler.ZkHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: chenchong
 * Date: 2019/1/12
 * description:
 */
public class ZkHandlerConcurrentMap<K, V extends ZkHandler> extends ConcurrentHashMap<K, V> {

	public ZkHandlerConcurrentMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ZkHandlerConcurrentMap() {
		super();
	}

	public ZkHandlerConcurrentMap(int initialCapacity) {
		super(initialCapacity);
	}

	public void getAndExecuteCreate(String key) {
		V v;
		if ((v = get(key)) != null)
			v.handleCreation();
	}

	public void getAndExecuteDeletion(String key) {
		V v;
		if ((v = get(key)) != null)
			v.handleDeletion();
	}

	public void getAndExecuteDataChange(String key) {
		V v;
		if ((v = get(key)) != null)
			v.handleDataChange();
	}

	public void getAndExecuteChildChange(String key) {
		V v;
		if ((v = get(key)) != null)
			v.handleChildChange();
	}
}

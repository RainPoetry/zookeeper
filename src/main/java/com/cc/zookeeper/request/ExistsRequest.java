package com.cc.zookeeper.request;

import com.cc.zookeeper.status.ZkApis;
import com.cc.zookeeper.response.AsyncResponse;

/**
 * User: chenchong
 * Date: 2019/1/14
 * description:
 */
public class ExistsRequest extends AsyncRequest{


	public ExistsRequest( String path) {
		this(path,null);
	}
	public ExistsRequest(String path, Object ctx) {
		super(ZkApis.EXISTS, path, ctx);
	}
}

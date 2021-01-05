/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package cn.jpush.spring.boot;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.connection.HttpProxy;
import cn.jpush.api.JPushClient;

public class JPushClientExt extends JPushClient {

	private final String appId;
	
	public JPushClientExt(String appId, String appKey, String masterSecret) {
		super(masterSecret, appKey);
		this.appId = appId;
	}
	
	public JPushClientExt(String appId, String appKey, String masterSecret, HttpProxy proxy, ClientConfig conf) {
		super(masterSecret, appKey, proxy, conf);
		this.appId = appId;
	}
	
	public String getAppId() {
		return appId;
	}
	
}

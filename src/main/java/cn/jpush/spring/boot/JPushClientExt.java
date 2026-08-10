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

/**
 * Extension of {@link JPushClient} that also carries the local application identifier, allowing slave
 * JPush clients to be keyed and looked up by app id within the {@link JPushTemplate}.
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class JPushClientExt extends JPushClient {

	private final String appId;

	/** Create a slave client without a proxy. @param appId local application id @param appKey JPush app key @param masterSecret JPush master secret */
	public JPushClientExt(String appId, String appKey, String masterSecret) {
		super(masterSecret, appKey);
		this.appId = appId;
	}

	/** Create a slave client with an HTTP proxy and client config. @param appId local application id @param appKey JPush app key @param masterSecret JPush master secret @param proxy HTTP proxy @param conf client config */
	public JPushClientExt(String appId, String appKey, String masterSecret, HttpProxy proxy, ClientConfig conf) {
		super(masterSecret, appKey, proxy, conf);
		this.appId = appId;
	}

	/** Return the local application id of this slave client. @return the app id */
	public String getAppId() {
		return appId;
	}

}

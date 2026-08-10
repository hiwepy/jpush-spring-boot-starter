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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import cn.jiguang.common.DeviceType;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;

/**
 * Convenience template for sending JPush notifications through the primary client and any registered
 * slave clients, closing all clients on bean destruction.
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class JPushTemplate implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(JPushTemplate.class);
    private JPushClient jPushClient;
    private ConcurrentHashMap<String, JPushClientExt> jPushClientMap;
    private boolean production;

    /** Create a template with the primary client and slave clients keyed by application id. @param jPushClient primary JPush client @param clients slave clients @param prod iOS production-environment flag */
    public JPushTemplate(JPushClient jPushClient, List<JPushClientExt> clients, boolean prod) {
        this.jPushClient = jPushClient;
        this.jPushClientMap = new ConcurrentHashMap<>();
		for (JPushClientExt jPushClientExt : clients) {
			this.jPushClientMap.put(jPushClientExt.getAppId(), jPushClientExt);
		}
        this.production = prod;
    }

    /** Return the primary JPush client. @return the primary client */
    public JPushClient getjPushClient() {
        return jPushClient;
    }

	/** Return the slave clients keyed by application id. @return the slave client map */
	public Map<String, JPushClientExt> getjPushClientMap() {
		return jPushClientMap;
	}

    /** Send a push to all audiences using the primary client. @param pushObject push content @return true if the push succeeded */
    public boolean sendPush(PushObject pushObject) {
		return this.sendPush(Audience.all(), pushObject);
    }

    /** Send a push to the given aliases using the primary client. @param alias target aliases @param pushObject push content @return true if the push succeeded */
    public boolean sendPush(List<String> alias, PushObject pushObject) {
		return this.sendPush(Audience.alias(alias), pushObject);
    }

    /** Send a push to the given tags using the primary client. @param tags target tags @param pushObject push content @return true if the push succeeded */
    public boolean sendPushByTag(List<String> tags, PushObject pushObject) {
		return this.sendPush(Audience.tag(tags), pushObject);
	}

    /** Send a push to the given audience using the primary client. @param audience target audience @param pushObject push content @return true if the push succeeded */
    public boolean sendPush(Audience audience, PushObject pushObject) {
    	
        PushPayload payload = JPushNotifications.buildPushPayloadForAndroidAndIos(production, audience, pushObject);
        try {
            PushResult result = jPushClient.sendPush(payload);
            LOG.info("Got result - " + result);
            return result.isResultOK();
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            LOG.error("Sendno: " + payload.getSendno());
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
            LOG.error("Sendno: " + payload.getSendno());
        }
        return false;
    }
    
    /** Send a push to all audiences using the slave client for the given application id. @param appId local application id @param pushObject push content @return true if the push succeeded */
    public boolean sendPush(String appId, PushObject pushObject) {
		return this.sendPush(appId, Audience.all(), pushObject);
    }

    /** Send a push to the given aliases using the slave client for the given application id. @param appId local application id @param alias target aliases @param pushObject push content @return true if the push succeeded */
    public boolean sendPush(String appId, List<String> alias, PushObject pushObject) {
		return this.sendPush(appId, Audience.alias(alias), pushObject);
    }

    /** Send a push to the given tags using the slave client for the given application id. @param appId local application id @param tags target tags @param pushObject push content @return true if the push succeeded */
    public boolean sendPushByTag(String appId, List<String> tags, PushObject pushObject) {
		return this.sendPush(appId, Audience.tag(tags), pushObject);
	}

    /** Send a push to the given audience using the slave client for the given application id. @param appId local application id @param audience target audience @param pushObject push content @return true if the push succeeded */
    public boolean sendPush(String appId, Audience audience, PushObject pushObject) {
    	JPushClient jPushClient = jPushClientMap.get(appId);
    	if(Objects.nonNull(jPushClient)) {
    		PushPayload payload = JPushNotifications.buildPushPayloadForAndroidAndIos(production, audience, pushObject);
	        try {
	            PushResult result = jPushClient.sendPush(payload);
	            LOG.info("Got result - " + result);
	            return result.isResultOK();
	        } catch (APIConnectionException e) {
	            LOG.error("Connection error. Should retry later. ", e);
	            LOG.error("Sendno: " + payload.getSendno());
	        } catch (APIRequestException e) {
	            LOG.error("Error response from JPush server. Should review and fix it. ", e);
	            LOG.info("HTTP Status: " + e.getStatus());
	            LOG.info("Error Code: " + e.getErrorCode());
	            LOG.info("Error Message: " + e.getErrorMessage());
	            LOG.info("Msg ID: " + e.getMsgId());
	            LOG.error("Sendno: " + payload.getSendno());
	        }
    	}
        return false;
    }

    /** Delete the given alias for both Android and iOS using the primary client. @param alias the alias to clear */
    public void clearAlias(String alias) {
        try {
            jPushClient.deleteAlias(alias, DeviceType.Android.value());
            jPushClient.deleteAlias(alias, DeviceType.IOS.value());
        } catch (APIConnectionException e) {
            LOG.error("清理Alias异常", e);
        } catch (APIRequestException e) {
            LOG.error("清理Alias异常", e);
        }
    }

    /** Delete the given alias for both Android and iOS using the slave client for the given application id. @param appId local application id @param alias the alias to clear */
    public void clearAlias(String appId, String alias) {
        try {
        	JPushClient jPushClient = jPushClientMap.get(appId);
        	if(Objects.nonNull(jPushClient)) {
        		jPushClient.deleteAlias(alias, DeviceType.Android.value());
            	jPushClient.deleteAlias(alias, DeviceType.IOS.value());
        	}
        } catch (APIConnectionException e) {
            LOG.error("清理Alias异常", e);
        } catch (APIRequestException e) {
            LOG.error("清理Alias异常", e);
        }
    }

	/** Close the primary and all slave JPush clients on bean destruction. @throws Exception if a client cannot be closed */
	@Override
	public void destroy() throws Exception {
		try {
			jPushClient.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (JPushClientExt jPushClientExt : jPushClientMap.values()) {
			try {
				jPushClientExt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
}

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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.google.gson.JsonObject;

import cn.jiguang.common.DeviceType;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.push.model.notification.PlatformNotification;

public class JPushTemplate implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(JPushTemplate.class);
    private JPushClient jPushClient;
    private boolean production;

    public JPushTemplate(JPushClient jPushClient, boolean prod) {
        this.jPushClient = jPushClient;
        this.production = prod;
    }

    public JPushClient getjPushClient() {
        return jPushClient;
    }
    
    public Notification buildNotification(Object alert, PlatformNotification... notifications) {
		Notification.Builder builder = Notification.newBuilder().setAlert(alert);
		for (PlatformNotification platformNotification : notifications) {
			builder = builder.addPlatformNotification(platformNotification);
		}
		return builder.build();
    }

    public PushPayload buildPushPayloadForAndroidAndIos(PushObject pushObject) {
        AndroidNotification.Builder androidBuilder = AndroidNotification.newBuilder();
        IosNotification.Builder iosBuilder = IosNotification.newBuilder();
        if (pushObject.getExtras() != null && pushObject.getExtras().size() > 0) {
            for (Map.Entry<String, Object> entry : pushObject.getExtras().entrySet()) {
                if (entry.getValue() instanceof Number) {
                    Number value = (Number) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else if (entry.getValue() instanceof String) {
                    String value = (String) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else if (entry.getValue() instanceof Boolean) {
                    Boolean value = (Boolean) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else if (entry.getValue() instanceof JsonObject) {
                    JsonObject value = (JsonObject) entry.getValue();
                    androidBuilder.addExtra(entry.getKey(), value);
                    iosBuilder.addExtra(entry.getKey(), value);
                } else {
                    //ignore ...
                }
            }
        }
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios()) //推送平台
                .setAudience(Audience.alias(pushObject.getAlias())) //推送目标
                .setNotification(Notification.newBuilder()
                        .setAlert(pushObject.getAlert()) //通知信息
                        .addPlatformNotification(androidBuilder.build())
                        .addPlatformNotification(iosBuilder
                                .incrBadge(1) //角标数字加 1
                                .setSound(pushObject.getSound()) //通知声音为 "happy"
                                .build())
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(pushObject.getMsgContent()) //消息内容
                        .build())
                .setOptions(Options.newBuilder()
                        //设置ios平台环境，true表示推送生产环境，false表示要推送开发环境
                        .setApnsProduction(production)
                        .build())
                .build();
        return payload;
    }

    public boolean sendPush(PushObject pushObject) {
    	
        PushPayload payload = buildPushPayloadForAndroidAndIos(pushObject);
        try {
            PushResult result = jPushClient.sendPush(payload);
            LOG.info("Got result - " + result);
            return true;
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

	@Override
	public void destroy() throws Exception {
		jPushClient.close();
	}
    
}

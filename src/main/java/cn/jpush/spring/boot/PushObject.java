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

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class PushObject {

	/**
	 * The ID of one application on Local System.
	 */
	private String appId;

	/**
	 * 通知信息
	 */
	private String alert;

	/**
	 * 消息内容
	 */
	private String msgContent;

	// ios声音
	private String sound = "happy";

	// ios右上角条数
	private int badge = 1;

	Map<String, Object> extras = new HashMap<String, Object>();

}

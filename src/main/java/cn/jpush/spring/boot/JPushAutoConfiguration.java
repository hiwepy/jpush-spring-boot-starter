package cn.jpush.spring.boot;

import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;

/**
 * 
 */
@Configuration
@ConditionalOnClass(JPushClient.class)
@EnableConfigurationProperties({ JPushProperties.class })
public class JPushAutoConfiguration {

	
	@Bean
	@ConditionalOnMissingBean
	public ClientConfig jPushClientConfig()   {
		return ClientConfig.getInstance();  
	}
	
	@Bean
	public JPushClient jPushClient(JPushProperties properties, ClientConfig jPushClientConfig)   {
		// not set proxy
		if(Objects.isNull(properties.getProxy())) {
			return new JPushClient(properties.getMasterSecret(), properties.getAppKey(), properties.getProxy(), jPushClientConfig);  
		}
		return new JPushClient(properties.getMasterSecret(), properties.getAppKey());  
	}

	@Bean
	public JPushTemplate jPushTemplate(JPushClient jPushClient, JPushProperties properties)   {
		return new JPushTemplate(jPushClient, properties.isProduction());  
	}

}

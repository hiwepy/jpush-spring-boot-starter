package cn.jpush.spring.boot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import cn.jpush.spring.boot.JPushProperties.JPushSlaveClientConfig;

/**
 * Auto-configuration for the JPush push-notification integration, exposing the JPush client config,
 * the primary {@link JPushClient} and the {@link JPushTemplate} helper (including slave clients).
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(JPushClient.class)
@EnableConfigurationProperties({ JPushProperties.class })
public class JPushAutoConfiguration {


	/** Provide the default JPush {@link ClientConfig} unless one already exists. @return the shared ClientConfig */
	@Bean
	@ConditionalOnMissingBean
	public ClientConfig jPushClientConfig()   {
		return ClientConfig.getInstance();
	}

	/** Create the primary {@link JPushClient} bean from the configured app key, master secret and optional proxy. @param properties JPush properties @param jPushClientConfig client config @return a JPushClient */
	@Bean
	public JPushClient jPushClient(JPushProperties properties, ClientConfig jPushClientConfig)   {
		// not set proxy
		if(Objects.isNull(properties.getProxy())) {
			return new JPushClient(properties.getAppKey(), properties.getMasterSecret(), properties.getProxy(), jPushClientConfig);
		}
		return new JPushClient(properties.getAppKey(), properties.getMasterSecret());
	}

	/** Create the {@link JPushTemplate} bean, initialising slave clients from the configured slave list. @param jPushClient primary JPush client @param properties JPush properties @param jPushClientConfig client config @return a JPushTemplate */
	@Bean
	public JPushTemplate jPushTemplate(JPushClient jPushClient, JPushProperties properties, ClientConfig jPushClientConfig)   {
		List<JPushClientExt> clients = new ArrayList<JPushClientExt>();
		if(!CollectionUtils.isEmpty(properties.getSlaves())) {
			for (JPushSlaveClientConfig clientConfig : properties.getSlaves()) {
				if(Objects.isNull(clientConfig.getProxy())) {
					clients.add(new JPushClientExt(clientConfig.getAppId(), clientConfig.getAppKey(), clientConfig.getAppSecret(), clientConfig.getProxy(), jPushClientConfig));
				} else {
					clients.add(new JPushClientExt(clientConfig.getAppId(), clientConfig.getAppKey(), clientConfig.getAppSecret()));
				}
			}
		}
		return new JPushTemplate(jPushClient, clients, properties.isProduction());
	}

}

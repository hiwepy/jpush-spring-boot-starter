package cn.jpush.spring.boot;

import java.util.LinkedList;

import org.springframework.boot.context.properties.ConfigurationProperties;

import cn.jiguang.common.connection.HttpProxy;
import lombok.Data;

@ConfigurationProperties(JPushProperties.PREFIX)
@Data
public class JPushProperties {

	public static final String PREFIX = "jpush";

	private String appKey;

    private String masterSecret;

    private HttpProxy proxy;

    private boolean production = true;

    private LinkedList<JPushSlaveClientConfig> slaves = new LinkedList<>();

    /**
     * Configuration for an additional (slave) JPush application, keyed by the local application id.
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
     */
    @Data
    public static class JPushSlaveClientConfig {

    	/** The ID of one application on the local system. */
    	private String appId;

    	/** The KEY of one application on JPush. */
    	private String appKey;

    	/** API access secret of the appKey. */
        private String appSecret;

        /** The proxy, if there is no proxy, should be null. */
        private HttpProxy proxy;

        /** iOS environment toggle: true pushes to the APNs production environment, false to the development environment. */
        private boolean production = true;
    	
    }
    

}
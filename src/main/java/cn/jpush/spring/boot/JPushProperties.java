package cn.jpush.spring.boot;

import java.util.LinkedList;

import org.springframework.boot.context.properties.ConfigurationProperties;

import cn.jiguang.common.connection.HttpProxy;
import lombok.Data;

/**
 * Configuration properties for the JPush integration, bound to the {@code jpush.*} prefix.
 * <p>Holds the primary application credentials, an optional HTTP proxy, the iOS production/development
 * toggle and the list of additional slave client configurations.</p>
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@ConfigurationProperties(JPushProperties.PREFIX)
@Data
public class JPushProperties {

	public static final String PREFIX = "jpush";

	/** The KEY of one application on JPush. */
	private String appKey;

	/** API access secret of the appKey. */
    private String masterSecret;

    /** The proxy, if there is no proxy, should be null. */
    private HttpProxy proxy;

    /** iOS environment toggle: true pushes to the APNs production environment, false to the development environment. */
    private boolean production = true;

    /** Additional slave client configurations, each addressable by its local application id. */
    private LinkedList<JPushSlaveClientConfig> slaves = new LinkedList<>();

    /**
     * Configuration for an additional (slave) JPush application, keyed by the local application id.
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
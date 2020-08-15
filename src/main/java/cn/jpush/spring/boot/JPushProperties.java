package cn.jpush.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import cn.jiguang.common.connection.HttpProxy;

@ConfigurationProperties(JPushProperties.PREFIX)
public class JPushProperties {

	public static final String PREFIX = "jpush";

	/**
	 * The KEY of one application on JPush.
	 */
	private String appKey;

	/**
	 *  API access secret of the appKey.
	 */
    private String masterSecret;
    
    /**
     * The proxy, if there is no proxy, should be null.
     */
    private HttpProxy proxy;

    private boolean apns;

    //推送开发还是生产环境(设置ios平台环境，true表示推送生产环境，false表示要推送开发环境)
    private boolean production = true;

    //项目名称
    private String name;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }

    public boolean isApns() {
        return apns;
    }

    public void setApns(boolean apns) {
        this.apns = apns;
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    public HttpProxy getProxy() {
		return proxy;
	}

	public void setProxy(HttpProxy proxy) {
		this.proxy = proxy;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
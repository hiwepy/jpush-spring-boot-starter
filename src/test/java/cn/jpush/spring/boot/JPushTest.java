package cn.jpush.spring.boot;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for JPush starter components.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class JPushTest {

    private static final String VALID_KEY = "a1b2c3d4e5f6a1b2c3d4e5f6";
    private static final String VALID_SECRET = "f6e5d4c3b2a1f6e5d4c3b2a1";

    // --- JPushProperties ---

    @Test
    public void propertiesGettersAndSetters() {
        JPushProperties props = new JPushProperties();
        props.setAppKey("myKey");
        props.setMasterSecret("mySecret");
        props.setProduction(false);
        assertThat(props.getAppKey()).isEqualTo("myKey");
        assertThat(props.getMasterSecret()).isEqualTo("mySecret");
        assertThat(props.isProduction()).isFalse();
        assertThat(props.getProxy()).isNull();
        assertThat(props.getSlaves()).isNotNull().isEmpty();
    }

    @Test
    public void propertiesDefaults() {
        JPushProperties props = new JPushProperties();
        assertThat(props.isProduction()).isTrue();
        assertThat(props.getSlaves()).isNotNull();
    }

    @Test
    public void slaveClientConfigGettersAndSetters() {
        JPushProperties.JPushSlaveClientConfig config = new JPushProperties.JPushSlaveClientConfig();
        config.setAppId("app1");
        config.setAppKey("key1");
        config.setAppSecret("secret1");
        config.setProduction(false);
        assertThat(config.getAppId()).isEqualTo("app1");
        assertThat(config.getAppKey()).isEqualTo("key1");
        assertThat(config.getAppSecret()).isEqualTo("secret1");
        assertThat(config.isProduction()).isFalse();
        assertThat(config.getProxy()).isNull();
    }

    @Test
    public void slaveClientConfigDefaults() {
        JPushProperties.JPushSlaveClientConfig config = new JPushProperties.JPushSlaveClientConfig();
        assertThat(config.isProduction()).isTrue();
    }

    @Test
    public void propertiesWithSlaves() {
        JPushProperties props = new JPushProperties();
        JPushProperties.JPushSlaveClientConfig slave = new JPushProperties.JPushSlaveClientConfig();
        slave.setAppId("slave1");
        props.getSlaves().add(slave);
        assertThat(props.getSlaves()).hasSize(1);
        assertThat(props.getSlaves().get(0).getAppId()).isEqualTo("slave1");
    }

    // --- PushObject ---

    @Test
    public void pushObjectGettersAndSetters() {
        PushObject obj = new PushObject();
        obj.setAppId("app1");
        obj.setAlert("Hello!");
        obj.setMsgContent("Custom message");
        obj.setSound("default");
        obj.setBadge(5);
        assertThat(obj.getAppId()).isEqualTo("app1");
        assertThat(obj.getAlert()).isEqualTo("Hello!");
        assertThat(obj.getMsgContent()).isEqualTo("Custom message");
        assertThat(obj.getSound()).isEqualTo("default");
        assertThat(obj.getBadge()).isEqualTo(5);
    }

    @Test
    public void pushObjectDefaults() {
        PushObject obj = new PushObject();
        assertThat(obj.getSound()).isEqualTo("happy");
        assertThat(obj.getBadge()).isEqualTo(1);
        assertThat(obj.getExtras()).isNotNull().isEmpty();
    }

    @Test
    public void pushObjectExtras() {
        PushObject obj = new PushObject();
        obj.getExtras().put("key1", "value1");
        obj.getExtras().put("key2", 42);
        obj.getExtras().put("key3", true);
        assertThat(obj.getExtras()).hasSize(3);
    }

    // --- JPushClientExt ---

    @Test
    public void jPushClientExtCreation() {
        JPushClientExt ext = new JPushClientExt("app1", VALID_KEY, VALID_SECRET);
        assertThat(ext.getAppId()).isEqualTo("app1");
    }

    @Test
    public void jPushClientExtWithProxyAndConfig() {
        cn.jiguang.common.ClientConfig config = cn.jiguang.common.ClientConfig.getInstance();
        JPushClientExt ext = new JPushClientExt("app1", VALID_KEY, VALID_SECRET, null, config);
        assertThat(ext.getAppId()).isEqualTo("app1");
    }

    // --- JPushNotifications ---

    @Test
    public void buildNotification() {
        Notification notification = JPushNotifications.buildNotification("test alert",
                AndroidNotification.newBuilder().build(),
                IosNotification.newBuilder().build());
        assertThat(notification).isNotNull();
    }

    @Test
    public void buildPushPayloadWithNoExtras() {
        PushObject obj = new PushObject();
        obj.setAlert("Hello!");
        obj.setMsgContent("Message");
        PushPayload payload = JPushNotifications.buildPushPayloadForAndroidAndIos(true, Audience.all(), obj);
        assertThat(payload).isNotNull();
    }

    @Test
    public void buildPushPayloadWithStringExtras() {
        PushObject obj = new PushObject();
        obj.setAlert("Hello!");
        obj.setMsgContent("Message");
        obj.getExtras().put("key1", "value1");
        assertThat(JPushNotifications.buildPushPayloadForAndroidAndIos(true, Audience.all(), obj)).isNotNull();
    }

    @Test
    public void buildPushPayloadWithNumberExtras() {
        PushObject obj = new PushObject();
        obj.setAlert("Hello!");
        obj.setMsgContent("Message");
        obj.getExtras().put("count", 42);
        assertThat(JPushNotifications.buildPushPayloadForAndroidAndIos(true, Audience.all(), obj)).isNotNull();
    }

    @Test
    public void buildPushPayloadWithBooleanExtras() {
        PushObject obj = new PushObject();
        obj.setAlert("Hello!");
        obj.setMsgContent("Message");
        obj.getExtras().put("flag", true);
        assertThat(JPushNotifications.buildPushPayloadForAndroidAndIos(true, Audience.all(), obj)).isNotNull();
    }

    @Test
    public void buildPushPayloadWithJsonExtras() {
        PushObject obj = new PushObject();
        obj.setAlert("Hello!");
        obj.setMsgContent("Message");
        JsonObject json = new JsonObject();
        json.addProperty("nested", "value");
        obj.getExtras().put("jsonKey", json);
        assertThat(JPushNotifications.buildPushPayloadForAndroidAndIos(true, Audience.all(), obj)).isNotNull();
    }

    @Test
    public void buildPushPayloadWithUnsupportedExtraType() {
        PushObject obj = new PushObject();
        obj.setAlert("Hello!");
        obj.setMsgContent("Message");
        obj.getExtras().put("unsupported", new Object());
        assertThat(JPushNotifications.buildPushPayloadForAndroidAndIos(true, Audience.all(), obj)).isNotNull();
    }

    @Test
    public void buildPushPayloadForDevelopment() {
        PushObject obj = new PushObject();
        obj.setAlert("Hello!");
        obj.setMsgContent("Message");
        assertThat(JPushNotifications.buildPushPayloadForAndroidAndIos(false, Audience.all(), obj)).isNotNull();
    }

    // --- JPushTemplate with mocks ---

    private PushObject createPushObject() {
        PushObject obj = new PushObject();
        obj.setAlert("Test");
        obj.setMsgContent("Message");
        return obj;
    }

    @Test
    public void templateCreation() {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        assertThat(template.getjPushClient()).isSameAs(client);
        assertThat(template.getjPushClientMap()).isEmpty();
    }

    @Test
    public void templateWithSlaveClients() {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        JPushTemplate template = new JPushTemplate(client, Collections.singletonList(slave), true);
        assertThat(template.getjPushClientMap()).hasSize(1);
    }

    @Test
    public void templateSendPushToAllSuccess() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        PushResult mockResult = mock(PushResult.class);
        when(mockResult.isResultOK()).thenReturn(true);
        when(client.sendPush(any(PushPayload.class))).thenReturn(mockResult);
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        assertThat(template.sendPush(createPushObject())).isTrue();
    }

    @Test
    public void templateSendPushWithAliasSuccess() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        PushResult mockResult = mock(PushResult.class);
        when(mockResult.isResultOK()).thenReturn(true);
        when(client.sendPush(any(PushPayload.class))).thenReturn(mockResult);
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        assertThat(template.sendPush(Arrays.asList("alias1"), createPushObject())).isTrue();
    }

    @Test
    public void templateSendPushByTagSuccess() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        PushResult mockResult = mock(PushResult.class);
        when(mockResult.isResultOK()).thenReturn(true);
        when(client.sendPush(any(PushPayload.class))).thenReturn(mockResult);
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        assertThat(template.sendPushByTag(Arrays.asList("tag1"), createPushObject())).isTrue();
    }

    @Test
    public void templateSendPushToAudienceSuccess() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        PushResult mockResult = mock(PushResult.class);
        when(mockResult.isResultOK()).thenReturn(true);
        when(client.sendPush(any(PushPayload.class))).thenReturn(mockResult);
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        assertThat(template.sendPush(Audience.all(), createPushObject())).isTrue();
    }

    @Test
    public void templateSendPushConnectionException() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        when(client.sendPush(any(PushPayload.class)))
                .thenThrow(new APIConnectionException("connection error", new RuntimeException()));
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        assertThat(template.sendPush(Audience.all(), createPushObject())).isFalse();
    }

    @Test
    public void templateSendPushRequestException() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.responseCode = 400;
        wrapper.responseContent = "{\"error\":{\"code\":1001,\"message\":\"error\"}}";
        when(client.sendPush(any(PushPayload.class)))
                .thenThrow(new APIRequestException(wrapper));
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        assertThat(template.sendPush(Audience.all(), createPushObject())).isFalse();
    }

    @Test
    public void templateSendPushBySlaveSuccess() throws Exception {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        PushResult mockResult = mock(PushResult.class);
        when(mockResult.isResultOK()).thenReturn(true);
        when(slave.sendPush(any(PushPayload.class))).thenReturn(mockResult);
        JPushTemplate template = new JPushTemplate(master, Collections.singletonList(slave), true);
        assertThat(template.sendPush("app1", Audience.all(), createPushObject())).isTrue();
    }

    @Test
    public void templateSendPushBySlaveConnectionException() throws Exception {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        when(slave.sendPush(any(PushPayload.class)))
                .thenThrow(new APIConnectionException("error", new RuntimeException()));
        JPushTemplate template = new JPushTemplate(master, Collections.singletonList(slave), true);
        assertThat(template.sendPush("app1", Audience.all(), createPushObject())).isFalse();
    }

    @Test
    public void templateSendPushBySlaveRequestException() throws Exception {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.responseCode = 400;
        wrapper.responseContent = "{\"error\":{\"code\":1001}}";
        when(slave.sendPush(any(PushPayload.class)))
                .thenThrow(new APIRequestException(wrapper));
        JPushTemplate template = new JPushTemplate(master, Collections.singletonList(slave), true);
        assertThat(template.sendPush("app1", Audience.all(), createPushObject())).isFalse();
    }

    @Test
    public void templateSendPushBySlaveNonexistent() {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushTemplate template = new JPushTemplate(master, new ArrayList<>(), true);
        assertThat(template.sendPush("nonexistent", Audience.all(), createPushObject())).isFalse();
    }

    @Test
    public void templateSendPushBySlaveAlias() {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushTemplate template = new JPushTemplate(master, new ArrayList<>(), true);
        assertThat(template.sendPush("nonexistent", Arrays.asList("alias1"), createPushObject())).isFalse();
    }

    @Test
    public void templateSendPushBySlaveTag() {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushTemplate template = new JPushTemplate(master, new ArrayList<>(), true);
        assertThat(template.sendPushByTag("nonexistent", Arrays.asList("tag1"), createPushObject())).isFalse();
    }

    @Test
    public void templateClearAlias() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        template.clearAlias("testAlias");
        verify(client).deleteAlias("testAlias", cn.jiguang.common.DeviceType.Android.value());
        verify(client).deleteAlias("testAlias", cn.jiguang.common.DeviceType.IOS.value());
    }

    @Test
    public void templateClearAliasConnectionException() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        doThrow(new APIConnectionException("error", new RuntimeException()))
                .when(client).deleteAlias(anyString(), anyString());
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        template.clearAlias("testAlias");
    }

    @Test
    public void templateClearAliasRequestException() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.responseCode = 400;
        wrapper.responseContent = "{}";
        doThrow(new APIRequestException(wrapper))
                .when(client).deleteAlias(anyString(), anyString());
        JPushTemplate template = new JPushTemplate(client, new ArrayList<>(), true);
        template.clearAlias("testAlias");
    }

    @Test
    public void templateClearAliasWithSlave() throws Exception {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        JPushTemplate template = new JPushTemplate(master, Collections.singletonList(slave), true);
        template.clearAlias("app1", "testAlias");
        verify(slave).deleteAlias("testAlias", cn.jiguang.common.DeviceType.Android.value());
        verify(slave).deleteAlias("testAlias", cn.jiguang.common.DeviceType.IOS.value());
    }

    @Test
    public void templateClearAliasWithSlaveConnectionException() throws Exception {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        doThrow(new APIConnectionException("error", new RuntimeException()))
                .when(slave).deleteAlias(anyString(), anyString());
        JPushTemplate template = new JPushTemplate(master, Collections.singletonList(slave), true);
        template.clearAlias("app1", "testAlias");
    }

    @Test
    public void templateClearAliasWithSlaveRequestException() throws Exception {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.responseCode = 400;
        wrapper.responseContent = "{}";
        doThrow(new APIRequestException(wrapper))
                .when(slave).deleteAlias(anyString(), anyString());
        JPushTemplate template = new JPushTemplate(master, Collections.singletonList(slave), true);
        template.clearAlias("app1", "testAlias");
    }

    @Test
    public void templateClearAliasNonexistentSlave() {
        cn.jpush.api.JPushClient master = mock(cn.jpush.api.JPushClient.class);
        JPushTemplate template = new JPushTemplate(master, new ArrayList<>(), true);
        template.clearAlias("nonexistent", "testAlias");
    }

    @Test
    public void templateDestroy() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        JPushTemplate template = new JPushTemplate(client, Collections.singletonList(slave), true);
        template.destroy();
        verify(client).close();
        verify(slave).close();
    }

    @Test
    public void templateDestroyWithException() throws Exception {
        cn.jpush.api.JPushClient client = mock(cn.jpush.api.JPushClient.class);
        doThrow(new RuntimeException("close error")).when(client).close();
        JPushClientExt slave = mock(JPushClientExt.class);
        when(slave.getAppId()).thenReturn("app1");
        doThrow(new RuntimeException("close error")).when(slave).close();
        JPushTemplate template = new JPushTemplate(client, Collections.singletonList(slave), true);
        template.destroy();
    }

    // --- JPushAutoConfiguration ---

    @Test
    public void autoConfigurationBeansCreated() {
        new org.springframework.boot.test.context.runner.ApplicationContextRunner()
                .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(JPushAutoConfiguration.class))
                .withPropertyValues("jpush.app-key=" + VALID_KEY, "jpush.master-secret=" + VALID_SECRET)
                .run(context -> {
                    assertThat(context).hasSingleBean(JPushProperties.class);
                    assertThat(context).hasSingleBean(cn.jpush.api.JPushClient.class);
                    assertThat(context).hasSingleBean(JPushTemplate.class);
                    assertThat(context).hasSingleBean(cn.jiguang.common.ClientConfig.class);
                });
    }

    @Test
    public void autoConfigurationPropertiesValues() {
        new org.springframework.boot.test.context.runner.ApplicationContextRunner()
                .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(JPushAutoConfiguration.class))
                .withPropertyValues("jpush.app-key=" + VALID_KEY, "jpush.master-secret=" + VALID_SECRET, "jpush.production=false")
                .run(context -> {
                    JPushProperties props = context.getBean(JPushProperties.class);
                    assertThat(props.getAppKey()).isEqualTo(VALID_KEY);
                    assertThat(props.getMasterSecret()).isEqualTo(VALID_SECRET);
                    assertThat(props.isProduction()).isFalse();
                });
    }

    @Test
    public void autoConfigurationDirectProxyPath() {
        JPushAutoConfiguration config = new JPushAutoConfiguration();
        cn.jiguang.common.ClientConfig clientConfig = config.jPushClientConfig();
        assertThat(clientConfig).isNotNull();

        JPushProperties props = new JPushProperties();
        props.setAppKey(VALID_KEY);
        props.setMasterSecret(VALID_SECRET);
        props.setProxy(new cn.jiguang.common.connection.HttpProxy("127.0.0.1", 8080));

        cn.jpush.api.JPushClient client = config.jPushClient(props, clientConfig);
        assertThat(client).isNotNull();

        JPushTemplate template = config.jPushTemplate(client, props, clientConfig);
        assertThat(template).isNotNull();
    }

    @Test
    public void autoConfigurationDirectNoProxyPath() {
        JPushAutoConfiguration config = new JPushAutoConfiguration();
        cn.jiguang.common.ClientConfig clientConfig = config.jPushClientConfig();

        JPushProperties props = new JPushProperties();
        props.setAppKey(VALID_KEY);
        props.setMasterSecret(VALID_SECRET);

        cn.jpush.api.JPushClient client = config.jPushClient(props, clientConfig);
        assertThat(client).isNotNull();

        JPushTemplate template = config.jPushTemplate(client, props, clientConfig);
        assertThat(template).isNotNull();
    }

    @Test
    public void autoConfigurationDirectWithSlaves() {
        JPushAutoConfiguration config = new JPushAutoConfiguration();
        cn.jiguang.common.ClientConfig clientConfig = config.jPushClientConfig();

        JPushProperties props = new JPushProperties();
        props.setAppKey(VALID_KEY);
        props.setMasterSecret(VALID_SECRET);

        JPushProperties.JPushSlaveClientConfig slave = new JPushProperties.JPushSlaveClientConfig();
        slave.setAppId("slave1");
        slave.setAppKey(VALID_KEY);
        slave.setAppSecret(VALID_SECRET);
        props.getSlaves().add(slave);

        cn.jpush.api.JPushClient client = config.jPushClient(props, clientConfig);
        JPushTemplate template = config.jPushTemplate(client, props, clientConfig);
        assertThat(template).isNotNull();
        assertThat(template.getjPushClientMap()).hasSize(1);
    }

    @Test
    public void autoConfigurationDirectWithSlavesAndProxy() {
        JPushAutoConfiguration config = new JPushAutoConfiguration();
        cn.jiguang.common.ClientConfig clientConfig = config.jPushClientConfig();

        JPushProperties props = new JPushProperties();
        props.setAppKey(VALID_KEY);
        props.setMasterSecret(VALID_SECRET);

        JPushProperties.JPushSlaveClientConfig slave = new JPushProperties.JPushSlaveClientConfig();
        slave.setAppId("slave1");
        slave.setAppKey(VALID_KEY);
        slave.setAppSecret(VALID_SECRET);
        slave.setProxy(new cn.jiguang.common.connection.HttpProxy("127.0.0.1", 8080));
        props.getSlaves().add(slave);

        cn.jpush.api.JPushClient client = config.jPushClient(props, clientConfig);
        JPushTemplate template = config.jPushTemplate(client, props, clientConfig);
        assertThat(template).isNotNull();
        assertThat(template.getjPushClientMap()).hasSize(1);
    }

    @Test
    public void enableJPushAnnotation() {
        assertThat(EnableJPush.class.isAnnotation()).isTrue();
        assertThat(EnableJPush.class.getAnnotation(Deprecated.class)).isNotNull();
    }
}

# jpush-spring-boot-starter
Spring Boot Starter For JPush

### 说明


 > 基于 jpush-client 的 Spring Boot Starter 实现

1. 官网地址： https://www.jiguang.cn/push

### Maven

``` xml
<dependency>
	<groupId>com.github.hiwepy</groupId>
	<artifactId>jpush-spring-boot-starter</artifactId>
	<version>2.0.0.RELEASE</version>
</dependency>
```

### Sample

```java

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableJPush
@SpringBootApplication
public class Application {

	@Autowired
	JPushTemplate template;

	@PostConstruct
	public void test() throws IOException {
		PushObject pushObject = new PushObject();
		System.out.println(template.sendPush(pushObject));
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

}

```

yaml配置，参考如下：

```yaml
jpush:
  master-secret: zzz
  app-key: xxx
  production: false
```


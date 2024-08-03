package com.yechat.contacts;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;
import org.springframework.experimental.boot.test.context.DynamicProperty;

@TestConfiguration
public class UserContainerConfiguration {

    @Bean
    @DynamicProperty(name = "services.user.url", value = "'http://localhost:' + port")
    static CommonsExecWebServerFactoryBean messagesApiServer() {
        return CommonsExecWebServerFactoryBean.builder()
                .classpath((cp) -> cp.files("../users/build/libs/users-0.0.1-SNAPSHOT.jar"))
                .mainClass("com.yechat.users.UsersApplication");
    }
}

package com.yechat.contacts.configuration;

import com.yechat.contacts.user.RestUserClient;
import com.yechat.contacts.user.UserClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    public UserClient userClient(RestClient.Builder restClientBuilder) {
        return new RestUserClient(restClientBuilder);
    }

    @LoadBalanced
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}

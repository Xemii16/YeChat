package com.yechat.contacts.configuration;

import com.yechat.contacts.user.UserClient;
import com.yechat.contacts.user.WebUserClient;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public UserClient userClient(WebClient.Builder webClientBuild, ReactorLoadBalancerExchangeFilterFunction function) {
        return new WebUserClient(webClientBuild, function);
    }

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebProperties.Resources webProperties() {
        return new WebProperties.Resources();
    }
}

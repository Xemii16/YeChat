package com.yechat.notification.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(lbFunction);
    }
}

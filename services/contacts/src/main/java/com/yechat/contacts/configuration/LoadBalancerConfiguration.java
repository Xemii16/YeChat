package com.yechat.contacts.configuration;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/*@Configuration*/
public class LoadBalancerConfiguration {

    @Bean
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withWeighted()
                .withCaching()
                .build(context);
    }

    @Bean
    public LoadBalancerRequestTransformer transformer() {

        return new LoadBalancerRequestTransformer() {
            @Override
            public HttpRequest transformRequest(HttpRequest request, ServiceInstance instance) {
                return new HttpRequestWrapper(request) {

                    @Override
                    @NonNull
                    public HttpHeaders getHeaders() {
                        HttpHeaders headers = new HttpHeaders();
                        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                        headers.add("Authorization", "Bearer " + authentication.getToken().getTokenValue());
                        headers.putAll(super.getHeaders());
                        return headers;
                    }
                };
            }
        };
    }

}

package com.yechat.chats.user;

import com.yechat.chats.user.exception.UserNotFoundException;
import com.yechat.chats.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebUserClient implements UserClient{

    private final WebClient.Builder webClientBuilder;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @Override
    public Mono<UserResponse> getUser(Integer id) {
        return webClientBuilder
                .filter(lbFunction)
                .build()
                .get().uri("http://user-service/api/v1/users/" + id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        clientResponse -> Mono.error(new UserNotFoundException(id)))
                .bodyToMono(UserResponse.class);
    }
}

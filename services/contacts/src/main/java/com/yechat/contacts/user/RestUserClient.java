package com.yechat.contacts.user;

import com.yechat.contacts.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestUserClient implements UserClient {

    private final RestClient.Builder restClientBuilder;

    @Override
    public Optional<UserResponse> getUser(Integer id) {
        ResponseEntity<UserResponse> response = restClientBuilder.build()
                .get().uri("http://user-service/api/v1/users/" + id)
                .retrieve().toEntity(UserResponse.class);
        if (response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
            throw new UserNotFoundException("User not found with given id: " + id);
        }
        return Optional.ofNullable(response.getBody());
    }
}

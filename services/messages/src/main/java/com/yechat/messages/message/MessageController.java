package com.yechat.messages.message;

import com.yechat.messages.message.request.MessageRequest;
import com.yechat.messages.message.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Profile("!testing")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<MessageResponse> sendMessage(@RequestBody MessageRequest request, @AuthenticationPrincipal Jwt jwt) {
        return messageService.sendMessage(request, jwt);
    }

    @GetMapping("/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MessageResponse> getMessage(
            @PathVariable UUID chatId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return messageService.getMessage(chatId, jwt);
    }
}

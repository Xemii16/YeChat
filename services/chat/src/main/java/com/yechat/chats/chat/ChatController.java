package com.yechat.chats.chat;

import com.yechat.chats.chat.request.ChatRequest;
import com.yechat.chats.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Profile("!testing")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ChatResponse> createChat(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return chatService.createChat(request, jwt);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ChatResponse> getChats(@AuthenticationPrincipal Jwt jwt) {
        return chatService.getChats(jwt);
    }

    @GetMapping("/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ChatResponse> getChat(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("chatId") UUID chatId
    ) {
        return chatService.getChat(chatId, jwt);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteChat(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer receiverId,
            @RequestParam(name = "all", defaultValue = "false") boolean deleteAll
    ) {
        return chatService.deleteChat(receiverId, jwt, deleteAll);
    }
}

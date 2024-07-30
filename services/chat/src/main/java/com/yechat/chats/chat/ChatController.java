package com.yechat.chats.chat;

import com.yechat.chats.chat.exception.ChatAlreadyExistsException;
import com.yechat.chats.chat.request.ChatRequest;
import com.yechat.chats.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> createChat(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) throws ChatAlreadyExistsException {
        return ResponseEntity.ok(chatService.createChat(request, jwt));
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> getChats(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(chatService.getChats(jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer receiverId,
            @RequestParam(name = "all", defaultValue = "false") boolean deleteAll
    ) {
        chatService.deleteChat(receiverId, jwt, deleteAll);
        return ResponseEntity.noContent().build();
    }
}

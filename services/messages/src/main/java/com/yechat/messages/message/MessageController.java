package com.yechat.messages.message;

import com.yechat.messages.message.request.MessageRequest;
import com.yechat.messages.message.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(messageService.sendMessage(request, jwt));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<List<MessageResponse>> getMessage(
            @PathVariable String chatId,
            @RequestParam(name = "page", defaultValue = "-1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(messageService.getMessage(chatId, jwt, page, size));
    }
}

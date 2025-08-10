package com.chatbox.controller;

import com.chatbox.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.UUID;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    /**
     * Trả về trang chat (templates/index.html)
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Endpoint REST đơn giản để kiểm tra sống/chết.
     */
    @GetMapping("/api/health")
    @ResponseBody
    public String health() {
        return "OK";
    }

    /**
     * Client gửi tin nhắn tới /app/chat.sendMessage (theo prefix /app trong WebSocketConfig)
     * và server broadcast ra /topic/public
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage send(@Payload ChatMessage message) {
        // Chuẩn hoá & bổ sung trường nếu thiếu
        if (message.getId() == null || message.getId().isBlank()) {
            message.setId(UUID.randomUUID().toString());
        }
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now());
        }
        if (message.getFrom() == null || message.getFrom().trim().isEmpty()) {
            message.setFrom("Anonymous");
        } else {
            message.setFrom(message.getFrom().trim());
        }
        if (message.getContent() != null) {
            message.setContent(message.getContent().trim());
        }

        log.info("New message from={} id={} content={}", message.getFrom(), message.getId(), message.getContent());
        return message; // Trả về để broker phát tới mọi subscriber /topic/public
    }

    /**
     * Bắt lỗi trong quá trình xử lý message và bắn thông báo ngắn gọn.
     * Bạn có thể subscribe thêm topic /topic/errors ở client nếu muốn hiện lỗi realtime.
     */
    @MessageExceptionHandler
    @SendTo("/topic/errors")
    public String handleException(Exception ex) {
        log.error("WebSocket error:", ex);
        return "Lỗi xử lý tin nhắn: " + ex.getMessage();
    }
}

package com.example.socketserver.controller;

import com.example.socketserver.model.Message;
import com.example.socketserver.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MessageController {
    private final MessageService messageService;

    // Mapped as /app/all
    @MessageMapping("/all")
    @SendTo("/all/messages")
    public Message sendAll(final Message message) throws Exception {
        return message;
    }

    // Mapped as /app/private
    @MessageMapping("/private")
    public void sendToSpecificUser(SimpMessageHeaderAccessor sha, @Payload Message message) {
        log.info("Header socket user info: " + sha.getUser());
        messageService.sendToSpecificUser(message.getTo(), message);
    }
}
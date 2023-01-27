package com.intheloop.chat.web.websocket;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.Message;
import com.intheloop.chat.domain.User;
import com.intheloop.chat.service.ConversationService;
import com.intheloop.chat.service.UserService;
import com.intheloop.chat.service.dto.MessageDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ChatController {
    private final ConversationService conversationService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(
            ConversationService conversationService,
            UserService userService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.conversationService = conversationService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(
            SimpMessageHeaderAccessor header,
            @DestinationVariable("conversationId") Long conversationId,
            @Payload MessageDTO messageDTO
    ) {
        if (header.getUser() == null || header.getUser().getName() == null)
            return;
        User sender;
        try {
            sender = userService.loadUserByUsername(header.getUser().getName());
        } catch (Exception e) {
            return;
        }

        Optional<Conversation> conversation = conversationService
                .getConversation(conversationId);
        if (conversation.isEmpty())
            return;

        Message message = new Message();
        message.setUser(sender);
        message.setType(messageDTO.getType());
        message.setContent(messageDTO.getContent());
        message = conversationService.addMessage(conversation.get(), message);
        messagingTemplate.convertAndSend("/topic/" + String.valueOf(conversationId), new MessageDTO(message));
    }
}

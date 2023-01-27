package com.intheloop.chat.service.dto;

import com.intheloop.chat.domain.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class MessageDTO {
    private Long id;
    private Message.Type type;
    private String content;
    private LocalDateTime createdAt;
    private Long senderId;
    private Long conversationId;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.type = message.getType();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
        this.senderId = message.getUser().getId();
        this.conversationId = message.getConversation().getId();
    }
}

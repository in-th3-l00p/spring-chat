package com.intheloop.chat.service.dto;

import com.intheloop.chat.domain.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MessageDTO {
    private Long id;
    private Message.Type type;
    private String content;
    private Long senderId;
    private Long conversationId;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.type = message.getType();
        this.content = message.getContent();
        this.senderId = message.getUser().getId();
        this.conversationId = message.getConversation().getId();
    }
}

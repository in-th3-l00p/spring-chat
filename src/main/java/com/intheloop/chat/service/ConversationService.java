package com.intheloop.chat.service;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.Message;
import com.intheloop.chat.domain.User;
import com.intheloop.chat.repository.ConversationRepository;
import com.intheloop.chat.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public void createConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findAllByUsersContains(user);
    }

    public Optional<Conversation> getConversation(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    public Message addMessage(Conversation conversation, Message message) {
        message.setConversation(conversation);
        message = messageRepository.save(message);
        Set<Message> messages = conversation.getMessages();
        messages.add(message);
        conversation.setMessages(messages);
        conversationRepository.save(conversation);
        return message;
    }
}

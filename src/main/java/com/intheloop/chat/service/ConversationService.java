package com.intheloop.chat.service;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.User;
import com.intheloop.chat.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public void createConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findAllByUsersContains(user);
    }
}

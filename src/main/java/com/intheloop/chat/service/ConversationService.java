package com.intheloop.chat.service;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.Message;
import com.intheloop.chat.domain.User;
import com.intheloop.chat.repository.ConversationRepository;
import com.intheloop.chat.repository.MessageRepository;
import org.springframework.data.domain.Sort;
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

    public List<Message> getSortedMessages(Conversation conversation) {
        return messageRepository
                .findMessagesByConversationIs(conversation, Sort.by("createdAt"));
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

    private void deleteConversation(Conversation conversation) {
        messageRepository.deleteAll(conversation.getMessages());
        conversationRepository.delete(conversation);
    }

    public void addUser(Conversation conversation, User user) {
        if (conversation.getUsers().contains(user))
            return;
        Set<User> users = conversation.getUsers();
        users.add(user);
        conversation.setUsers(users);
        conversationRepository.save(conversation);
    }

    public void removeUser(Conversation conversation, User user) {
        if (!conversation.getUsers().contains(user))
            return;
        Set<User> users = conversation.getUsers();
        users.remove(user);
        if (users.size() == 0) {
            deleteConversation(conversation);
            return;
        }
        conversation.setUsers(users);
        conversationRepository.save(conversation);
    }
}

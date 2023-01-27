package com.intheloop.chat.repository;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MessageRepository extends
        PagingAndSortingRepository<Message, Long>,
        CrudRepository<Message, Long> {
    List<Message> findMessagesByConversationIs(Conversation conversation, Sort sort);
}

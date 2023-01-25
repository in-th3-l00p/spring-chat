package com.intheloop.chat.repository;

import com.intheloop.chat.domain.Conversation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConversationRepository extends
        PagingAndSortingRepository<Conversation, Long>,
        CrudRepository<Conversation, Long> {
}

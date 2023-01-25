package com.intheloop.chat.repository;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ConversationRepository extends
        PagingAndSortingRepository<Conversation, Long>,
        CrudRepository<Conversation, Long> {
    List<Conversation> findAllByUsersContains(User user);
}

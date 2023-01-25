package com.intheloop.chat.repository;

import com.intheloop.chat.domain.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageRepository extends
        PagingAndSortingRepository<Message, Long>,
        CrudRepository<Message, Long> {
}

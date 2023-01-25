package com.intheloop.chat.web;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.Message;
import com.intheloop.chat.domain.User;
import com.intheloop.chat.service.ConversationService;
import com.intheloop.chat.service.UserService;
import com.intheloop.chat.service.dto.MessageDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/conversation")
public class ConversationController {
    private final ConversationService conversationService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ConversationController(
            ConversationService conversationService,
            UserService userService,
            SimpMessagingTemplate simpMessagingTemplate
    ) {
        this.conversationService = conversationService;
        this.userService = userService;
        this.messagingTemplate = simpMessagingTemplate;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    private static class ConversationForm {
        private String name;
    }

    @GetMapping("/{conversationId}")
    public String conversationPage(
            Model model,
            Authentication authentication,
            @PathVariable("conversationId") Long conversationId
    ) {
        Optional<Conversation> conversation = conversationService.getConversation(conversationId);
        if (conversation.isEmpty())
            return "error/conversationNotFound";
        User user;
        try {
            user = userService.loadUserByUsername(authentication.getName());
        } catch (Exception exception) {
            return "redirect:/error";
        }
        if (!conversation.get().getUsers().contains(user))
            return "error/conversationNotAllowed";
        model.addAttribute("currentUser", user);
        model.addAttribute("conversation", conversation.get());
        return "conversation";
    }

    @GetMapping("/list")
    public String conversationsPage(Model model, Authentication authentication) {
        try {
            User user = userService.loadUserByUsername(authentication.getName());
            model.addAttribute("conversations", conversationService.getUserConversations(user));
        } catch (UsernameNotFoundException e) {
            return "redirect:/error";
        }

        return "conversations";
    }

    @GetMapping("/create")
    public String createConversationPage(Model model) {
        model.addAttribute("conversationForm", new ConversationForm());
        return "createConversation";
    }

    @PostMapping("/create")
    public String createConversation(
            @ModelAttribute ConversationForm conversationForm,
            Authentication authentication
    ) {
        try {
            User user = userService.loadUserByUsername(authentication.getName());
            Set<User> users = new HashSet<>();
            users.add(user);

            Conversation conversation = new Conversation();
            conversation.setName(conversationForm.getName());
            conversation.setUsers(users);
            conversationService.createConversation(conversation);
        } catch (Exception e) {
            return "redirect:/error";
        }

        return "redirect:/conversation/list";
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
        if (conversation.isEmpty() || !conversation.get().getUsers().contains(sender))
            return;

        Message message = new Message();
        message.setUser(sender);
        message.setType(messageDTO.getType());
        message.setContent(messageDTO.getContent());
        message = conversationService.addMessage(conversation.get(), message);
        messagingTemplate.convertAndSend("/topic/" + conversationId, new MessageDTO(message));
    }
}

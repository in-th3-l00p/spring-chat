package com.intheloop.chat.web;

import com.intheloop.chat.domain.Conversation;
import com.intheloop.chat.domain.User;
import com.intheloop.chat.service.ConversationService;
import com.intheloop.chat.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/conversations")
public class ConversationController {
    private final ConversationService conversationService;
    private final UserService userService;

    public ConversationController(
            ConversationService conversationService,
            UserService userService
    ) {
        this.conversationService = conversationService;
        this.userService = userService;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    private static class ConversationForm {
        private String name;
    }

    @GetMapping
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

        return "redirect:/conversations";
    }
}

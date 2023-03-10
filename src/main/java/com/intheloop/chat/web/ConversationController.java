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
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/conversation")
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

    @NoArgsConstructor
    @Getter
    @Setter
    private static class AddUserForm {
        private String username;
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
            return "redirect:/serverError";
        }
        if (!conversation.get().getUsers().contains(user))
            return "error/conversationNotAllowed";
        model.addAttribute("currentUser", user);
        model.addAttribute("conversation", conversation.get());
        model.addAttribute("messages", conversationService.getSortedMessages(conversation.get()));
        return "conversation";
    }

    @GetMapping("/list")
    public String conversationsPage(Model model, Authentication authentication) {
        try {
            User user = userService.loadUserByUsername(authentication.getName());
            model.addAttribute("conversations", conversationService.getUserConversations(user));
        } catch (UsernameNotFoundException e) {
            return "redirect:/serverError";
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
            return "redirect:/conversation/list?exists";
        }

        return "redirect:/conversation/list";
    }

    @GetMapping("/add/{conversationId}")
    public String addUserToConversationPage(
            @PathVariable("conversationId") Long conversationId,
            Model model
    ) {
        Optional<Conversation> conversation = conversationService.getConversation(conversationId);
        if (conversation.isEmpty())
            return "error/conversationNotFound";
        model.addAttribute("conversation", conversation.get());
        model.addAttribute("addUserForm", new AddUserForm());
        return "conversationAddUser";
    }

    @PostMapping("/add/{conversationId}")
    public String addUserToConversation(
            @PathVariable("conversationId") Long conversationId,
            @ModelAttribute AddUserForm addUserForm,
            Authentication authentication
    ) {
        if (authentication.getName().equals(addUserForm.getUsername()))
            return "redirect:/conversation/" + conversationId;
        User currentUser, addedUser;
        try {
            currentUser = userService.loadUserByUsername(authentication.getName());
        } catch (Exception e) {
            return "redirect:/serverError";
        }
        try {
            addedUser = userService.loadUserByUsername(addUserForm.getUsername());
        } catch (Exception e) {
            return String.format("redirect:/add/%s?userNotFound", conversationId);
        }
        Optional<Conversation> conversation = conversationService.getConversation(conversationId);
        if (conversation.isEmpty())
            return "error/conversationNotFound";
        if (!conversation.get().getUsers().contains(currentUser))
            return "error/conversationNotAllowed";

        conversationService.addUser(conversation.get(), addedUser);
        return "redirect:/conversation/" + conversationId;
    }

    @PostMapping("/leave/{conversationId}")
    public String leaveConversation(
            @PathVariable("conversationId") Long conversationId,
            Authentication authentication
    ) {
        User user;
        try {
            user = userService.loadUserByUsername(authentication.getName());
        } catch (Exception e) {
            return "redirect:/serverError";
        }

        Optional<Conversation> conversation = conversationService.getConversation(conversationId);
        if (conversation.isEmpty())
            return "error/conversationNotFound";
        if (!conversation.get().getUsers().contains(user))
            return "error/conversationNotAllowed";

        conversationService.removeUser(conversation.get(), user);
        return "redirect:/conversation/list";
    }
}

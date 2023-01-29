package com.intheloop.chat.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeneralRouteController {
    @GetMapping("/")
    public String indexPage() {
        return "redirect:/conversation/list";
    }

    @GetMapping("/serverError")
    public String errorPage() {
        return "error/error";
    }
}

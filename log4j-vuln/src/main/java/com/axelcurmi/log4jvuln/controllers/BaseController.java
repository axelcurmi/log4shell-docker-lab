package com.axelcurmi.log4jvuln.controllers;

import com.axelcurmi.log4jvuln.models.LoginInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class BaseController {
    private static final Logger logger = LogManager.getLogger("HelloWorld");

    @GetMapping("/")
    public ModelAndView index(@RequestParam(required = false) String error) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("loginInput", new LoginInput());
        mv.addObject("error", error);
        mv.setViewName("index");
        return mv;
    }

    @PostMapping("/login")
    public RedirectView login(@ModelAttribute LoginInput loginInput) {
        logger.info("Incorrect login attempt for username '{}'", loginInput.getUsername());
        return new RedirectView("/?error=Incorrect+username+or+password");
    }
}

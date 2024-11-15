package com.lotte4.controller.pagecontroller;

import com.lotte4.service.TermsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Controller
public class PolicyController {
    private final TermsService termsService;

    @GetMapping("/policy/buyer")
    public String buyer(Model model) {
        List<String[]> terms =  termsService.selectTerm(1);
        model.addAttribute("terms", terms);
        return "/policy/buyer";
    }

    @GetMapping("/policy/seller")
    public String seller(Model model) {
        List<String[]> terms =  termsService.selectTerm(2);
        model.addAttribute("terms", terms);
        return "/policy/seller";
    }

    @GetMapping("/policy/finance")
    public String finance(Model model) {
        List<String[]> terms =  termsService.selectTerm(3);
        model.addAttribute("terms", terms);
        return "/policy/finance";
    }

    @GetMapping("/policy/location")
    public String location(Model model) {
        List<String[]> terms =  termsService.selectTerm(4);
        model.addAttribute("terms", terms);
        return "/policy/location";
    }

    @GetMapping("/policy/privacy")
    public String privacy(Model model) {
        List<String[]> terms =  termsService.selectTerm(5);
        model.addAttribute("terms", terms);
        return "/policy/privacy";

    }


}

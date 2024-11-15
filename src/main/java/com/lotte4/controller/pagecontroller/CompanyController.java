package com.lotte4.controller.pagecontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyController {

    @GetMapping("/company/index")
    public String companyIndex(){
        return "/company/index";
    }

    @GetMapping("/company/culture")
    public String companyCulture(){
        return "/company/culture";
    }

    @GetMapping("/company/story")
    public String companyStory(){
        return "/company/story";
    }

    @GetMapping("/company/media")
    public String companyMedia(){
        return "/company/media";
    }



}

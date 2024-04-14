package com.test.musicrecorder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    MusicRecorder mr = new MusicRecorder();

    @RequestMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        if (mr.validatelogin(username, password) == false) {

            String errorMsg = "invalid login dumbass";
            model.addAttribute("errorMsg", errorMsg);

            return "login";
        } else {

            // Add attributes to pass data to the next page
            model.addAttribute("username", username);
            model.addAttribute("password", password);

            // Redirect to a new page
            return "redirect:/welcome";

        }

    }

    @RequestMapping("/welcome")
    public String welcomePage(Model model) {

        model.addAttribute("username", mr.getUsername());
        model.addAttribute("password", mr.getPassword());

        return "welcome";
    }
}
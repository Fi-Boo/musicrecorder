package com.cca2.musiclibrary;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    MusicLibrary mr = new MusicLibrary();

    @RequestMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")

    public String processLogin(@RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        if (mr.checkLoginCredentials(email, password) == false) {

            String errorMsg = "email or password is invalid";
            model.addAttribute("errorMsg", errorMsg);

            return "login";
        } else {

            // Redirect to a new page
            return "redirect:/main";

        }

    }

    @RequestMapping("/main")
    public String welcomePage(Model model) {

        model.addAttribute("username", mr.getLoggedUsername());
        model.addAttribute("artists", mr.getArtists());

        return "main";
    }

    @RequestMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam("email") String email,
            @RequestParam("password") String password, @RequestParam("username") String username,
            Model model) {

        String errorMsg;

        if (mr.checkEmailExists(email)) {

            errorMsg = "The email already exists";
            model.addAttribute("errorMsg", errorMsg);

        } else if (email.isBlank() || password.isBlank() || username.isBlank()) {

            errorMsg = "cannot have blank values";
            model.addAttribute("errorMsg", errorMsg);

        } else {

            mr.addUser(email, username, password);

            return "login";

        }

        return "register";
    }

    @PostMapping("/query")
    public String query(@RequestParam(required = false) String title, @RequestParam(required = false) String year,
            @RequestParam(required = false) String selectedArtist, Model model) {

        List<Song> songs = mr.queryFor(title, year, selectedArtist);

        model.addAttribute("username", mr.getLoggedUsername());
        model.addAttribute("artists", mr.getArtists());

        if (songs.size() == 0) {

            String errorMsg = "No result is retrieved. Please query again";
            model.addAttribute("errorMsg", errorMsg);

        } else {

            model.addAttribute("queryResults", songs);
        }

        return "main";
    }

    @PostMapping("/logout")
    public String logout() {

        mr.logout();

        return "redirect:/";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("subTitle") String subTitle) {

        mr.addToSubscribeList(subTitle);

        return "main";
    }
}
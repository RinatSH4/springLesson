package com.example.shop.controllers;

import com.example.shop.models.Item;
import com.example.shop.models.Role;
import com.example.shop.models.User;
import com.example.shop.repo.ItemRepository;
import com.example.shop.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/user")
    public String user(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/adminpanel")
    public String adminpanel(Model model) {
        Iterable<User> user = userRepository.findAll();
        model.addAttribute("users", user);
        return "adminpanel";
    }

    @GetMapping("/adminpanel/user/{id}")
    public String itemsUser(@PathVariable(value = "id") long user_id, Model model) {
        User user = userRepository.findById(user_id).orElse(new User());
        Iterable<Item> items = user.getItems();
        model.addAttribute("user", user);
        model.addAttribute("items", items);
        return "itemsUser";
    }

    @GetMapping("/reg")
    public String reg(@RequestParam (name = "error", defaultValue = "", required = false) String error,
            Model model) {
        if (error.equals("username"))
            model.addAttribute("error", "логин занят");
        else if (error.equals("email"))
            model.addAttribute("error", "email занят");

        return "reg";
    }


    @PostMapping("/reg")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password) {
        if (userRepository.findByUsername(username) != null)
            return "redirect:/reg?error=username";
        else if (userRepository.findByEmail(email) != null)
            return "redirect:/reg?error=email";

        password = passwordEncoder.encode(password);
        User user = new User(username, password, email, true, Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }

    @PostMapping("/user/update")
    public String updateUser(Principal principal, User userForm) {
        User user = userRepository.findByUsername(principal.getName());
        user.setUsername(userForm.getUsername());
        user.setEmail(userForm.getEmail());
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setRoles(userForm.getRoles());
        userRepository.save(user);
        return "redirect:/user";
    }
}

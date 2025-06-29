package com.skillswap.controller;

import com.skillswap.entity.User;
import com.skillswap.service.UserService;
import com.skillswap.service.RequestService;
import com.skillswap.entity.Request;
import com.skillswap.service.OfferService;
import com.skillswap.entity.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RequestService requestService;
    private final OfferService offerService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam String password, Model model) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "register";
        }
        userService.registerUser(user, password);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        // Requests for offers owned by this user (as service provider)
        model.addAttribute("offerRequests", requestService.getRequestsForOffersOwnedBy(user));
        // Requests placed by this user (as service requester)
        model.addAttribute("myRequests", requestService.getRequestsByUser(user));
        // Add request service to model for executor lookup
        model.addAttribute("requestService", requestService);
        return "profile";
    }

    @PostMapping("/requests/{id}/status")
    public String changeRequestStatus(@PathVariable Long id, @RequestParam String status, Principal principal) {
        Request req = requestService.getRequestById(id).orElse(null);
        if (req == null) return "redirect:/profile";
        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        // Check if current user is offer owner or admin
        boolean isOwner = false;
        for (Offer offer : offerService.getOffersByUser(currentUser)) {
            if (offer.getSkill().getId().equals(req.getSkill().getId())) {
                isOwner = true;
                break;
            }
        }
        if (currentUser != null && ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || isOwner)) {
            req.setStatus(status);
            requestService.saveRequest(req);
        }
        return "redirect:/profile";
    }

    @PostMapping("/requests/{id}/delete")
    public String deleteRequest(@PathVariable Long id, Principal principal) {
        Request req = requestService.getRequestById(id).orElse(null);
        if (req == null) return "redirect:/profile";
        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        boolean isOwner = false;
        for (Offer offer : offerService.getOffersByUser(currentUser)) {
            if (offer.getSkill().getId().equals(req.getSkill().getId())) {
                isOwner = true;
                break;
            }
        }
        if (currentUser != null && ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || isOwner)) {
            requestService.deleteRequest(id);
        }
        return "redirect:/profile";
    }

} 
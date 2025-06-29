package com.skillswap.controller;

import com.skillswap.entity.Offer;
import com.skillswap.entity.User;
import com.skillswap.service.OfferService;
import com.skillswap.service.UserService;
import com.skillswap.service.SkillService;
import com.skillswap.service.RequestService;
import com.skillswap.service.CurrencyService;
import com.skillswap.entity.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/offers")
public class OfferController {
    private final OfferService offerService;
    private final UserService userService;
    private final SkillService skillService;
    private final RequestService requestService;
    private final CurrencyService currencyService;

    @GetMapping
    public String listOffers(Model model, Principal principal,
                           @RequestParam(required = false) String search,
                           @RequestParam(required = false) Long skillId,
                           @RequestParam(required = false) String minPrice,
                           @RequestParam(required = false) String maxPrice,
                           @RequestParam(required = false) String currency) {
        
        List<Offer> offers = offerService.searchOffers(search, skillId, minPrice, maxPrice, currency);
        model.addAttribute("offers", offers);
        model.addAttribute("skills", skillService.getAllSkills());
        model.addAttribute("currentUsername", principal != null ? principal.getName() : null);
        model.addAttribute("search", search);
        model.addAttribute("selectedSkillId", skillId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedCurrency", currency);
        
        // Add currency rates for conversion
        Double usdRate = currencyService.getRubToUsdRate();
        Double eurRate = currencyService.getRubToEurRate();
        model.addAttribute("usdRate", usdRate);
        model.addAttribute("eurRate", eurRate);
        
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("currentUserRole", user != null ? user.getRole() : null);
        } else {
            model.addAttribute("currentUserRole", null);
        }
        return "offers/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("offer", new Offer());
        model.addAttribute("skills", skillService.getAllSkills());
        return "offers/create";
    }

    @PostMapping
    public String createOffer(@ModelAttribute Offer offer, @RequestParam Long skillId, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        offer.setUser(user);
        offer.setSkill(skillService.getSkillById(skillId).orElse(null));
        offer.setCreatedAt(LocalDateTime.now());
        offer.setIsActive(true);
        offerService.saveOffer(offer);
        return "redirect:/offers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Offer offer = offerService.getOfferById(id).orElse(null);
        if (offer == null) {
            return "redirect:/offers";
        }
        
        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null || (!"ADMIN".equalsIgnoreCase(currentUser.getRole()) && 
            !offer.getUser().getId().equals(currentUser.getId()))) {
            return "redirect:/offers";
        }
        
        model.addAttribute("offer", offer);
        model.addAttribute("skills", skillService.getAllSkills());
        return "offers/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateOffer(@PathVariable Long id, @ModelAttribute Offer offer, 
                            @RequestParam Long skillId, Principal principal) {
        Offer existingOffer = offerService.getOfferById(id).orElse(null);
        if (existingOffer == null) {
            return "redirect:/offers";
        }
        
        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null || (!"ADMIN".equalsIgnoreCase(currentUser.getRole()) && 
            !existingOffer.getUser().getId().equals(currentUser.getId()))) {
            return "redirect:/offers";
        }
        
        existingOffer.setTitle(offer.getTitle());
        existingOffer.setDescription(offer.getDescription());
        existingOffer.setPrice(offer.getPrice());
        existingOffer.setCurrency(offer.getCurrency());
        existingOffer.setSkill(skillService.getSkillById(skillId).orElse(null));
        
        offerService.saveOffer(existingOffer);
        return "redirect:/offers";
    }

    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleOfferStatus(@PathVariable Long id, Principal principal) {
        Offer offer = offerService.getOfferById(id).orElse(null);
        if (offer == null) {
            return ResponseEntity.notFound().build();
        }
        
        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null || (!"ADMIN".equalsIgnoreCase(currentUser.getRole()) && 
            !offer.getUser().getId().equals(currentUser.getId()))) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        
        offer.setIsActive(!offer.getIsActive());
        offerService.saveOffer(offer);
        return ResponseEntity.ok(offer.getIsActive() ? "activated" : "deactivated");
    }

    @GetMapping("/{id}")
    public String viewOffer(@PathVariable Long id, Model model, Principal principal) {
        offerService.getOfferById(id).ifPresent(o -> model.addAttribute("offer", o));
        model.addAttribute("currentUsername", principal != null ? principal.getName() : null);
        
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("currentUserRole", user != null ? user.getRole() : null);
        } else {
            model.addAttribute("currentUserRole", null);
        }
        
        return "offers/view";
    }

    @PostMapping("/{id}/delete")
    public Object deleteOffer(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        Offer offer = offerService.getOfferById(id).orElse(null);
        if (offer == null) {
            if (isAjax(request)) return ResponseEntity.notFound().build();
            return "redirect:/offers";
        }
        User currentUser = userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser != null && ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || offer.getUser().getId().equals(currentUser.getId()))) {
            offerService.deleteOffer(id);
            if (isAjax(request)) return ResponseEntity.ok().build();
        } else {
            if (isAjax(request)) return ResponseEntity.status(403).build();
        }
        return "redirect:/offers";
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @PostMapping("/{id}/order")
    public String orderOffer(@PathVariable Long id, Principal principal, Model model) {
        Offer offer = offerService.getOfferById(id).orElse(null);
        if (offer == null) return "redirect:/offers";
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null || offer.getUser().getId().equals(user.getId())) {
            return "redirect:/offers/" + id;
        }
        Request request = Request.builder()
            .user(user)
            .skill(offer.getSkill())
            .title("Order for: " + offer.getTitle())
            .description("Request for offer: " + offer.getTitle())
            .createdAt(LocalDateTime.now())
            .status("NEW")
            .build();
        requestService.saveRequest(request);
        model.addAttribute("success", "Заявка отправлена!");
        return "redirect:/offers/" + id;
    }
} 
package com.skillswap.controller.api;

import com.skillswap.entity.Offer;
import com.skillswap.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OfferRestController {
    private final OfferService offerService;

    @GetMapping
    public List<OfferDTO> getAllOffers() {
        return offerService.getAllOffers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable Long id) {
        return offerService.getOfferById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OfferDTO createOffer(@RequestBody CreateOfferRequest request) {
        Offer offer = new Offer();
        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setPrice(request.getPrice());
        offer.setCurrency(request.getCurrency());
        offer.setCreatedAt(LocalDateTime.now());
        offer.setIsActive(true);
        
        Offer savedOffer = offerService.saveOffer(offer);
        return convertToDTO(savedOffer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable Long id, @RequestBody UpdateOfferRequest request) {
        return offerService.getOfferById(id)
                .map(existing -> {
                    existing.setTitle(request.getTitle());
                    existing.setDescription(request.getDescription());
                    existing.setPrice(request.getPrice());
                    existing.setCurrency(request.getCurrency());
                    
                    Offer savedOffer = offerService.saveOffer(existing);
                    return ResponseEntity.ok(convertToDTO(savedOffer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleOfferStatus(@PathVariable Long id) {
        return offerService.getOfferById(id)
                .map(offer -> {
                    offer.setIsActive(!offer.getIsActive());
                    offerService.saveOffer(offer);
                    return ResponseEntity.ok(offer.getIsActive() ? "activated" : "deactivated");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private OfferDTO convertToDTO(Offer offer) {
        return OfferDTO.builder()
                .id(offer.getId())
                .title(offer.getTitle())
                .description(offer.getDescription())
                .price(offer.getPrice())
                .currency(offer.getCurrency())
                .isActive(offer.getIsActive())
                .createdAt(offer.getCreatedAt())
                .userId(offer.getUser() != null ? offer.getUser().getId() : null)
                .username(offer.getUser() != null ? offer.getUser().getUsername() : null)
                .skillId(offer.getSkill() != null ? offer.getSkill().getId() : null)
                .skillName(offer.getSkill() != null ? offer.getSkill().getName() : null)
                .build();
    }

    // DTOs
    public static class OfferDTO {
        private Long id;
        private String title;
        private String description;
        private BigDecimal price;
        private String currency;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private Long userId;
        private String username;
        private Long skillId;
        private String skillName;

        // Builder pattern
        public static OfferDTOBuilder builder() {
            return new OfferDTOBuilder();
        }

        public static class OfferDTOBuilder {
            private OfferDTO dto = new OfferDTO();

            public OfferDTOBuilder id(Long id) { dto.id = id; return this; }
            public OfferDTOBuilder title(String title) { dto.title = title; return this; }
            public OfferDTOBuilder description(String description) { dto.description = description; return this; }
            public OfferDTOBuilder price(BigDecimal price) { dto.price = price; return this; }
            public OfferDTOBuilder currency(String currency) { dto.currency = currency; return this; }
            public OfferDTOBuilder isActive(Boolean isActive) { dto.isActive = isActive; return this; }
            public OfferDTOBuilder createdAt(LocalDateTime createdAt) { dto.createdAt = createdAt; return this; }
            public OfferDTOBuilder userId(Long userId) { dto.userId = userId; return this; }
            public OfferDTOBuilder username(String username) { dto.username = username; return this; }
            public OfferDTOBuilder skillId(Long skillId) { dto.skillId = skillId; return this; }
            public OfferDTOBuilder skillName(String skillName) { dto.skillName = skillName; return this; }
            public OfferDTO build() { return dto; }
        }

        // Getters
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public BigDecimal getPrice() { return price; }
        public String getCurrency() { return currency; }
        public Boolean getIsActive() { return isActive; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public Long getSkillId() { return skillId; }
        public String getSkillName() { return skillName; }
    }

    public static class CreateOfferRequest {
        private String title;
        private String description;
        private BigDecimal price;
        private String currency;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    public static class UpdateOfferRequest {
        private String title;
        private String description;
        private BigDecimal price;
        private String currency;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
} 
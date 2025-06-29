package com.skillswap.service;

import com.skillswap.entity.Offer;
import com.skillswap.entity.User;
import com.skillswap.entity.Skill;
import com.skillswap.repository.OfferRepository;
import com.skillswap.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;
    private final SkillRepository skillRepository;

    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    public List<Offer> getOffersByUser(User user) {
        return offerRepository.findByUser(user);
    }

    public Optional<Offer> getOfferById(Long id) {
        return offerRepository.findById(id);
    }

    public Offer saveOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    public List<Offer> searchOffers(String search, Long skillId, String minPrice, String maxPrice, String currency) {
        List<Offer> offers = offerRepository.findAll();
        
        return offers.stream()
            .filter(offer -> {
                // Filter by search term
                if (search != null && !search.trim().isEmpty()) {
                    String searchLower = search.toLowerCase();
                    boolean matchesSearch = (offer.getTitle() != null && offer.getTitle().toLowerCase().contains(searchLower)) ||
                                          (offer.getDescription() != null && offer.getDescription().toLowerCase().contains(searchLower));
                    if (!matchesSearch) return false;
                }
                
                // Filter by skill
                if (skillId != null) {
                    if (offer.getSkill() == null || !offer.getSkill().getId().equals(skillId)) {
                        return false;
                    }
                }
                
                // Filter by price range
                if (minPrice != null && !minPrice.trim().isEmpty()) {
                    try {
                        BigDecimal min = new BigDecimal(minPrice);
                        if (offer.getPrice() == null || offer.getPrice().compareTo(min) < 0) {
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        // Invalid number, skip this filter
                    }
                }
                
                if (maxPrice != null && !maxPrice.trim().isEmpty()) {
                    try {
                        BigDecimal max = new BigDecimal(maxPrice);
                        if (offer.getPrice() == null || offer.getPrice().compareTo(max) > 0) {
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        // Invalid number, skip this filter
                    }
                }
                
                // Filter by currency
                if (currency != null && !currency.trim().isEmpty()) {
                    if (offer.getCurrency() == null || !offer.getCurrency().equals(currency)) {
                        return false;
                    }
                }
                
                return true;
            })
            .toList();
    }
} 
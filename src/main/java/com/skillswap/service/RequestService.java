package com.skillswap.service;

import com.skillswap.entity.Request;
import com.skillswap.entity.User;
import com.skillswap.entity.Offer;
import com.skillswap.repository.RequestRepository;
import com.skillswap.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final OfferRepository offerRepository;

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<Request> getRequestsByUser(User user) {
        return requestRepository.findByUser(user);
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public Request saveRequest(Request request) {
        return requestRepository.save(request);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

    public List<Request> getRequestsForOffersOwnedBy(User owner) {
        return requestRepository.findRequestsForOffersOwnedBy(owner.getId());
    }

    public String getExecutorsForRequest(Request request) {
        if (request.getSkill() == null) {
            return "—";
        }
        
        List<Offer> activeOffers = offerRepository.findActiveOffersBySkillId(request.getSkill().getId());
        if (activeOffers.isEmpty()) {
            return "Нет активных исполнителей";
        }
        
        return activeOffers.stream()
                .filter(offer -> offer.getUser() != null && !offer.getUser().getId().equals(request.getUser().getId()))
                .map(offer -> offer.getUser().getUsername())
                .distinct()
                .collect(Collectors.joining(", "));
    }
} 
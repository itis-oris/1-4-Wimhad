package com.skillswap.repository;

import com.skillswap.entity.Offer;
import com.skillswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByUser(User user);

    // Кастомный запрос: найти активные предложения по навыку
    @Query("SELECT o FROM Offer o WHERE o.skill.id = :skillId AND o.isActive = true")
    List<Offer> findActiveOffersBySkillId(Long skillId);
} 
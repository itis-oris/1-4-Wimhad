package com.skillswap.repository;

import com.skillswap.entity.Request;
import com.skillswap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUser(User user);

    @Query("SELECT r FROM Request r WHERE r.skill.id IN (SELECT o.skill.id FROM Offer o WHERE o.user.id = :ownerId)")
    List<Request> findRequestsForOffersOwnedBy(@Param("ownerId") Long ownerId);
} 
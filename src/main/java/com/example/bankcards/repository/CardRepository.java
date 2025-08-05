package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {
    boolean existsByCardNumber(String cardNumber);
    Optional<Card> findById(Long id);
    Optional<Card> findByIdAndOwner(Long id, User user);
    List<Card> findByOwner(User user);

    /** С участием Pageable */
    Page<Card> findByOwner(User user, Pageable pageable);
}

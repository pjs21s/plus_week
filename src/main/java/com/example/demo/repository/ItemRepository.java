package com.example.demo.repository;

import com.example.demo.entity.Item;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE item SET status = NULL WHERE id = :itemId", nativeQuery = true)
    void updateStatusNull(Long itemId);
}

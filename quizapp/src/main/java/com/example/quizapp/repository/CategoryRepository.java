package com.example.quizapp.repository;

import com.example.quizapp.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find category by name
    Optional<Category> findByName(String name);

    // Check if category exists
    boolean existsByName(String name);

    // Pagination support
    Page<Category> findAll(Pageable pageable);
}

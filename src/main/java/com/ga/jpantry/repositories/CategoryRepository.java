package com.ga.jpantry.repositories;

import com.ga.jpantry.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    void deleteByName(String name);

    /**
     * Multithreaded find all
     * @return CompletableFuture ArrayList Category
     */
    @Async("executor")
    CompletableFuture<ArrayList<Category>> findAllBy();
}

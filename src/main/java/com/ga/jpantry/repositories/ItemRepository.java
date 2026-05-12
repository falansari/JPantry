package com.ga.jpantry.repositories;

import com.ga.jpantry.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ItemRepository extends JpaRepository<Item, Long> {
    boolean existsByName(String name);

    Optional<Item> findByName(String name);

    void deleteByName(String name);

    /**
     * Multithreaded find all
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllBy();

    /**
     * Multithreaded find all items belonging to a category.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllByCategory(Long categoryId);

    /**
     * Multithreaded find all items belonging to a location.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllByLocation(Long locationId);

    /**
     * Multithreaded find all items belonging to a source.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllBySource(Long sourceId);

    /**
     * Multithreaded find all items with a specific quantity.
     * Useful for finding out of stock items.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllByQuantity(int quantity);
}

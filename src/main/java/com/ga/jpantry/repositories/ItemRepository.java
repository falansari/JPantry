package com.ga.jpantry.repositories;

import com.ga.jpantry.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllByName(String name);

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
     * Multithreaded find all items under a specific quantity.
     * Useful for finding out of stock items by defining 1, or low stock items.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllByQuantityLessThan(int quantity);

    /**
     * Multithreaded find all items above a specific quantity.
     * Useful for finding high stock items.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    CompletableFuture<ArrayList<Item>> findAllByQuantityGreaterThan(int quantity);
}

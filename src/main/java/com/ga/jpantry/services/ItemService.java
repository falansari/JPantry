package com.ga.jpantry.services;

import com.ga.jpantry.exceptions.AccessDeniedException;
import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.exceptions.InformationNotFoundException;
import com.ga.jpantry.models.Item;
import com.ga.jpantry.models.enums.Role;
import com.ga.jpantry.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Create a new item.
     * @param item Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Item
     */
    public Item create(Item item) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to create a item.");
        }

        // rule: name not nullable
        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequestException("A name must be provided to create a new item.");
        }

        return itemRepository.save(item);
    }

    /**
     * Get item by its ID.
     * @param id Long
     * @return Item
     */
    public Item readById(Long id) {
        // rule: exists
        return itemRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("A item with ID " + id + " does not exist."));
    }

    /**
     * Get list of items that have the same name.
     * @param name String
     * @return CompletableFuture ArrayList Item
     */
    public CompletableFuture<ArrayList<Item>> readAllByName(String name) {
        // rule: exists
        return itemRepository.findAllByName(name);
    }

    /**
     * Get all items. Asynchronous Operation.
     * @return CompletableFuture ArrayList Item
     */
    public CompletableFuture<ArrayList<Item>> readAll() {
        return itemRepository.findAllBy();
    }

    /**
     * Get all items belonging to a category. Asynchronous Operation.
     * @return CompletableFuture ArrayList Item
     */
    public CompletableFuture<ArrayList<Item>> readAllByCategory(Long categoryId) {
        return itemRepository.findAllByCategory(categoryId);
    }

    /**
     * Get all items belonging to a location. Asynchronous Operation.
     * @return CompletableFuture ArrayList Item
     */
    public CompletableFuture<ArrayList<Item>> readAllByLocation(Long locationId) {
        return itemRepository.findAllByLocation(locationId);
    }

    /**
     * Get all items belonging to a source. Asynchronous Operation.
     * @return CompletableFuture ArrayList Item
     */
    public CompletableFuture<ArrayList<Item>> readAllBySource(Long sourceId) {
        return itemRepository.findAllBySource(sourceId);
    }

    /**
     * Get all items under a specific quantity. Asynchronous Operation.
     * Useful for finding out of stock items by defining 1, or low stock items.
     * @return CompletableFuture ArrayList Item
     */
    public CompletableFuture<ArrayList<Item>> readAllByQuantityLessThan(int quantity) {
        return itemRepository.findAllByQuantityLessThan(quantity);
    }

    /**
     * Get all items above a specific quantity. Asynchronous Operation.
     * Useful for finding high stock items.
     * @return CompletableFuture ArrayList Item
     */
    public CompletableFuture<ArrayList<Item>> readAllByQuantityGreaterThan(int quantity) {
        return itemRepository.findAllByQuantityGreaterThan(quantity);
    }

    /**
     * Update an existing item.
     * @param item Object {id Long, name String, defaultExpiryPeriodDays int}
     * @return Item updated record
     */
    public Item updateById(Item item) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to update a item.");
        }

        // rule: id not null
        if (item.getId() == null)
            throw new BadRequestException("Item id must not be null.");

        // rule: exists
        Item record = itemRepository.findById(item.getId())
                .orElseThrow(() -> new InformationNotFoundException("A item with ID " + item.getId() + " does not exist."));

        // update record
        if (!Objects.equals(item.getName(), record.getName())) {
            record.setName(item.getName());
        }

        return itemRepository.save(record);
    }

    /**
     * Delete a item.
     * @param id Long
     * @return boolean True if successful.
     */
    public boolean deleteById(Long id) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to delete a item.");
        }

        // rule: exists
        if (!itemRepository.existsById(id))
            throw new InformationNotFoundException("A item with ID " + id + " does not exist.");

        itemRepository.deleteById(id);
        return true;
    }
}

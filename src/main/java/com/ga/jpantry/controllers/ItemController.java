package com.ga.jpantry.controllers;

import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.models.Item;
import com.ga.jpantry.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("item")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Find an item by its id.
     * @param id Long
     * @return Item
     */
    @GetMapping("")
    public Item getItem(@RequestParam(value = "id") Long id) {
        return itemService.readById(id);
    }

    /**
     * Download item's photo.
     * @param itemId Long Item's id
     * @return ResponseEntity Resource
     */
    @GetMapping("/photo")
    public ResponseEntity<Resource> downloadItemPhoto(@RequestParam(value = "id") Long itemId) {
        return itemService.downloadPhoto(itemId);
    }

    /**
     * Get all items.
     * @return CompletableFuture ArrayList Item
     */
    @GetMapping("/list")
    public CompletableFuture<ArrayList<Item>> getAllItems() {
        return itemService.readAll();
    }

    /**
     * Get all items by their name, category, location, or source, in that priority.
     * Not combinable.
     * @return CompletableFuture ArrayList Item
     */
    @GetMapping("/list/by")
    public CompletableFuture<ArrayList<Item>> GetAllItemsBy(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "location", required = false) Long locationId,
            @RequestParam(value = "source", required = false) Long sourceId
    ) {
        if (name != null) {
            return itemService.readAllByName(name);
        } else if (categoryId != null) {
            return itemService.readAllByCategory(categoryId);
        } else if (locationId != null) {
            return itemService.readAllByLocation(locationId);
        } else if (sourceId != null) {
            return itemService.readAllBySource(sourceId);
        } else {
            throw new BadRequestException("A name, category, location or source must be provided to search.");
        }
    }

    /**
     * Get list of all items with available stock less than quantity.
     * @param quantity int
     * @return CompletableFuture ArrayList Item
     */
    @GetMapping("/list/less")
    public CompletableFuture<ArrayList<Item>> getAllItemsLessThanQuantity(@RequestParam("q") int quantity) {
        return itemService.readAllByQuantityLessThan(quantity);
    }

    /**
     * Get list of all items with available stock greater than quantity.
     * @param quantity int
     * @return CompletableFuture ArrayList Item
     */
    @GetMapping("/list/greater")
    public CompletableFuture<ArrayList<Item>> getAllItemsGreaterThanQuantity(@RequestParam("q") int quantity) {
        return itemService.readAllByQuantityGreaterThan(quantity);
    }

    /**
     * Create a new item.
     * @param item Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @param photo MultipartFile PNG, JPEG. Optional.
     * @return Item
     */
    @PostMapping("/add")
    public Item addItem(@RequestBody Item item, @RequestParam(value = "photo", required = false) MultipartFile photo) {
        return itemService.create(item, photo);
    }

    /**
     * Update an existing item.
     * @param item Object {id Long, name String, defaultExpiryPeriodDays int}
     * @param photo MultipartFile PNG, JPEG. Optional.
     * @return Item updated record
     */
    @PatchMapping("/edit")
    public Item editItem(@RequestBody Item item, @RequestParam(value = "photo", required = false) MultipartFile photo) {
        return itemService.updateById(item, photo);
    }

    /**
     * Delete a item.
     * @param id Long
     * @return boolean True if successful.
     */
    @DeleteMapping("/delete")
    public boolean deleteItem(@RequestParam(value = "id") Long id) {
        return itemService.deleteById(id);
    }
}

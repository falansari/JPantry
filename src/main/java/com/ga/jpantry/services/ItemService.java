package com.ga.jpantry.services;

import com.ga.jpantry.exceptions.AccessDeniedException;
import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.exceptions.InformationNotFoundException;
import com.ga.jpantry.models.Item;
import com.ga.jpantry.models.enums.Role;
import com.ga.jpantry.repositories.ItemRepository;
import com.ga.jpantry.utilities.Uploads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final Uploads uploads;
    final String uploadImagePath = "uploads/items";

    @Autowired
    public ItemService(ItemRepository itemRepository, Uploads uploads) {
        this.itemRepository = itemRepository;
        this.uploads = uploads;
    }

    /**
     * Create a new item.
     * @param item Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @param photo MultipartFile PNG, JPEG. Optional.
     * @return Item
     */
    public Item create(Item item, MultipartFile photo) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to create a item.");
        }

        // rule: name not nullable
        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequestException("A name must be provided to create a new item.");
        }

        if (!photo.isEmpty()) { // upload photo if any
            String uploadedPhoto = uploadPhoto(photo);
            item.setPhoto(uploadedPhoto);
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
    public Item updateById(Item item, MultipartFile photo) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to update a item.");
        }

        // rule: id not null
        if (item.getId() == null)
            throw new BadRequestException("Item id must not be null.");

        // rule: exists
        Item record = readById(item.getId());

        // update record
        if (!Objects.equals(item.getName(), record.getName())) {
            record.setName(item.getName());
        }

        if (!photo.isEmpty()) { // re-upload new photo
            if (record.getPhoto() != null) {
                uploads.deleteFile(uploadImagePath, record.getPhoto());
            }

            String uploadedPhoto = uploadPhoto(photo);
            record.setPhoto(uploadedPhoto);
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

        Item item = readById(id);
        deletePhoto(item.getId());
        itemRepository.deleteById(item.getId());
        return true;
    }

    /**
     * Upload photo
     * @param file MultipartFile PNG, JPG
     * @return ResponseEntity Resource
     */
    public String uploadPhoto(MultipartFile file) {
        return uploads.uploadImage(uploadImagePath, file);
    }

    /**
     * Download stored photo
     * @return ResponseEntity Resource The stored image if any [PNG, JPEG]
     */
    public ResponseEntity<Resource> downloadPhoto(Long itemId) {
        Item item = readById(itemId);

        return uploads.downloadFile(uploadImagePath, item.getPhoto());
    }

    /**
     * Delete photo.
     * @param itemId Long
     */
    public void deletePhoto(Long itemId) {
        Item item = readById(itemId);

        if (item.getPhoto() == null) {
            throw new InformationNotFoundException("Item with ID " + item.getId() + " does not have an associated photo");
        }

        uploads.deleteFile(uploadImagePath, item.getPhoto());
        item.setPhoto(null);
        itemRepository.save(item);
    }
}

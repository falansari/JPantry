package com.ga.jpantry.services;

import com.ga.jpantry.exceptions.AccessDeniedException;
import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.exceptions.InformationNotFoundException;
import com.ga.jpantry.models.Category;
import com.ga.jpantry.models.Item;
import com.ga.jpantry.models.Location;
import com.ga.jpantry.models.Source;
import com.ga.jpantry.models.enums.Role;
import com.ga.jpantry.repositories.ItemRepository;
import com.ga.jpantry.utilities.Uploads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final Uploads uploads;
    final String uploadImagePath = "uploads/items";
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final SourceService sourceService;

    @Autowired
    public ItemService(ItemRepository itemRepository, Uploads uploads, CategoryService categoryService, LocationService locationService, SourceService sourceService) {
        this.itemRepository = itemRepository;
        this.uploads = uploads;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.sourceService = sourceService;
    }

    /**
     * Create a new item.
     * @param item Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @param photo MultipartFile PNG, JPEG. Optional.
     * @return Item
     */
    public Item create(Item item, MultipartFile photo, Long categoryId, Long locationId, Long sourceId) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to create a item.");
        }

        // rule: name not nullable
        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequestException("A name must be provided to create a new item.");
        }

        if (photo != null) {
            if (!photo.isEmpty()) { // upload photo if any
                String uploadedPhoto = uploadPhoto(photo);
                item.setPhoto(uploadedPhoto);
            }
        }

        if (categoryId != null) {
            Category category = categoryService.readById(categoryId);
            item.setCategory(category);

            if (item.getExpiryDate() == null) { // set default expiry if custom not set
                if (item.getProductionDate() != null) { // based on production date if set
                    item.setExpiryDate(item.getProductionDate().plusDays(category.getDefaultExpiryPeriodDays()));
                } else { // based on today's date
                    item.setExpiryDate(LocalDate.now().plusDays(category.getDefaultExpiryPeriodDays()));
                }
            }
        }

        if (locationId != null) {
            Location location = locationService.readById(locationId);
            item.setLocation(location);
        }

        if (sourceId != null) {
            Source source = sourceService.readById(sourceId);
            item.setSource(source);
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
    @Async("executor")
    public CompletableFuture<ArrayList<Item>> readAll() {
        return itemRepository.findAllBy();
    }

    /**
     * Get all items belonging to a category. Asynchronous Operation.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    public CompletableFuture<ArrayList<Item>> readAllByCategory(Long categoryId) {
        Category category = categoryService.readById(categoryId);
        return itemRepository.findAllByCategory(category);
    }

    /**
     * Get all items belonging to a location. Asynchronous Operation.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    public CompletableFuture<ArrayList<Item>> readAllByLocation(Long locationId) {
        Location location = locationService.readById(locationId);
        return itemRepository.findAllByLocation(location);
    }

    /**
     * Get all items belonging to a source. Asynchronous Operation.
     * @return CompletableFuture ArrayList Item
     */
    @Async("executor")
    public CompletableFuture<ArrayList<Item>> readAllBySource(Long sourceId) {
        Source source = sourceService.readById(sourceId);
        return itemRepository.findAllBySource(source);
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
    public Item updateById(Item item, MultipartFile photo, Long categoryId, Long locationId, Long sourceId) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to update a item.");
        }

        // rule: id not null
        if (item.getId() == null)
            throw new BadRequestException("Item id must not be null.");

        // rule: exists
        Item record = readById(item.getId());

        if (photo != null) {
            if (!photo.isEmpty()) { // re-upload new photo
                if (record.getPhoto() != null) {
                    uploads.deleteFile(uploadImagePath, record.getPhoto());
                }

                String uploadedPhoto = uploadPhoto(photo);
                record.setPhoto(uploadedPhoto);
            }
        }

        if (categoryId != null) { // update category
            Category category = categoryService.readById(categoryId);
            record.setCategory(category);

            // set default expiry
            if (record.getExpiryDate() == null) record.setExpiryDate(LocalDate.now().plusDays(category.getDefaultExpiryPeriodDays()));
        }

        if (locationId != null) {
            Location location = locationService.readById(locationId);
            record.setLocation(location);
        }

        if (sourceId != null) {
            Source source = sourceService.readById(sourceId);
            record.setSource(source);
        }

        // update record
        if (item.getName() != null) record.setName(item.getName());
        if (item.getProductionDate() != null) record.setProductionDate(item.getProductionDate());
        if (item.getExpiryDate() != null) record.setExpiryDate(item.getExpiryDate()); // override default expiry
        if (item.getPrice() != null) record.setPrice(item.getPrice());
        if (item.getQuantity() != record.getQuantity()) record.setQuantity(item.getQuantity());

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

        if (item.getPhoto() != null) {
            uploads.deleteFile(uploadImagePath, item.getPhoto());
            item.setPhoto(null);
        }

        itemRepository.save(item);
    }
}

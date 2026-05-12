package com.ga.jpantry.services;

import com.ga.jpantry.exceptions.AccessDeniedException;
import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.exceptions.InformationExistException;
import com.ga.jpantry.exceptions.InformationNotFoundException;
import com.ga.jpantry.models.Category;
import com.ga.jpantry.models.enums.Role;
import com.ga.jpantry.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Create a new category.
     * @param category Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Category
     */
    public Category create(Category category) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to create a category.");
        }

        // rule: name not nullable
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BadRequestException("A name must be provided to create a new category.");
        }

        // rule: unique, doesn't already exist
        if (categoryRepository.existsByName(category.getName())) {
            throw new InformationExistException("A category with the name " + category.getName() + " already exists.");
        }

        return categoryRepository.save(category);
    }

    /**
     * Get category by its ID.
     * @param id Long
     * @return Category
     */
    public Category readById(Long id) {
        // rule: exists
        return categoryRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("A category with ID " + id + " does not exist."));
    }

    /**
     * Get category by its name.
     * @param name String
     * @return Category
     */
    public Category readByName(String name) {
        // rule: exists
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new InformationNotFoundException("A category with name " + name + " does not exist."));
    }

    /**
     * Get all categories. Asynchronous Operation.
     * @return CompletableFuture ArrayList Category
     */
    public CompletableFuture<ArrayList<Category>> readAll() {
        return categoryRepository.findAllBy();
    }

    /**
     * Update an existing category.
     * @param category Object {id Long, name String, defaultExpiryPeriodDays int}
     * @return Category updated record
     */
    public Category updateById(Category category) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to update a category.");
        }

        // rule: id not null
        if (category.getId() == null)
            throw new BadRequestException("Category id must not be null.");

        // rule: exists
        Category record = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new InformationNotFoundException("A category with ID " + category.getId() + " does not exist."));

        // rule: unique, doesn't already exist
        Category existingName = categoryRepository.findByName(category.getName()).orElse(null);

        if (existingName != null && !Objects.equals(record.getId(), existingName.getId())) {
            throw new InformationExistException("A category with the name " + category.getName() + " already exists.");
        }

        // update record
        if (!Objects.equals(category.getName(), record.getName())) {
            record.setName(category.getName());
        }

        if (!Objects.equals(category.getDefaultExpiryPeriodDays(), record.getDefaultExpiryPeriodDays())
        && category.getDefaultExpiryPeriodDays() != null) {
            record.setDefaultExpiryPeriodDays(category.getDefaultExpiryPeriodDays());
        }

        return categoryRepository.save(record);
    }

    /**
     * Delete an item category.
     * @param id Long
     * @return boolean True if successful.
     */
    public boolean deleteById(Long id) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to delete a category.");
        }

        // rule: exists
        if (!categoryRepository.existsById(id))
            throw new InformationNotFoundException("A category with ID " + id + " does not exist.");

        categoryRepository.deleteById(id);
        return true;
    }
}

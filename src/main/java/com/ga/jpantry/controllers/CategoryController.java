package com.ga.jpantry.controllers;

import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.models.Category;
import com.ga.jpantry.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Find a category by its id or name.
     * @param id Long
     * @param name String
     * @return Category
     */
    @GetMapping("")
    public Category getCategory(@RequestParam(value = "id", required = false) Long id,
                                @RequestParam(value = "name", required = false) String name) {
        if (id != null) {
            return categoryService.readById(id);
        } else if (name != null) {
            return categoryService.readByName(name);
        } else {
            throw new BadRequestException("Either an id or name must be provided to search for a category.");
        }
    }

    /**
     * Get all categories.
     * @return CompletableFuture ArrayList Category
     */
    @GetMapping("/list")
    public CompletableFuture<ArrayList<Category>> getAllCategories() {
        return categoryService.readAll();
    }

    /**
     * Create a new category.
     * @param category Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Category
     */
    @PostMapping("/add")
    public Category addCategory(@RequestBody Category category) {
        return categoryService.create(category);
    }

    /**
     * Update an existing category.
     * @param category Object {id Long, name String, defaultExpiryPeriodDays int}
     * @return Category updated record
     */
    @PostMapping("/edit")
    public Category editCategory(@RequestBody Category category) {
        return categoryService.updateById(category);
    }

    /**
     * Delete a category.
     * @param id Long
     * @param name String
     * @return boolean True if successful.
     */
    @PostMapping("/delete")
    public boolean deleteCategory(@RequestParam(value = "id", required = false) Long id,
                                @RequestParam(value = "name", required = false) String name) {
        if (id != null) {
            return categoryService.deleteById(id);
        } else if (name != null) {
            return categoryService.deleteByName(name);
        } else {
            throw new BadRequestException("Either an id or name must be provided to delete a category.");
        }
    }
}

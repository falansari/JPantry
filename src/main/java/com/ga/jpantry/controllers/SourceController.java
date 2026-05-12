package com.ga.jpantry.controllers;

import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.models.Source;
import com.ga.jpantry.services.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("source")
public class SourceController {
    private final SourceService sourceService;

    @Autowired
    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    /**
     * Find a source by its id or name.
     * @param id Long
     * @param name String
     * @return Source
     */
    @GetMapping("")
    public Source getSource(@RequestParam(value = "id", required = false) Long id,
                                @RequestParam(value = "name", required = false) String name) {
        if (id != null) {
            return sourceService.readById(id);
        } else if (name != null) {
            return sourceService.readByName(name);
        } else {
            throw new BadRequestException("Either an id or name must be provided to search for a source.");
        }
    }

    /**
     * Get all categories.
     * @return CompletableFuture ArrayList Source
     */
    @GetMapping("/list")
    public CompletableFuture<ArrayList<Source>> getAllCategories() {
        return sourceService.readAll();
    }

    /**
     * Create a new source.
     * @param source Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Source
     */
    @PostMapping("/add")
    public Source addSource(@RequestBody Source source) {
        return sourceService.create(source);
    }

    /**
     * Update an existing source.
     * @param source Object {id Long, name String, defaultExpiryPeriodDays int}
     * @return Source updated record
     */
    @PatchMapping("/edit")
    public Source editSource(@RequestBody Source source) {
        return sourceService.updateById(source);
    }

    /**
     * Delete a source.
     * @param id Long
     * @return boolean True if successful.
     */
    @DeleteMapping("/delete")
    public boolean deleteSource(@RequestParam(value = "id") Long id) {
        return sourceService.deleteById(id);
    }
}

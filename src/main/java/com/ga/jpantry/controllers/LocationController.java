package com.ga.jpantry.controllers;

import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.models.Location;
import com.ga.jpantry.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("location")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Find a location by its id or name.
     * @param id Long
     * @param name String
     * @return Location
     */
    @GetMapping("")
    public Location getLocation(@RequestParam(value = "id", required = false) Long id,
                                @RequestParam(value = "name", required = false) String name) {
        if (id != null) {
            return locationService.readById(id);
        } else if (name != null) {
            return locationService.readByName(name);
        } else {
            throw new BadRequestException("Either an id or name must be provided to search for a location.");
        }
    }

    /**
     * Get all locations.
     * @return CompletableFuture ArrayList Location
     */
    @GetMapping("/list")
    public CompletableFuture<ArrayList<Location>> getAllLocations() {
        return locationService.readAll();
    }

    /**
     * Create a new location.
     * @param location Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Location
     */
    @PostMapping("/add")
    public Location addLocation(@RequestBody Location location) {
        return locationService.create(location);
    }

    /**
     * Update an existing location.
     * @param location Object {id Long, name String, defaultExpiryPeriodDays int}
     * @return Location updated record
     */
    @PatchMapping("/edit")
    public Location editLocation(@RequestBody Location location) {
        return locationService.updateById(location);
    }

    /**
     * Delete a location.
     * @param id Long
     * @return boolean True if successful.
     */
    @DeleteMapping("/delete")
    public boolean deleteLocation(@RequestParam(value = "id") Long id) {
        return locationService.deleteById(id);
    }
}

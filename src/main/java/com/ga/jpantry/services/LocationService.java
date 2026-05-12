package com.ga.jpantry.services;

import com.ga.jpantry.exceptions.AccessDeniedException;
import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.exceptions.InformationExistException;
import com.ga.jpantry.exceptions.InformationNotFoundException;
import com.ga.jpantry.models.Location;
import com.ga.jpantry.models.enums.Role;
import com.ga.jpantry.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Create a new location.
     * @param location Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Location
     */
    public Location create(Location location) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to create a location.");
        }

        // rule: name not nullable
        if (location.getName() == null || location.getName().isBlank()) {
            throw new BadRequestException("A name must be provided to create a new location.");
        }

        // rule: unique, doesn't already exist
        if (locationRepository.existsByName(location.getName())) {
            throw new InformationExistException("A location with the name " + location.getName() + " already exists.");
        }

        return locationRepository.save(location);
    }

    /**
     * Get location by its ID.
     * @param id Long
     * @return Location
     */
    public Location readById(Long id) {
        // rule: exists
        return locationRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("A location with ID " + id + " does not exist."));
    }

    /**
     * Get location by its name.
     * @param name String
     * @return Location
     */
    public Location readByName(String name) {
        // rule: exists
        return locationRepository.findByName(name)
                .orElseThrow(() -> new InformationNotFoundException("A location with name " + name + " does not exist."));
    }

    /**
     * Get all categories. Asynchronous Operation.
     * @return CompletableFuture ArrayList Location
     */
    public CompletableFuture<ArrayList<Location>> readAll() {
        return locationRepository.findAllBy();
    }

    /**
     * Update an existing location.
     * @param location Object {id Long, name String, defaultExpiryPeriodDays int}
     * @return Location updated record
     */
    public Location updateById(Location location) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to update a location.");
        }

        // rule: id not null
        if (location.getId() == null)
            throw new BadRequestException("Location id must not be null.");

        // rule: exists
        Location record = locationRepository.findById(location.getId())
                .orElseThrow(() -> new InformationNotFoundException("A location with ID " + location.getId() + " does not exist."));

        // rule: unique, doesn't already exist
        Location existingName = locationRepository.findByName(location.getName()).orElse(null);

        if (existingName != null && !Objects.equals(record.getId(), existingName.getId())) {
            throw new InformationExistException("A location with the name " + location.getName() + " already exists.");
        }

        // update record
        if (!Objects.equals(location.getName(), record.getName())) {
            record.setName(location.getName());
        }

        return locationRepository.save(record);
    }

    /**
     * Delete a location.
     * @param id Long
     * @return boolean True if successful.
     */
    public boolean deleteById(Long id) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to delete a location.");
        }

        // rule: exists
        if (!locationRepository.existsById(id))
            throw new InformationNotFoundException("A location with ID " + id + " does not exist.");

        locationRepository.deleteById(id);
        return true;
    }
}

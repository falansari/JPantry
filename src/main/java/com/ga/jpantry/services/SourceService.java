package com.ga.jpantry.services;

import com.ga.jpantry.exceptions.AccessDeniedException;
import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.exceptions.InformationExistException;
import com.ga.jpantry.exceptions.InformationNotFoundException;
import com.ga.jpantry.models.Source;
import com.ga.jpantry.models.enums.Role;
import com.ga.jpantry.repositories.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class SourceService {
    private final SourceRepository sourceRepository;

    @Autowired
    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    /**
     * Create a new source.
     * @param source Object {name (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Source
     */
    public Source create(Source source) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to create a source.");
        }

        // rule: name not nullable
        if (source.getName() == null || source.getName().isBlank()) {
            throw new BadRequestException("A name must be provided to create a new source.");
        }

        // rule: unique, doesn't already exist
        if (sourceRepository.existsByName(source.getName())) {
            throw new InformationExistException("A source with the name " + source.getName() + " already exists.");
        }

        return sourceRepository.save(source);
    }

    /**
     * Get source by its ID.
     * @param id Long
     * @return Source
     */
    public Source readById(Long id) {
        // rule: exists
        return sourceRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("A source with ID " + id + " does not exist."));
    }

    /**
     * Get source by its name.
     * @param name String
     * @return Source
     */
    public Source readByName(String name) {
        // rule: exists
        return sourceRepository.findByName(name)
                .orElseThrow(() -> new InformationNotFoundException("A source with name " + name + " does not exist."));
    }

    /**
     * Get all categories. Asynchronous Operation.
     * @return CompletableFuture ArrayList Source
     */
    public CompletableFuture<ArrayList<Source>> readAll() {
        return sourceRepository.findAllBy();
    }

    /**
     * Update an existing source.
     * @param source Object {id Long, name String, defaultExpiryPeriodDays int}
     * @return Source updated record
     */
    public Source updateById(Source source) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to update a source.");
        }

        // rule: id not null
        if (source.getId() == null)
            throw new BadRequestException("Source id must not be null.");

        // rule: exists
        Source record = sourceRepository.findById(source.getId())
                .orElseThrow(() -> new InformationNotFoundException("A source with ID " + source.getId() + " does not exist."));

        // rule: unique, doesn't already exist
        Source existingName = sourceRepository.findByName(source.getName()).orElse(null);

        if (existingName != null && !Objects.equals(record.getId(), existingName.getId())) {
            throw new InformationExistException("A source with the name " + source.getName() + " already exists.");
        }

        // update record
        if (!Objects.equals(source.getName(), record.getName())) {
            record.setName(source.getName());
        }

        return sourceRepository.save(record);
    }

    /**
     * Delete a source.
     * @param id Long
     * @return boolean True if successful.
     */
    public boolean deleteById(Long id) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to delete a source.");
        }

        // rule: exists
        if (!sourceRepository.existsById(id))
            throw new InformationNotFoundException("A source with ID " + id + " does not exist.");

        sourceRepository.deleteById(id);
        return true;
    }
}

package com.ga.jpantry.repositories;

import com.ga.jpantry.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByName(String name);

    Optional<Location> findByName(String name);

    void deleteByName(String name);

    /**
     * Multithreaded find all
     * @return CompletableFuture ArrayList Category
     */
    @Async("executor")
    CompletableFuture<ArrayList<Location>> findAllBy();
}

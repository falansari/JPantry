package com.ga.jpantry.repositories;

import com.ga.jpantry.models.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {
    boolean existsByBarcode(String barcode);

    Optional<Barcode> findByBarcode(String barcode);

    void deleteByBarcode(String barcode);

    /**
     * Multithreaded find all
     * @return CompletableFuture ArrayList Category
     */
    @Async("executor")
    CompletableFuture<ArrayList<Barcode>> findAllBy();
}

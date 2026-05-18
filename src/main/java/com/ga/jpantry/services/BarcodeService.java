package com.ga.jpantry.services;

import com.ga.jpantry.exceptions.AccessDeniedException;
import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.exceptions.InformationExistException;
import com.ga.jpantry.exceptions.InformationNotFoundException;
import com.ga.jpantry.models.Barcode;
import com.ga.jpantry.models.enums.Role;
import com.ga.jpantry.repositories.BarcodeRepository;
import com.spire.barcode.BarCodeGenerator;
import com.spire.barcode.BarCodeType;
import com.spire.barcode.BarcodeScanner;
import com.spire.barcode.BarcodeSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class BarcodeService {
    private final BarcodeRepository barcodeRepository;

    @Autowired
    public BarcodeService(BarcodeRepository barcodeRepository) {
        this.barcodeRepository = barcodeRepository;
    }

    /**
     * Create a new barcode.
     * @param barcode Object {barcode (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Barcode
     */
    public Barcode create(Barcode barcode) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to create a barcode.");
        }

        // rule: barcode not nullable
        if (barcode.getBarcode() == null || barcode.getBarcode().isBlank()) {
            throw new BadRequestException("A barcode must be provided to create a new barcode.");
        }

        // rule: unique, doesn't already exist
        if (barcodeRepository.existsByBarcode(barcode.getBarcode())) {
            throw new InformationExistException("A barcode with the barcode " + barcode.getBarcode() + " already exists.");
        }

        return barcodeRepository.save(barcode);
    }

    /**
     * Get barcode by its ID.
     * @param id Long
     * @return Barcode
     */
    public Barcode readById(Long id) {
        // rule: exists
        return barcodeRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("A barcode with ID " + id + " does not exist."));
    }

    /**
     * Get barcode by its barcode.
     * @param barcode String
     * @return Barcode if found, else returns null.
     */
    public Barcode readByBarcode(String barcode) {
        // rule: exists
        return barcodeRepository.findByBarcode(barcode)
                .orElse(null);
    }

    /**
     * Get all categories. Asynchronous Operation.
     * @return CompletableFuture ArrayList Barcode
     */
    public CompletableFuture<ArrayList<Barcode>> readAll() {
        return barcodeRepository.findAllBy();
    }

    /**
     * Update an existing barcode.
     * @param barcode Object {id Long, barcode String, defaultExpiryPeriodDays int}
     * @return Barcode updated record
     */
    public Barcode updateById(Barcode barcode) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to update a barcode.");
        }

        // rule: id not null
        if (barcode.getId() == null)
            throw new BadRequestException("Barcode id must not be null.");

        // rule: exists
        Barcode record = barcodeRepository.findById(barcode.getId())
                .orElseThrow(() -> new InformationNotFoundException("A barcode with ID " + barcode.getId() + " does not exist."));

        // rule: unique, doesn't already exist
        Barcode existingBarcode = barcodeRepository.findByBarcode(barcode.getBarcode()).orElse(null);

        if (existingBarcode != null && !Objects.equals(record.getId(), existingBarcode.getId())) {
            throw new InformationExistException("A barcode with the barcode " + barcode.getBarcode() + " already exists.");
        }

        // update record
        if (!Objects.equals(barcode.getBarcode(), record.getBarcode())) {
            record.setBarcode(barcode.getBarcode());
        }

        return barcodeRepository.save(record);
    }

    /**
     * Delete a barcode.
     * @param id Long
     * @return boolean True if successful.
     */
    public boolean deleteById(Long id) {
        // rule: only owner
        if (!UserService.getCurrentLoggedInUser().getRole().equals(Role.OWNER)) {
            throw new AccessDeniedException("User not authorized to delete a barcode.");
        }

        // rule: exists
        if (!barcodeRepository.existsById(id))
            throw new InformationNotFoundException("A barcode with ID " + id + " does not exist.");

        barcodeRepository.deleteById(id);
        return true;
    }

    /**
     * Get barcode number as String from barcode image. EAN-13 barcodes supported only.
     * @param barcodeImage MultipartFile image
     * @return String
     */
    public String readFromImage(MultipartFile barcodeImage) {
        if (barcodeImage == null) throw new BadRequestException("Barcode image must not be null.");
        if (barcodeImage.isEmpty()) throw new BadRequestException("Barcode image must not be empty.");

        try { // Supporting EAN-13 barcodes only for enhanced scanning accuracy
            return BarcodeScanner.scanOne(barcodeImage.getInputStream(), BarCodeType.EAN_13, true);

        } catch (Exception e) {
            throw new BadRequestException("Error executing read barcode image: " + e.getMessage());
        }
    }
}

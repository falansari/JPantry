package com.ga.jpantry.controllers;

import com.ga.jpantry.exceptions.BadRequestException;
import com.ga.jpantry.models.Barcode;
import com.ga.jpantry.services.BarcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("barcode")
public class BarcodeController {
    private final BarcodeService barcodeService;

    @Autowired
    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    /**
     * Find a barcode by its id or barcode.
     * @param id Long
     * @param barcode String
     * @return Barcode
     */
    @GetMapping("")
    public Barcode getBarcode(@RequestParam(value = "id", required = false) Long id,
                                @RequestParam(value = "barcode", required = false) String barcode) {
        if (id != null) {
            return barcodeService.readById(id);
        } else if (barcode != null) {
            return barcodeService.readByBarcode(barcode);
        } else {
            throw new BadRequestException("Either an id or barcode must be provided to search for a barcode.");
        }
    }

    /**
     * Get all barcodes.
     * @return CompletableFuture ArrayList Barcode
     */
    @GetMapping("/list")
    public CompletableFuture<ArrayList<Barcode>> getAllBarcodes() {
        return barcodeService.readAll();
    }

    /**
     * Create a new barcode.
     * @param barcode Object {barcode (required) String, defaultExpiryPeriodDays (optional) int}
     * @return Barcode
     */
    @PostMapping("/add")
    public Barcode addBarcode(@RequestBody Barcode barcode) {
        return barcodeService.create(barcode);
    }

    /**
     * Update an existing barcode.
     * @param barcode Object {id Long, barcode String, defaultExpiryPeriodDays int}
     * @return Barcode updated record
     */
    @PatchMapping("/edit")
    public Barcode editBarcode(@RequestBody Barcode barcode) {
        return barcodeService.updateById(barcode);
    }

    /**
     * Delete a barcode.
     * @param id Long
     * @return boolean True if successful.
     */
    @DeleteMapping("/delete")
    public boolean deleteBarcode(@RequestParam(value = "id") Long id) {
        return barcodeService.deleteById(id);
    }

    /**
     * Get barcode number as String from barcode image. EAN-13 barcodes supported only.
     * @param barcodeImage barcode Image file
     * @return String barcode number
     */
    @GetMapping("/scan")
    public String scanBarcodeImage(@RequestParam(value = "barcode") MultipartFile barcodeImage) {
        return barcodeService.readFromImage(barcodeImage);
    }

    /**
     * Generate and save barcode image from barcode number.
     * @param barcode String barcode number
     * @return ResponseEntity Resource Barcode image.
     */
    @PostMapping("/generate")
    public ResponseEntity<Resource> generateBarcodeImage(@RequestParam(value = "barcode") String barcode) {
        return barcodeService.generateBarcodeImage(barcode);
    }
}

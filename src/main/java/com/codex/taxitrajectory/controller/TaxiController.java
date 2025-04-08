package com.codex.taxitrajectory.controller;

import com.codex.taxitrajectory.model.TaxiRecord;

import com.codex.taxitrajectory.repository.DataLoader;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/taxi")
public class TaxiController {


    private final DataLoader dataLoader;

    public TaxiController(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<TaxiRecord>> getTaxiTrajectory(@PathVariable String id) {
        List<TaxiRecord> records = dataLoader.getRecordsByTaxiId(id);
        return ResponseEntity.ok(records);
    }
}


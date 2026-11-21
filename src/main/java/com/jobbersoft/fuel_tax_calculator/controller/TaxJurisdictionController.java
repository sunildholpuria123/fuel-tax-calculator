
package com.jobbersoft.fuel_tax_calculator.controller;

import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;
import com.jobbersoft.fuel_tax_calculator.repository.TaxJurisdictionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tax/fuel/jurisdictions")
@RequiredArgsConstructor
public class TaxJurisdictionController {

    private final TaxJurisdictionRepository jurisdictionRepository;

    @GetMapping
    public List<TaxJurisdiction> getJurisdictions() {
        return jurisdictionRepository.findAll();
    }
}

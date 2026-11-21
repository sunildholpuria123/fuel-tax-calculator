
package com.jobbersoft.fuel_tax_calculator.controller;

import com.jobbersoft.fuel_tax_calculator.dto.request.TaxCalculationRequest;
import com.jobbersoft.fuel_tax_calculator.dto.response.TaxCalculationResponse;
import com.jobbersoft.fuel_tax_calculator.service.FuelTaxCalculationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tax/fuel")
@RequiredArgsConstructor
public class FuelTaxController {

    private final FuelTaxCalculationService calculationService;

    @PostMapping("/calculate")
    @ResponseStatus(HttpStatus.CREATED)
    public TaxCalculationResponse calculateTax(
            @Valid @RequestBody TaxCalculationRequest request
    ) {
        return calculationService.calculateTax(request);
    }

    @GetMapping("/history/{shipmentId}")
    public List<TaxCalculationResponse> getTaxHistory(
            @PathVariable String shipmentId
    ) {
        return calculationService.getTaxHistory(shipmentId);
    }
}

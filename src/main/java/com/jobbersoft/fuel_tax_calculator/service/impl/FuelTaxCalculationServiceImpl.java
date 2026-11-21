
package com.jobbersoft.fuel_tax_calculator.service.impl;

import com.jobbersoft.fuel_tax_calculator.dto.request.TaxCalculationRequest;
import com.jobbersoft.fuel_tax_calculator.dto.response.TaxCalculationResponse;
import com.jobbersoft.fuel_tax_calculator.entity.FuelTaxCalculation;
import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;
import com.jobbersoft.fuel_tax_calculator.repository.FuelTaxCalculationRepository;
import com.jobbersoft.fuel_tax_calculator.repository.TaxJurisdictionRepository;
import com.jobbersoft.fuel_tax_calculator.service.FuelTaxCalculationService;
import com.jobbersoft.fuel_tax_calculator.exception.JurisdictionNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelTaxCalculationServiceImpl
        implements FuelTaxCalculationService {

    private final TaxJurisdictionRepository jurisdictionRepository;
    private final FuelTaxCalculationRepository calculationRepository;

    @Override
    @Transactional
    public TaxCalculationResponse calculateTax(TaxCalculationRequest request) {

        log.info("Calculating fuel tax for shipment: {}", request.shipmentId());

        TaxJurisdiction jurisdiction = jurisdictionRepository
                .findActiveByCode(
                        request.jurisdictionCode(),
                        LocalDate.now()
                )
                .orElseThrow(() -> new JurisdictionNotFoundException(
                        "No active jurisdiction found: " + request.jurisdictionCode()
                ));

        var calculatedTax = request.fuelQuantity()
                .multiply(request.pricePerGallon())
                .multiply(jurisdiction.getBaseRate())
                .setScale(4, RoundingMode.HALF_UP);

        FuelTaxCalculation calculation = FuelTaxCalculation.builder()
                .shipmentId(request.shipmentId())
                .jurisdiction(jurisdiction)
                .fuelQuantity(request.fuelQuantity())
                .pricePerGallon(request.pricePerGallon())
                .calculatedTax(calculatedTax)
                .calculatedOn(LocalDateTime.now())
                .build();

        FuelTaxCalculation saved = calculationRepository.save(calculation);

        log.info(
                "Fuel tax calculated successfully. Shipment: {}, Tax: {}",
                saved.getShipmentId(),
                saved.getCalculatedTax()
        );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxCalculationResponse> getTaxHistory(String shipmentId) {

        log.info("Fetching tax history for shipment: {}", shipmentId);

        return calculationRepository
                .findByShipmentIdOrderByCalculatedOnDesc(shipmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TaxCalculationResponse toResponse(
            FuelTaxCalculation calculation
    ) {
        TaxJurisdiction jurisdiction = calculation.getJurisdiction();

        return new TaxCalculationResponse(
                calculation.getId(),
                calculation.getShipmentId(),
                jurisdiction.getCode(),
                jurisdiction.getName(),
                calculation.getFuelQuantity(),
                calculation.getPricePerGallon(),
                jurisdiction.getBaseRate(),
                calculation.getCalculatedTax(),
                calculation.getCalculatedOn()
        );
    }
}

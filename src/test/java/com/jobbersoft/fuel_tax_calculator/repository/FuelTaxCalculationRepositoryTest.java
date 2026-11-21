
package com.jobbersoft.fuel_tax_calculator.repository;

import com.jobbersoft.fuel_tax_calculator.entity.FuelTaxCalculation;
import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FuelTaxCalculationRepositoryTest {

    @Autowired
    private FuelTaxCalculationRepository calculationRepository;

    @Autowired
    private TaxJurisdictionRepository jurisdictionRepository;

    @Test
    void findByShipmentIdOrderByCalculatedOnDesc_shouldReturnNewestFirst() {

        TaxJurisdiction jurisdiction = jurisdictionRepository
                .findById(1L)
                .orElseThrow();

        FuelTaxCalculation older = createCalculation(
                jurisdiction,
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        FuelTaxCalculation newer = createCalculation(
                jurisdiction,
                LocalDateTime.of(2026, 1, 2, 10, 0)
        );

        calculationRepository.save(older);
        calculationRepository.save(newer);
        calculationRepository.flush();

        List<FuelTaxCalculation> history =
                calculationRepository
                        .findByShipmentIdOrderByCalculatedOnDesc("SHIP-TEST");

        assertEquals(2, history.size());
        assertTrue(history.get(0).getCalculatedOn()
                .isAfter(history.get(1).getCalculatedOn()));
    }

    private FuelTaxCalculation createCalculation(
            TaxJurisdiction jurisdiction,
            LocalDateTime calculatedOn
    ) {
        return FuelTaxCalculation.builder()
                .shipmentId("SHIP-TEST")
                .jurisdiction(jurisdiction)
                .fuelQuantity(new BigDecimal("100"))
                .pricePerGallon(new BigDecimal("3.50"))
                .calculatedTax(new BigDecimal("26.2500"))
                .calculatedOn(calculatedOn)
                .build();
    }
}

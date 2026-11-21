
package com.jobbersoft.fuel_tax_calculator.repository;

import com.jobbersoft.fuel_tax_calculator.entity.FuelTaxCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FuelTaxCalculationRepository
        extends JpaRepository<FuelTaxCalculation, Long> {

    List<FuelTaxCalculation> findByShipmentIdOrderByCalculatedOnDesc(
            String shipmentId
    );
}

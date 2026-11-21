
package com.jobbersoft.fuel_tax_calculator.repository;

import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaxJurisdictionRepositoryCustom {

    Optional<TaxJurisdiction> findActiveByCode(
            String code,
            LocalDate asOfDate
    );

    List<TaxJurisdiction> findByRateRange(
            BigDecimal minRate,
            BigDecimal maxRate
    );
}

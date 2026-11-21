
package com.jobbersoft.fuel_tax_calculator.repository;

import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxJurisdictionRepository
        extends JpaRepository<TaxJurisdiction, Long>,
        TaxJurisdictionRepositoryCustom {
}

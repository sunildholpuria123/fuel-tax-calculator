
package com.jobbersoft.fuel_tax_calculator.repository;

import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TaxJurisdictionRepositoryCustomImpl.class)
class TaxJurisdictionRepositoryTest {

    @Autowired
    private TaxJurisdictionRepository repository;

    @Test
    void findActiveByCode_shouldReturnActiveJurisdiction() {
        var result = repository.findActiveByCode(
                "CA", LocalDate.now()
        );

        assertTrue(result.isPresent());
        assertEquals("California", result.get().getName());
    }

    @Test
    void findActiveByCode_shouldReturnEmptyForFutureEffectiveDate() {
        var result = repository.findActiveByCode(
                "CA", LocalDate.of(2020, 1, 1)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByRateRange_shouldReturnMatchingJurisdictions() {
        var results = repository.findByRateRange(
                new BigDecimal("0.0600"),
                new BigDecimal("0.0750")
        );

        assertEquals(3, results.size());
        assertTrue(results.stream()
                .allMatch(j -> j.getBaseRate().compareTo(
                        new BigDecimal("0.0600")) >= 0
                        && j.getBaseRate().compareTo(
                        new BigDecimal("0.0750")) <= 0));
    }
}

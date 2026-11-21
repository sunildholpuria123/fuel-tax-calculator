
package com.jobbersoft.fuel_tax_calculator.repository;

import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;
import com.jobbersoft.fuel_tax_calculator.entity.QTaxJurisdiction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TaxJurisdictionRepositoryCustomImpl
        implements TaxJurisdictionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public TaxJurisdictionRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<TaxJurisdiction> findActiveByCode(
            String code,
            LocalDate asOfDate
    ) {
        QTaxJurisdiction jurisdiction = QTaxJurisdiction.taxJurisdiction;

        TaxJurisdiction result = queryFactory
                .selectFrom(jurisdiction)
                .where(
                        jurisdiction.code.eq(code),
                        jurisdiction.effectiveDate.loe(asOfDate)
                )
                .orderBy(jurisdiction.effectiveDate.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public List<TaxJurisdiction> findByRateRange(
            BigDecimal minRate,
            BigDecimal maxRate
    ) {
        QTaxJurisdiction jurisdiction = QTaxJurisdiction.taxJurisdiction;

        return queryFactory
                .selectFrom(jurisdiction)
                .where(jurisdiction.baseRate.between(minRate, maxRate))
                .orderBy(jurisdiction.baseRate.asc())
                .fetch();
    }
}

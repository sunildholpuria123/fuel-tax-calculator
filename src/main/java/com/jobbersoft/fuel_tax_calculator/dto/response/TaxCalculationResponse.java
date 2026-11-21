
package com.jobbersoft.fuel_tax_calculator.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TaxCalculationResponse(
        Long id,
        String shipmentId,
        String jurisdictionCode,
        String jurisdictionName,
        BigDecimal fuelQuantity,
        BigDecimal pricePerGallon,
        BigDecimal baseRate,
        BigDecimal calculatedTax,
        LocalDateTime calculatedOn
) {
}

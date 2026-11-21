
package com.jobbersoft.fuel_tax_calculator.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TaxCalculationRequest(

        @NotBlank(message = "Shipment ID is required")
        @Size(max = 100)
        String shipmentId,

        @NotBlank(message = "Jurisdiction code is required")
        @Size(max = 20)
        String jurisdictionCode,

        @NotNull(message = "Fuel quantity is required")
        @DecimalMin(value = "0.001", message = "Fuel quantity must be positive")
        BigDecimal fuelQuantity,

        @NotNull(message = "Price per gallon is required")
        @DecimalMin(value = "0.0001", message = "Price per gallon must be positive")
        BigDecimal pricePerGallon
) {
}

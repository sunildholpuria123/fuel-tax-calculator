
package com.jobbersoft.fuel_tax_calculator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "fuel_tax_calculations",
        indexes = {
                @Index(name = "idx_calculation_shipment", columnList = "shipment_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelTaxCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Shipment ID is required")
    @Size(max = 100, message = "Shipment ID cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String shipmentId;

    @NotNull(message = "Tax jurisdiction is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jurisdiction_id", nullable = false)
    private TaxJurisdiction jurisdiction;

    @NotNull(message = "Fuel quantity is required")
    @DecimalMin(value = "0.001", message = "Fuel quantity must be at least 0.001")
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal fuelQuantity;

    @NotNull(message = "Price per gallon is required")
    @DecimalMin(value = "0.0001", message = "Price per gallon must be positive")
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal pricePerGallon;

    @NotNull(message = "Calculated tax is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Calculated tax cannot be negative")
    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal calculatedTax;

    @NotNull(message = "Calculation date is required")
    @Column(nullable = false)
    private LocalDateTime calculatedOn;
}

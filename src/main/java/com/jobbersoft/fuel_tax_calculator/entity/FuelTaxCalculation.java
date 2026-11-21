
package com.jobbersoft.fuel_tax_calculator.entity;

import jakarta.persistence.*;
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

    @Column(nullable = false, length = 100)
    private String shipmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jurisdiction_id", nullable = false)
    private TaxJurisdiction jurisdiction;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal fuelQuantity;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal pricePerGallon;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal calculatedTax;

    @Column(nullable = false)
    private LocalDateTime calculatedOn;
}

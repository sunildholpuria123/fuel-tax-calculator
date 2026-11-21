
package com.jobbersoft.fuel_tax_calculator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "tax_jurisdictions",
        indexes = {
                @Index(name = "idx_jurisdiction_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxJurisdiction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Jurisdiction code is required")
    @Size(max = 20, message = "Jurisdiction code cannot exceed 20 characters")
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank(message = "Jurisdiction name is required")
    @Size(max = 100, message = "Jurisdiction name cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Base rate is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Base rate must be greater than zero")
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal baseRate;

    @NotNull(message = "Effective date is required")
    @Column(nullable = false)
    private LocalDate effectiveDate;
}


package com.jobbersoft.fuel_tax_calculator.service.impl;

import com.jobbersoft.fuel_tax_calculator.dto.request.TaxCalculationRequest;
import com.jobbersoft.fuel_tax_calculator.dto.response.TaxCalculationResponse;
import com.jobbersoft.fuel_tax_calculator.entity.FuelTaxCalculation;
import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;
import com.jobbersoft.fuel_tax_calculator.exception.JurisdictionNotFoundException;
import com.jobbersoft.fuel_tax_calculator.repository.FuelTaxCalculationRepository;
import com.jobbersoft.fuel_tax_calculator.repository.TaxJurisdictionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuelTaxCalculationServiceTest {

    @Mock
    private TaxJurisdictionRepository jurisdictionRepository;

    @Mock
    private FuelTaxCalculationRepository calculationRepository;

    @InjectMocks
    private FuelTaxCalculationServiceImpl service;

    @Test
    void calculateTax_shouldCalculateAndSaveSuccessfully() {

        TaxCalculationRequest request = new TaxCalculationRequest(
                "SHIP-1001",
                "CA",
                new BigDecimal("100"),
                new BigDecimal("3.50")
        );

        TaxJurisdiction jurisdiction = TaxJurisdiction.builder()
                .id(1L)
                .code("CA")
                .name("California")
                .baseRate(new BigDecimal("0.0750"))
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .build();

        when(jurisdictionRepository.findActiveByCode(
                eq("CA"), any(LocalDate.class)))
                .thenReturn(Optional.of(jurisdiction));

        when(calculationRepository.save(any(FuelTaxCalculation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaxCalculationResponse response = service.calculateTax(request);

        assertEquals("SHIP-1001", response.shipmentId());
        assertEquals("CA", response.jurisdictionCode());
        assertEquals(new BigDecimal("26.2500"), response.calculatedTax());

        ArgumentCaptor<FuelTaxCalculation> captor =
                ArgumentCaptor.forClass(FuelTaxCalculation.class);

        verify(calculationRepository).save(captor.capture());

        assertEquals(
                new BigDecimal("26.2500"),
                captor.getValue().getCalculatedTax()
        );
    }

    @Test
    void calculateTax_shouldThrowWhenJurisdictionNotFound() {

        TaxCalculationRequest request = new TaxCalculationRequest(
                "SHIP-1002",
                "INVALID",
                new BigDecimal("100"),
                new BigDecimal("3.50")
        );

        when(jurisdictionRepository.findActiveByCode(
                eq("INVALID"), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                JurisdictionNotFoundException.class,
                () -> service.calculateTax(request)
        );

        verify(calculationRepository, never()).save(any());
    }

    @Test
    void getTaxHistory_shouldReturnShipmentHistory() {

        TaxJurisdiction jurisdiction = TaxJurisdiction.builder()
                .id(1L)
                .code("CA")
                .name("California")
                .baseRate(new BigDecimal("0.0750"))
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .build();

        FuelTaxCalculation calculation = FuelTaxCalculation.builder()
                .id(1L)
                .shipmentId("SHIP-1001")
                .jurisdiction(jurisdiction)
                .fuelQuantity(new BigDecimal("100"))
                .pricePerGallon(new BigDecimal("3.50"))
                .calculatedTax(new BigDecimal("26.2500"))
                .calculatedOn(LocalDateTime.now())
                .build();

        when(calculationRepository
                .findByShipmentIdOrderByCalculatedOnDesc("SHIP-1001"))
                .thenReturn(List.of(calculation));

        List<TaxCalculationResponse> history =
                service.getTaxHistory("SHIP-1001");

        assertEquals(1, history.size());
        assertEquals("SHIP-1001", history.get(0).shipmentId());
        assertEquals(
                new BigDecimal("26.2500"),
                history.get(0).calculatedTax()
        );

        verify(calculationRepository)
                .findByShipmentIdOrderByCalculatedOnDesc("SHIP-1001");
    }

    @Test
    void getTaxHistory_shouldReturnEmptyListWhenNoHistoryExists() {

        when(calculationRepository
                .findByShipmentIdOrderByCalculatedOnDesc("SHIP-9999"))
                .thenReturn(List.of());

        List<TaxCalculationResponse> history =
                service.getTaxHistory("SHIP-9999");

        assertTrue(history.isEmpty());
    }

    @Test
    void calculateTax_shouldRoundResultToFourDecimalPlaces() {

        TaxCalculationRequest request = new TaxCalculationRequest(
                "SHIP-1003",
                "CA",
                new BigDecimal("10"),
                new BigDecimal("3.333")
        );

        TaxJurisdiction jurisdiction = TaxJurisdiction.builder()
                .id(1L)
                .code("CA")
                .name("California")
                .baseRate(new BigDecimal("0.0750"))
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .build();

        when(jurisdictionRepository.findActiveByCode(
                eq("CA"), any(LocalDate.class)))
                .thenReturn(Optional.of(jurisdiction));

        when(calculationRepository.save(any(FuelTaxCalculation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaxCalculationResponse response = service.calculateTax(request);

        assertEquals(
                new BigDecimal("2.4998"),
                response.calculatedTax()
        );
    }

    @Test
    void calculateTax_shouldSaveCalculationExactlyOnce() {

        TaxCalculationRequest request = new TaxCalculationRequest(
                "SHIP-1004",
                "CA",
                new BigDecimal("100"),
                new BigDecimal("3.50")
        );

        TaxJurisdiction jurisdiction = TaxJurisdiction.builder()
                .id(1L)
                .code("CA")
                .name("California")
                .baseRate(new BigDecimal("0.0750"))
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .build();

        when(jurisdictionRepository.findActiveByCode(
                eq("CA"), any(LocalDate.class)))
                .thenReturn(Optional.of(jurisdiction));

        when(calculationRepository.save(any(FuelTaxCalculation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.calculateTax(request);

        verify(calculationRepository, times(1))
                .save(any(FuelTaxCalculation.class));
    }
}


package com.jobbersoft.fuel_tax_calculator.controller;

import com.jobbersoft.fuel_tax_calculator.dto.request.TaxCalculationRequest;
import com.jobbersoft.fuel_tax_calculator.dto.response.TaxCalculationResponse;
import com.jobbersoft.fuel_tax_calculator.exception.JurisdictionNotFoundException;
import com.jobbersoft.fuel_tax_calculator.service.FuelTaxCalculationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FuelTaxController.class)
class FuelTaxControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuelTaxCalculationService service;

    @Test
    void calculateTaxShouldReturn201() throws Exception {

        TaxCalculationResponse response = new TaxCalculationResponse(
                1L,
                "SHIP-1001",
                "CA",
                "California",
                new BigDecimal("100"),
                new BigDecimal("3.50"),
                new BigDecimal("0.0750"),
                new BigDecimal("26.2500"),
                LocalDateTime.now()
        );

        when(service.calculateTax(any(TaxCalculationRequest.class)))
                .thenReturn(response);

        String request = """
                {
                    "shipmentId": "SHIP-1001",
                    "jurisdictionCode": "CA",
                    "fuelQuantity": 100,
                    "pricePerGallon": 3.50
                }
                """;

        mockMvc.perform(post("/v1/tax/fuel/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipmentId").value("SHIP-1001"))
                .andExpect(jsonPath("$.calculatedTax").value(26.25));
    }

    @Test
    void calculateTaxShouldReturn400ForInvalidRequest() throws Exception {

        String request = """
                {
                    "shipmentId": "",
                    "jurisdictionCode": "CA",
                    "fuelQuantity": 100,
                    "pricePerGallon": 3.50
                }
                """;

        mockMvc.perform(post("/v1/tax/fuel/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.shipmentId").exists());
    }

    @Test
    void calculateTaxShouldReturn404ForUnknownJurisdiction() throws Exception {

        when(service.calculateTax(any(TaxCalculationRequest.class)))
                .thenThrow(new JurisdictionNotFoundException(
                        "No active jurisdiction found: INVALID"
                ));

        String request = """
                {
                    "shipmentId": "SHIP-1001",
                    "jurisdictionCode": "INVALID",
                    "fuelQuantity": 100,
                    "pricePerGallon": 3.50
                }
                """;

        mockMvc.perform(post("/v1/tax/fuel/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getHistoryShouldReturn200() throws Exception {

        when(service.getTaxHistory("SHIP-1001"))
                .thenReturn(List.of());

        mockMvc.perform(get("/v1/tax/fuel/history/SHIP-1001"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}


package com.jobbersoft.fuel_tax_calculator.controller;

import com.jobbersoft.fuel_tax_calculator.entity.TaxJurisdiction;
import com.jobbersoft.fuel_tax_calculator.repository.TaxJurisdictionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaxJurisdictionController.class)
class TaxJurisdictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxJurisdictionRepository jurisdictionRepository;

    @Test
    void getJurisdictions_shouldReturnAllJurisdictions() throws Exception {
        TaxJurisdiction california = TaxJurisdiction.builder()
                .id(1L)
                .code("CA")
                .name("California")
                .baseRate(new BigDecimal("0.0750"))
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .build();

        TaxJurisdiction texas = TaxJurisdiction.builder()
                .id(2L)
                .code("TX")
                .name("Texas")
                .baseRate(new BigDecimal("0.0625"))
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .build();

        when(jurisdictionRepository.findAll())
                .thenReturn(List.of(california, texas));

        mockMvc.perform(get("/v1/tax/fuel/jurisdictions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("CA"))
                .andExpect(jsonPath("$[0].name").value("California"))
                .andExpect(jsonPath("$[1].code").value("TX"))
                .andExpect(jsonPath("$[1].name").value("Texas"));
    }

    @Test
    void getJurisdictions_shouldReturnEmptyListWhenNoJurisdictionsExist()
            throws Exception {

        when(jurisdictionRepository.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/v1/tax/fuel/jurisdictions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}

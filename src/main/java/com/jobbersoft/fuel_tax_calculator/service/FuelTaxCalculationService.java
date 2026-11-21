
package com.jobbersoft.fuel_tax_calculator.service;

import com.jobbersoft.fuel_tax_calculator.dto.request.TaxCalculationRequest;
import com.jobbersoft.fuel_tax_calculator.dto.response.TaxCalculationResponse;

import java.util.List;

public interface FuelTaxCalculationService {

    TaxCalculationResponse calculateTax(TaxCalculationRequest request);

    List<TaxCalculationResponse> getTaxHistory(String shipmentId);
}

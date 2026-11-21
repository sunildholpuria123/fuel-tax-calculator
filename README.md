# Fuel Tax Calculator Service

A RESTful Fuel Tax Calculator built using Java 17, Spring Boot, Spring Data JPA, QueryDSL, and PostgreSQL. The service calculates fuel taxes using jurisdiction-specific rates and maintains shipment-level tax calculation history.

## Tech Stack

- Java 17
- Spring Boot 4.2.0
- Spring Data JPA / Hibernate
- QueryDSL 5.1.0 (Jakarta)
- PostgreSQL
- H2 Database for testing
- Flyway Database Migration
- Maven
- Lombok
- JUnit 5
- Mockito
- Jakarta Bean Validation

## Features

- Calculate fuel tax based on jurisdiction-specific tax rates.
- Retrieve tax calculation history by shipment ID.
- Retrieve available tax jurisdictions.
- Find active jurisdictions by code and effective date using QueryDSL.
- Search jurisdictions within a specified tax-rate range using QueryDSL.
- Validate incoming API requests.
- Centralized exception handling with meaningful HTTP status codes.
- Database versioning and sample jurisdiction data through Flyway.
- Transaction management at the service layer.
- Unit, repository, controller, and application context tests.

## Project Structure

```text
src/main/java/com/jobbersoft/fuel_tax_calculator/
  config/
  controller/
  dto/
    request/
    response/
  entity/
  exception/
  repository/
  service/
    impl/
  FuelTaxCalculatorApplication.java

src/main/resources/
  db/migration/
    V1__create_fuel_tax_tables.sql
    V2__insert_sample_jurisdictions.sql
  application.yml

src/test/java/com/jobbersoft/fuel_tax_calculator/
  controller/
  repository/
  service/
    impl/
  FuelTaxCalculatorApplicationTests.java

src/test/resources/
  application-test.yml
```

## Domain Model

### TaxJurisdiction

Represents a fuel tax jurisdiction.

- `id`: Unique identifier.
- `code`: Unique jurisdiction code.
- `name`: Jurisdiction name.
- `baseRate`: Tax rate.
- `effectiveDate`: Date from which the tax rate becomes effective.

### FuelTaxCalculation

Stores individual fuel tax calculations.

- `id`: Unique calculation identifier.
- `shipmentId`: Shipment reference.
- `jurisdiction`: Associated tax jurisdiction.
- `fuelQuantity`: Fuel quantity.
- `pricePerGallon`: Price per gallon.
- `calculatedTax`: Calculated tax amount.
- `calculatedOn`: Calculation timestamp.

The relationship between `FuelTaxCalculation` and `TaxJurisdiction` is implemented using JPA `@ManyToOne`.

## Tax Calculation Formula

```text
Calculated Tax =
Fuel Quantity × Price Per Gallon × Tax Rate
```

Example:

```text
Fuel Quantity   = 100
Price Per Gallon = 3.50
Tax Rate         = 0.0750

Calculated Tax = 100 × 3.50 × 0.0750
               = 26.2500
```

Calculations use Java `BigDecimal` to avoid floating-point precision issues. Results are rounded to four decimal places using `HALF_UP`.

## REST API Endpoints

### 1. Calculate Fuel Tax

**POST** `/v1/tax/fuel/calculate`

Request:

```json
{
  "shipmentId": "SHIP-1001",
  "jurisdictionCode": "CA",
  "fuelQuantity": 100,
  "pricePerGallon": 3.50
}
```

Successful response: **HTTP 201 Created**

```json
{
  "id": 1,
  "shipmentId": "SHIP-1001",
  "jurisdictionCode": "CA",
  "jurisdictionName": "California",
  "fuelQuantity": 100,
  "pricePerGallon": 3.50,
  "baseRate": 0.0750,
  "calculatedTax": 26.2500,
  "calculatedOn": "2026-11-21T17:04:13"
}
```

### 2. Get Shipment Tax History

**GET** `/v1/tax/fuel/history/SHIP-1001`

Returns tax calculation records for the specified shipment, ordered by calculation date in descending order.

### 3. Get Tax Jurisdictions

**GET** `/v1/tax/fuel/jurisdictions`

Returns all available tax jurisdictions.

## QueryDSL Custom Repository Methods

The `TaxJurisdictionRepository
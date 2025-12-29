# Fimea National High-Risk Medicines Classifications CDS Hooks Service

A [CDS Hooks](https://cds-hooks.hl7.org/) service that checks patient medications against the Finnish [National High-Risk Medicines Classification](https://fimea.fi/en/databases_and_registers/national-risk-medicines-classification) from Fimea.

## Disclaimer

**NOT FOR CLINICAL USE**

This service is a demonstration project developed for the Nordic Health Data Hackathon in Helsinki, January 2026. It is intended solely to showcase the CDS Hooks standard and technical integration capabilities.

**This software:**
- Is NOT validated for clinical use
- Is NOT approved or certified by any regulatory authority
- Should NOT be used for any real healthcare decision-making
- Should NOT be used in production clinical environments
- Is provided "AS IS" without warranty of any kind

Healthcare professionals must rely on their clinical judgment and validated, approved systems for patient care decisions. The developers assume no liability for any consequences resulting from the use or misuse of this software.

## Overview

This service:
- Integrates with HAPI FHIR Server (v8.6.1) as a CDS Hooks provider
- Provides two CDS Hooks:
  - `patient-view` - Checks patient's existing medications when viewing patient record
  - `order-select` - Checks medications being prescribed when creating orders
- Matches medications by VNR (Finnish package code) and ATC codes
- Returns warnings for medications classified as high-risk
- Filters results by route of administration when applicable

## Requirements

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose**

## Data Files

The project requires data files from Fimea's databases. These files are licensed and cannot be included in the repository.

### Required Files

The following files must be placed in the `data/` directory:

1. `riskilaakeluokitus.xml` - Risk medicines classification
2. `Riskilaakeluokitus2024.xsd` - XML schema of the risk medicines classification
3. `pakkaus_nolla.txt` - Medicine package data (from Basic Register)
4. `laakeaine.txt` - Medicine substance data (from Basic Register) - *downloaded but not currently used*

### Obtaining Data Files

The download URLs are already configured in `pom.xml`. Simply run:

```bash
mvn antrun:run@getData
```

This will download all required files from Fimea's databases to the `data/` directory.

**Note:** The files are available from:
- [Fimea Risk Medicines Classification](https://fimea.fi/en/databases_and_registers/national-risk-medicines-classification)
- [Fimea Basic Register](https://fimea.fi/en/databases_and_registers/basic-register)

## Building the Project

1. Ensure data files are in place (see above)

2. Build the project:

```bash
mvn clean package
```

This will:
- Compile the Java code
- Run Checkstyle validation
- Package the JAR file
- Copy the JAR and dependencies to `hapi-extra-classes/`

## Running Locally with Docker

The Docker setup uses the official HAPI FHIR server image and mounts the custom CDS Hooks service as extra classes.

**How it works:**
- The `hapi-extra-classes/` directory is mounted into the container at `/app/extra-classes`
- HAPI FHIR automatically loads JARs from this directory at startup
- Spring Boot auto-configuration discovers the `CdsServiceDefinitions` class via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- The CDS Hooks service becomes available at the `/cds-services` endpoint

**To run:**

1. Start the HAPI FHIR server with the CDS Hooks service:

```bash
docker compose up
```

The server will start on `http://localhost:8080`

2. To stop the server:

```bash
docker compose down
```

## Testing the Service

### 1. Discovery Endpoint

Get the list of available CDS services:

```bash
curl http://localhost:8080/cds-services
```

Expected response:
```json
{
  "services": [
    {
      "id": "risk-medicines-pv",
      "hook": "patient-view",
      "title": "High-Risk Medicines Check Patient View",
      "description": "A service that checks the patient's medications against the risk medicines list",
      "prefetch": {
        "medications": "MedicationStatement?patient={{context.patientId}}"
      }
    },
    {
      "id": "risk-medicines-os",
      "hook": "order-select",
      "title": "High-Risk Medicines Check Prescription",
      "description": "A service that checks the medications being prescribed against the risk medicines list",
      "prefetch": {}
    }
  ]
}
```

### 2. Patient View Hook

Test the patient-view hook with a sample request:

```bash
curl -X POST http://localhost:8080/cds-services/risk-medicines-pv \
  -H "Content-Type: application/json" \
  -d @src/test/resources/request-patient-view.json
```

### 3. Order Select Hook

Test the order-select hook with a sample request:

```bash
curl -X POST http://localhost:8080/cds-services/risk-medicines-os \
  -H "Content-Type: application/json" \
  -d @src/test/resources/request-order-select.json
```

Expected response format (for both hooks):
```json
{
  "cards": [
    {
      "summary": "High-Risk Medicine: MAREVAN 3 mg tabletti (Vnr: 486571)",
      "indicator": "warning",
      "detail": "**Medication:** MAREVAN 3 mg tabletti\n**Strength:** 3 mg\n...",
      "source": {
        "label": "The National High-Risk Medicines Classification by Fimea",
        "url": "https://fimea.fi/en/databases_and_registers/national-risk-medicines-classification"
      }
    }
  ]
}
```

## Project Structure

```
.
├── data/                           # Data files (not in repository)
├── hapi-extra-classes/             # Generated JARs loaded by HAPI FHIR
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── no/ntnu/folk/adamzk/
│   │   │       ├── model/          # Data models for risk classifications
│   │   │       ├── repository/     # Data repositories
│   │   │       ├── service/        # CDS Hooks service implementation
│   │   │       └── CdsServiceDefinitions.java
│   │   └── resources/
│   │       └── META-INF/spring/    # Spring Boot autoconfiguration
│   └── test/
│       └── resources/
│           ├── request-patient-view.json   # Sample patient-view hook request
│           └── request-order-select.json   # Sample order-select hook request
├── docker-compose.yml
├── hapi.application.yaml           # HAPI FHIR configuration
└── pom.xml
```

## Configuration

Environment variables are configured in `docker-compose.yml`:

- `NO_NTNU_FOLK_ADAMZK_RISK_MEDICATIONS` - Path to risk medicines XML file
- `NO_NTNU_FOLK_ADAMZK_MEDICINE_PACKAGES` - Path to medicine packages file

## Development

### Rebuilding After Changes

After making code changes:

```bash
mvn clean package
docker compose down
docker compose up
```

### Code Style

The project uses Checkstyle for code quality. Run validation:

```bash
mvn checkstyle:check
```

## License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). 

**Note:** The data files from Fimea are subject to their own licensing terms and are not included in this repository.

## References

- [CDS Hooks Specification](https://cds-hooks.org/)
- [HAPI FHIR](https://hapifhir.io/)
- [Fimea Risk Medicines Classification](https://fimea.fi/en/databases_and_registers/national-risk-medicines-classification)
- [Fimea Basic Register](https://fimea.fi/en/databases_and_registers/basic-register)

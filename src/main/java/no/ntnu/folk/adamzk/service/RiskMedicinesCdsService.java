package no.ntnu.folk.adamzk.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.MedicationRequest;
import org.hl7.fhir.r5.model.MedicationStatement;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.api.server.cdshooks.CdsServiceRequestContextJson;
import ca.uhn.fhir.rest.api.server.cdshooks.CdsServiceRequestJson;
import ca.uhn.hapi.fhir.cdshooks.api.CdsService;
import ca.uhn.hapi.fhir.cdshooks.api.CdsServicePrefetch;
import ca.uhn.hapi.fhir.cdshooks.api.json.CdsServiceIndicatorEnum;
import ca.uhn.hapi.fhir.cdshooks.api.json.CdsServiceResponseCardJson;
import ca.uhn.hapi.fhir.cdshooks.api.json.CdsServiceResponseCardSourceJson;
import ca.uhn.hapi.fhir.cdshooks.api.json.CdsServiceResponseJson;
import no.ntnu.folk.adamzk.model.MedicinePackage;
import no.ntnu.folk.adamzk.model.RiskMedicineClassification;
import no.ntnu.folk.adamzk.repository.MedicinePackageRepository;
import no.ntnu.folk.adamzk.repository.RiskMedicinesRepository;

@Component
public class RiskMedicinesCdsService {

    private static final String VNR_SYSTEM = "https://pharmaca.fi/vnr";

    private static final String ROUTE_SYSTEM = "https://fimea.fi/risk-medicines/route-of-administration";

    private MedicinePackageRepository medicinePackageRepository;

    private RiskMedicinesRepository riskMedicinesRepository;

    public RiskMedicinesCdsService(
            MedicinePackageRepository medicinePackageRepository,
            RiskMedicinesRepository riskMedicinesRepository) {
        this.medicinePackageRepository = medicinePackageRepository;
        this.riskMedicinesRepository = riskMedicinesRepository;
    }

    @CdsService(value = "risk-medicines-pv", hook = "patient-view",
            title = "High-Risk Medicines Check Patient View",
            description = "A service that checks the patient's medications against the risk medicines list",
            prefetch = {
                    @CdsServicePrefetch(value = "medications",
                            query = "MedicationStatement?patient={{context.patientId}}")
    })
    public CdsServiceResponseJson riskMedicinesPatientView(CdsServiceRequestJson request) {
        CdsServiceResponseJson response = new CdsServiceResponseJson();
        List<MedicationStatement> medicationStatements = extractMedicationStatements(request);
        for (MedicationStatement medicationStatement : medicationStatements) {
            String vnr = extractVnr(medicationStatement);
            String routeOfAdministration = extractRoute(medicationStatement);
            List<MedicinePackage> packages = medicinePackageRepository.findByVnr(vnr);
            for (MedicinePackage medicinePackage : packages) {
                String atcCode = medicinePackage.getAtcCode();
                if (StringUtils.isNotEmpty(atcCode)) {
                    List<RiskMedicineClassification> riskMedicines = riskMedicinesRepository
                            .findByAtcCodeAndRoute(atcCode, routeOfAdministration);
                    for (RiskMedicineClassification riskMedicine : riskMedicines) {
                        CdsServiceResponseCardJson card = createRiskMedicineCard(medicinePackage, riskMedicine);
                        response.addCard(card);
                    }
                }
            }
        }
        return response;
    }

    @CdsService(value = "risk-medicines-os", hook = "order-select",
            title = "High-Risk Medicines Check Prescription",
            description = "A service that checks the medications being prescribed against the risk medicines list",
            prefetch = {})
    public CdsServiceResponseJson riskMedicinesOrderSelect(CdsServiceRequestJson request) {
        CdsServiceResponseJson response = new CdsServiceResponseJson();
        List<MedicationRequest> medicationRequests = extractMedicationRequests(request);
        for (MedicationRequest medicationRequest : medicationRequests) {
            String vnr = extractVnr(medicationRequest);
            String routeOfAdministration = extractRoute(medicationRequest);
            List<MedicinePackage> packages = medicinePackageRepository.findByVnr(vnr);
            for (MedicinePackage medicinePackage : packages) {
                String atcCode = medicinePackage.getAtcCode();
                if (StringUtils.isNotEmpty(atcCode)) {
                    List<RiskMedicineClassification> riskMedicines = riskMedicinesRepository
                            .findByAtcCodeAndRoute(atcCode, routeOfAdministration);
                    for (RiskMedicineClassification riskMedicine : riskMedicines) {
                        CdsServiceResponseCardJson card = createRiskMedicineCard(medicinePackage, riskMedicine);
                        response.addCard(card);
                    }
                }
            }
        }
        return response;
    }

    private List<MedicationStatement> extractMedicationStatements(CdsServiceRequestJson request) {
        List<MedicationStatement> medicationStatements = new ArrayList<>();
        if (request.getPrefetchKeys() != null && request.getPrefetchKeys().contains("medications")) {
            IBaseResource medicationsResource = request.getPrefetch("medications");
            if (medicationsResource instanceof Bundle) {
                Bundle medicationsBundle = (Bundle) medicationsResource;
                medicationsBundle.getEntry().forEach(entry -> {
                    if (entry.getResource() instanceof MedicationStatement) {
                        MedicationStatement medicationStatement = (MedicationStatement) entry.getResource();
                        medicationStatements.add(medicationStatement);
                    }
                });
            }
        }
        return medicationStatements;
    }

    private List<MedicationRequest> extractMedicationRequests(CdsServiceRequestJson request) {
        List<MedicationRequest> medicationRequests = new ArrayList<>();
        CdsServiceRequestContextJson context = request.getContext();
        if (context != null && context.containsKey("draftOrders")) {
            Object medicationsResource = context.get("draftOrders");
            if (medicationsResource instanceof Bundle) {
                Bundle medicationsBundle = (Bundle) medicationsResource;
                medicationsBundle.getEntry().forEach(entry -> {
                    if (entry.getResource() instanceof MedicationRequest) {
                        MedicationRequest medicationRequest = (MedicationRequest) entry.getResource();
                        medicationRequests.add(medicationRequest);
                    }
                });
            }
        }
        return medicationRequests;
    }

    private String extractVnr(MedicationStatement medicationStatement) {
        return medicationStatement.getMedication().getConcept().getCoding().stream()
                .filter(coding -> VNR_SYSTEM.equals(coding.getSystem()))
                .map(Coding::getCode)
                .findFirst()
                .orElse(null);
    }

    private String extractVnr(MedicationRequest medicationRequest) {
        return medicationRequest.getMedication().getConcept().getCoding().stream()
                .filter(coding -> VNR_SYSTEM.equals(coding.getSystem()))
                .map(Coding::getCode)
                .findFirst()
                .orElse(null);
    }

    private String extractRoute(MedicationStatement medicationStatement) {
        return medicationStatement.getDosage().stream()
                .filter(dosage -> dosage.hasRoute())
                .map(dosage -> dosage.getRoute().getCoding().stream()
                        .filter(coding -> ROUTE_SYSTEM.equals(coding.getSystem()))
                        .map(Coding::getCode)
                        .findFirst()
                        .orElse(null))
                .findFirst()
                .orElse(null);
    }

    private String extractRoute(MedicationRequest medicationRequest) {
        return medicationRequest.getDosageInstruction().stream()
                .filter(dosage -> dosage.hasRoute())
                .map(dosage -> dosage.getRoute().getCoding().stream()
                        .filter(coding -> ROUTE_SYSTEM.equals(coding.getSystem()))
                        .map(Coding::getCode)
                        .findFirst()
                        .orElse(null))
                .findFirst()
                .orElse(null);
    }

    private CdsServiceResponseCardJson createRiskMedicineCard(MedicinePackage medicinePackage,
            RiskMedicineClassification riskMedication) {
        CdsServiceResponseCardJson card = new CdsServiceResponseCardJson();

        // Summary
        card.setSummary("High-Risk Medicine: " + medicinePackage.getMedicineName()
                + " (Vnr: " + medicinePackage.getVnr() + ")");

        // Detail
        StringBuilder detail = new StringBuilder();
        detail.append("**Medication:** ").append(medicinePackage.getMedicineName()).append("\n");
        detail.append("**Strength:** ").append(medicinePackage.getStrength()).append("\n");
        detail.append("**Dose Form:** ").append(medicinePackage.getDoseForm()).append("\n");
        detail.append("**ATC Code:** ").append(medicinePackage.getAtcCode()).append("\n");
        detail.append("**Producer:** ").append(medicinePackage.getProducer()).append("\n\n");

        String title = riskMedication.getTitle();
        if (StringUtils.isNotEmpty(title)) {
            detail.append("**Risk Category:** ").append(title).append("\n\n");
        }

        if (!riskMedication.getSeriousConsequences().isEmpty()) {
            detail.append("**Serious Consequences:**\n\n");
            riskMedication.getSeriousConsequences().forEach(consequence -> {
                if (consequence.getConsequence() != null) {
                    String value = consequence.getConsequence().getValue();
                    if (StringUtils.isNotEmpty(value)) {
                        detail.append("- ").append(value);
                        String description = consequence.getDescription();
                        if (StringUtils.isNotEmpty(description)) {
                            detail.append(": ").append(description);
                        }
                        detail.append("\n");
                    }
                }
            });
            detail.append("\n");
        }

        if (!riskMedication.getMedicineRelatedRisks().isEmpty()) {
            detail.append("**Medication-Related Risks:**\n\n");
            riskMedication.getMedicineRelatedRisks().forEach(risk -> {
                if (risk.getRisk() != null) {
                    String value = risk.getRisk().getValue();
                    if (StringUtils.isNotEmpty(value)) {
                        detail.append("- ").append(value);
                        String description = risk.getDescription();
                        if (StringUtils.isNotEmpty(description)) {
                            detail.append(": ").append(description);
                        }
                        detail.append("\n");
                    }
                }
            });
            detail.append("\n");
        }

        if (!riskMedication.getProcessRelatedRisks().isEmpty()) {
            detail.append("**Process-Related Risks:**\n\n");
            riskMedication.getProcessRelatedRisks().forEach(risk -> {
                if (risk.getMedicationPhase() != null) {
                    String value = risk.getMedicationPhase().getValue();
                    if (StringUtils.isNotEmpty(value)) {
                        detail.append("- ").append(value);
                        String description = risk.getDescription();
                        if (StringUtils.isNotEmpty(description)) {
                            detail.append(": ").append(description);
                        }
                        detail.append("\n");
                    }
                }
            });
            detail.append("\n");
        }

        card.setDetail(detail.toString());

        // Indicator
        card.setIndicator(CdsServiceIndicatorEnum.WARNING);

        // Source
        CdsServiceResponseCardSourceJson source = new CdsServiceResponseCardSourceJson();
        source.setLabel("The National High-Risk Medicines Classification by Fimea");
        source.setUrl("https://fimea.fi/en/databases_and_registers/national-risk-medicines-classification");
        card.setSource(source);

        return card;
    }

}

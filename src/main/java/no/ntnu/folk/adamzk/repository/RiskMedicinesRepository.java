package no.ntnu.folk.adamzk.repository;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import no.ntnu.folk.adamzk.model.RiskMedicineClassification;
import no.ntnu.folk.adamzk.model.RiskMedicineClassificationList;

@Service
public class RiskMedicinesRepository {

    private static final Logger logger = LoggerFactory.getLogger(RiskMedicinesRepository.class);

    @Value("${no.ntnu.folk.adamzk.risk-medications}")
    private String riskMedicationsFilePath;

    private List<RiskMedicineClassification> riskMedications = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        logger.info("Initializing Risk medicines information from file: {}", riskMedicationsFilePath);

        try {
            Path path = Paths.get(riskMedicationsFilePath);
            File file = path.toFile();

            JAXBContext context = JAXBContext.newInstance(RiskMedicineClassificationList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            RiskMedicineClassificationList wrapper = (RiskMedicineClassificationList) unmarshaller.unmarshal(file);
            this.riskMedications = wrapper.getRows();

            logger.info("Loaded {} risk medication classification rows", riskMedications.size());

        } catch (JAXBException e) {
            logger.error("Error parsing risk medications XML: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error loading risk medications: {}", e.getMessage(), e);
        }
    }

    /**
     * Find all risk classification rows matching the given ATC code.
     * @param atcCode The ATC code to search for
     * @return List of matching rows
     */
    public List<RiskMedicineClassification> findByAtcCode(String atcCode) {
        if (atcCode == null || atcCode.isEmpty()) {
            return Collections.emptyList();
        }
        return riskMedications.stream()
            .filter(row -> row.matches(atcCode, null))
            .collect(Collectors.toList());
    }

    /**
     * Find all risk classification rows matching the given ATC code and route of administration.
     * @param atcCode The ATC code to search for
     * @param routeId The route of administration ID
     * @return List of matching rows
     */
    public List<RiskMedicineClassification> findByAtcCodeAndRoute(String atcCode, String routeId) {
        if (atcCode == null || atcCode.isEmpty()) {
            return Collections.emptyList();
        }
        return riskMedications.stream()
            .filter(row -> row.matches(atcCode, routeId))
            .collect(Collectors.toList());
    }

}

package no.ntnu.folk.adamzk.repository;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import no.ntnu.folk.adamzk.model.MedicinePackage;

/**
 * Repository for medicine packages loaded from the packages file of the Basic Register.
 * https://fimea.fi/en/databases_and_registers/basic-register
 */
@Service
public class MedicinePackageRepository {

    private static final Logger logger = LoggerFactory.getLogger(MedicinePackageRepository.class);

    @Value("${no.ntnu.folk.adamzk.medicine-packages}")
    private String medicinePackagesFilePath;

    private List<MedicinePackage> medicinePackages = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        logger.info("Initializing medicine packages from file: {}", medicinePackagesFilePath);

        try (Reader reader = Files.newBufferedReader(Paths.get(medicinePackagesFilePath), BasicRegister.CHARSET);
                CSVParser parser = new CSVParser(reader, BasicRegister.CSV_FORMAT)) {

            for (CSVRecord record : parser) {
                MedicinePackage pkg = new MedicinePackage(
                        record.get(BasicRegister.Packages.COL_VNR),
                        record.get(BasicRegister.Packages.COL_ATC_CODE),
                        record.get(BasicRegister.Packages.COL_MEDICINE_NAME),
                        record.get(BasicRegister.Packages.COL_STRENGTH),
                        record.get(BasicRegister.Packages.COL_DOSE_FORM),
                        record.get(BasicRegister.Packages.COL_PRODUCER));
                medicinePackages.add(pkg);
            }

            logger.info("Loaded {} medicine packages", medicinePackages.size());

        } catch (IOException e) {
            logger.error("Error reading medicine packages file: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error parsing medicine packages CSV: {}", e.getMessage(), e);
        }
    }

    /**
     * Find medicine packages by VNR number.
     * 
     * @param vnr The VNR number to search for
     * @return List of matching packages
     */
    public List<MedicinePackage> findByVnr(String vnr) {
        if (vnr == null || vnr.isEmpty()) {
            return Collections.emptyList();
        }
        return medicinePackages.stream()
                .filter(pkg -> vnr.equals(pkg.getVnr()))
                .toList();
    }

    /**
     * Find medicine packages by ATC code.
     * 
     * @param atcCode The ATC code to search for
     * @return List of matching packages
     */
    public List<MedicinePackage> findByAtcCode(String atcCode) {
        if (atcCode == null || atcCode.isEmpty()) {
            return Collections.emptyList();
        }
        return medicinePackages.stream()
                .filter(pkg -> atcCode.equalsIgnoreCase(pkg.getAtcCode()))
                .toList();
    }

}

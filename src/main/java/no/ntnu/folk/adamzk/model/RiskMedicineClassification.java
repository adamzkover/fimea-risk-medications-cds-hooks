package no.ntnu.folk.adamzk.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Riskilaakeluokitusrivi")
@XmlAccessorType(XmlAccessType.FIELD)
public class RiskMedicineClassification {

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "title")
    private String titleFi;

    @XmlAttribute(name = "titlesv")
    private String titleSv;

    @XmlAttribute(name = "titleen")
    private String titleEn;

    @XmlElement(name = "ATC-koodi")
    private List<CodesetValue> atcCodes = new ArrayList<>();

    @XmlElement(name = "Antoreitti")
    private List<CodesetValue> routesOfAdministration = new ArrayList<>();

    @XmlElement(name = "HoidonKesto")
    private CodesetValue treatmentDuration;

    @XmlElement(name = "Kayttoaihe")
    private CodesetValue indication;

    @XmlElement(name = "VakavaSeuraus")
    private List<SeriousConsequence> seriousConsequences = new ArrayList<>();

    @XmlElement(name = "LaakkeeseenLiittyvaRiski")
    private List<MedicineRelatedRisk> medicineRelatedRisks = new ArrayList<>();

    @XmlElement(name = "LaakehoidonToteutukseenLiittyvaRiskikohta")
    private List<ProcessRelatedRisk> processRelatedRisks = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitleFi() {
        return titleFi;
    }

    public void setTitleFi(String titleFi) {
        this.titleFi = titleFi;
    }

    public String getTitleSv() {
        return titleSv;
    }

    public void setTitleSv(String titleSv) {
        this.titleSv = titleSv;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public List<CodesetValue> getAtcCodes() {
        return atcCodes;
    }

    public void setAtcCodes(List<CodesetValue> atcCodes) {
        this.atcCodes = atcCodes;
    }

    public List<CodesetValue> getRoutesOfAdministration() {
        return routesOfAdministration;
    }

    public void setRoutesOfAdministration(List<CodesetValue> routesOfAdministration) {
        this.routesOfAdministration = routesOfAdministration;
    }

    public CodesetValue getTreatmentDuration() {
        return treatmentDuration;
    }

    public void setTreatmentDuration(CodesetValue treatmentDuration) {
        this.treatmentDuration = treatmentDuration;
    }

    public CodesetValue getIndication() {
        return indication;
    }

    public void setIndication(CodesetValue indication) {
        this.indication = indication;
    }

    public List<SeriousConsequence> getSeriousConsequences() {
        return seriousConsequences;
    }

    public void setSeriousConsequences(List<SeriousConsequence> seriousConsequences) {
        this.seriousConsequences = seriousConsequences;
    }

    public List<MedicineRelatedRisk> getMedicineRelatedRisks() {
        return medicineRelatedRisks;
    }

    public void setMedicineRelatedRisks(List<MedicineRelatedRisk> medicineRelatedRisks) {
        this.medicineRelatedRisks = medicineRelatedRisks;
    }

    public List<ProcessRelatedRisk> getProcessRelatedRisks() {
        return processRelatedRisks;
    }

    public void setProcessRelatedRisks(List<ProcessRelatedRisk> processRelatedRisks) {
        this.processRelatedRisks = processRelatedRisks;
    }

    /**
     * Convenience method to get the title in preferred order: English, Swedish, Finnish.
     * @return the title in the first available language
     */
    public String getTitle() {
        if (titleEn != null && !titleEn.isEmpty()) {
            return titleEn;
        } else if (titleSv != null && !titleSv.isEmpty()) {
            return titleSv;
        } else {
            return titleFi;
        }
    }

    /**
     * Checks if this classification row matches
     * the given ATC code and route of administration.
     * 
     * @param atcCode The ATC code to match
     * @param routeId The route of administration ID to match (optional)
     * @return true if matches
     */
    public boolean matches(String atcCode, String routeId) {
        boolean atcMatches = atcCodes.stream()
                .anyMatch(code -> code.getId().equalsIgnoreCase(atcCode));

        if (!atcMatches) {
            return false;
        }

        if (routeId == null || routeId.isEmpty() || routesOfAdministration.isEmpty()) {
            return true;
        }

        return routesOfAdministration.stream()
                .anyMatch(route -> route.getId().equals(routeId));
    }

}

package no.ntnu.folk.adamzk.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessRelatedRisk {

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "description")
    private String descriptionFi;

    @XmlAttribute(name = "descriptionsv")
    private String descriptionSv;

    @XmlAttribute(name = "descriptionen")
    private String descriptionEn;

    @XmlElement(name = "LaakehoidonVaihe")
    private CodesetValue medicationPhase;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescriptionFi() {
        return descriptionFi;
    }

    public void setDescriptionFi(String descriptionFi) {
        this.descriptionFi = descriptionFi;
    }

    public String getDescriptionSv() {
        return descriptionSv;
    }

    public void setDescriptionSv(String descriptionSv) {
        this.descriptionSv = descriptionSv;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public CodesetValue getMedicationPhase() {
        return medicationPhase;
    }

    public void setMedicationPhase(CodesetValue medicationPhase) {
        this.medicationPhase = medicationPhase;
    }

    /**
     * Convenience method to get the description in preferred language order: English, Swedish, Finnish.
     * @return the value in the first available language
     */
    public String getDescription() {
        if (descriptionEn != null && !descriptionEn.isEmpty()) {
            return descriptionEn;
        } else if (descriptionSv != null && !descriptionSv.isEmpty()) {
            return descriptionSv;
        } else {
            return descriptionFi;
        }
    }

}

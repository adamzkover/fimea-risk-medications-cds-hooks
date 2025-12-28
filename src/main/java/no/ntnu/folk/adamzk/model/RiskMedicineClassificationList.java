package no.ntnu.folk.adamzk.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Riskilaakeluokitus")
@XmlAccessorType(XmlAccessType.FIELD)
public class RiskMedicineClassificationList {

    @XmlElement(name = "Riskilaakeluokitusrivi")
    private List<RiskMedicineClassification> rows = new ArrayList<>();

    public List<RiskMedicineClassification> getRows() {
        return rows;
    }

    public void setRows(List<RiskMedicineClassification> rows) {
        this.rows = rows;
    }

}

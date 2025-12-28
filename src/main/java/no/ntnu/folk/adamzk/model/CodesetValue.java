package no.ntnu.folk.adamzk.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * Represents a coded value with human-readable labels in multiple languages.
 * Corresponds to the Coding data type in FHIR.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CodesetValue {

    @XmlAttribute(name = "listname")
    private String listName;

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "value")
    private String valueFi;

    @XmlAttribute(name = "valuesv")
    private String valueSv;

    @XmlAttribute(name = "valueen")
    private String valueEn;

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValueFi() {
        return valueFi;
    }

    public void setValueFi(String valueFi) {
        this.valueFi = valueFi;
    }

    public String getValueSv() {
        return valueSv;
    }

    public void setValueSv(String valueSv) {
        this.valueSv = valueSv;
    }

    public String getValueEn() {
        return valueEn;
    }

    public void setValueEn(String valueEn) {
        this.valueEn = valueEn;
    }

    /**
     * Convenience method to get the value in preferred language order: English, Swedish, Finnish.
     * @return the value in the first available language
     */
    public String getValue() {
        if (valueEn != null && !valueEn.isEmpty()) {
            return valueEn;
        } else if (valueSv != null && !valueSv.isEmpty()) {
            return valueSv;
        } else {
            return valueFi;
        }
    }

}

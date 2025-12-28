package no.ntnu.folk.adamzk.model;

public class MedicinePackage {

    private String vnr;

    private String atcCode;

    private String medicineName;

    private String strength;

    private String doseForm;

    private String producer;

    public MedicinePackage() {
    }

    public MedicinePackage(String vnr, String atcCode, String medicineName,
        String strength, String doseForm, String producer) {
        this.vnr = vnr;
        this.atcCode = atcCode;
        this.medicineName = medicineName;
        this.strength = strength;
        this.doseForm = doseForm;
        this.producer = producer;
    }

    public String getVnr() {
        return vnr;
    }

    public void setVnr(String vnr) {
        this.vnr = vnr;
    }

    public String getAtcCode() {
        return atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDoseForm() {
        return doseForm;
    }

    public void setDoseForm(String doseForm) {
        this.doseForm = doseForm;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

}

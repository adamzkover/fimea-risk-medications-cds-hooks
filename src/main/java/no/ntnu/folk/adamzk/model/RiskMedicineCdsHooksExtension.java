package no.ntnu.folk.adamzk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.uhn.fhir.rest.api.server.cdshooks.CdsHooksExtension;

public class RiskMedicineCdsHooksExtension extends CdsHooksExtension {

    @JsonProperty("usageRequirements")
    private String usageRequirements;

    public String getUsageRequirements() {
        return usageRequirements;
    }

    public void setUsageRequirements(String usageRequirements) {
        this.usageRequirements = usageRequirements;
    }

}

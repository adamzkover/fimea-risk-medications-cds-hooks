package no.ntnu.folk.adamzk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import ca.uhn.hapi.fhir.cdshooks.svc.CdsHooksContextBooter;

@Configuration
public class CdsHooksContextConfiguration {

    @Bean
    @Primary
    public CdsHooksContextBooter cdsHooksContextBooter() {
        CdsHooksContextBooter booter = new CdsHooksContextBooter();
        booter.setDefinitionsClass(CdsServicesConfiguration.class);
        booter.start();
        return booter;
    }

}

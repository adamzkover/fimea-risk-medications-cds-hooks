package no.ntnu.folk.adamzk;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import no.ntnu.folk.adamzk.service.RiskMedicinesCdsService;

@Configuration
@ComponentScan(basePackages = { "no.ntnu.folk.adamzk.repository", "no.ntnu.folk.adamzk.service" })
public class CdsServicesConfiguration {

    @Bean(name = "cdsServices")
    public List<Object> cdsServices(RiskMedicinesCdsService riskMedicinesCdsService) {
        return List.of(riskMedicinesCdsService);
    }

}

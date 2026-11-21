package com.baarsch_bytes.studentRegDemo.config;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Serves the lab settings that the web UI reads when it loads (frontend/src/lab/labConfig.ts).
//
//   lab.latency.min-ms / lab.latency.max-ms  simulated network delay on every API response
//   lab.mutant                               id of a deliberately faulty UI variant, or empty
//
// Example: mvn spring-boot:run -Dspring-boot.run.arguments=--lab.mutant=P1
@Hidden
@RestController
public class LabConfigController {

    private static final Logger log = LoggerFactory.getLogger(LabConfigController.class);

    @Value("${lab.mutant:}")
    private String mutant;

    @Value("${lab.latency.min-ms:100}")
    private int latencyMinMs;

    @Value("${lab.latency.max-ms:1500}")
    private int latencyMaxMs;

    @PostConstruct
    void validate() {
        if (!mutant.matches("[A-Za-z0-9]{0,10}")) {
            throw new IllegalArgumentException("lab.mutant must be a short id such as P1, not: " + mutant);
        }
        if (!mutant.isEmpty()) {
            log.warn("*** UI MUTANT {} IS ACTIVE: the web UI is deliberately faulty ***", mutant);
        }
        log.info("Simulated network latency: {} to {} ms", latencyMinMs, latencyMaxMs);
    }

    @GetMapping(value = "/lab-config.js", produces = "application/javascript")
    public String labConfig() {
        return "window.LAB_CONFIG = { mutant: '" + mutant + "', latencyMinMs: " + latencyMinMs
                + ", latencyMaxMs: " + latencyMaxMs + " };\n";
    }
}

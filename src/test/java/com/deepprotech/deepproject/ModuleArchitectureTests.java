package com.deepprotech.deepproject;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModuleArchitectureTests {

    @Test
    void verifyModules() {
        ApplicationModules.of(InternalApiApplication.class).verify();
    }

    @Test
    void documentModules() {
        new Documenter(ApplicationModules.of(InternalApiApplication.class))
                .writeDocumentation();
    }
}
package io.github.mianalysis.mia.module.ObjectProcessing.Identification;

import io.github.mianalysis.mia.module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

public class RidgeDetectionTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new RidgeDetection(null).getDescription());
    }
}
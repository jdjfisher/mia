package io.github.mianalysis.mia.module.ObjectProcessing.Identification;

import io.github.mianalysis.mia.module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

public class ActiveContourObjectDetectionTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new ActiveContourObjectDetection(null).getDescription());
    }
}
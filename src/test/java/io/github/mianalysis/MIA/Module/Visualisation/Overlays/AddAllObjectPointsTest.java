package io.github.mianalysis.mia.module.Visualisation.overlays;

import io.github.mianalysis.mia.module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

public class AddAllObjectPointsTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new AddAllObjectPoints(null).getDescription());
    }
}
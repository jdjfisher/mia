package io.github.mianalysis.mia.module.Visualisation.ImageRendering;

import io.github.mianalysis.mia.module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

public class SetLookupTableTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new SetLookupTable(null).getDescription());
    }
}
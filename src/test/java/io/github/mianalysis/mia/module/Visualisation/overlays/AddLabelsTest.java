package io.github.mianalysis.mia.module.Visualisation.overlays;

import io.github.mianalysis.mia.module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Stephen Cross on 29/03/2019.
 */
public class AddLabelsTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new AddLabels(null).getDescription());
    }
}
package io.github.mianalysis.mia.module.imageprocessing.Stack;

import io.github.mianalysis.mia.module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

public class ReplaceImageTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new ReplaceImage(null).getDescription());
    }
}
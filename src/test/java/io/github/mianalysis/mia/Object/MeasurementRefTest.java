package io.github.mianalysis.mia.Object;

import org.junit.jupiter.api.Test;

import io.github.mianalysis.mia.Object.Refs.ImageMeasurementRef;

import static org.junit.jupiter.api.Assertions.*;

public class MeasurementRefTest {
    @Test
    public void testConstructor() {
        ImageMeasurementRef measurementReference = new ImageMeasurementRef("Test name");

        assertEquals("Test name",measurementReference.getName());
        assertEquals("",measurementReference.getImageName());
        assertTrue(measurementReference.isExportIndividual());
    }
}
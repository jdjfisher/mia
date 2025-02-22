package io.github.mianalysis.mia.module.objects.measure.spatial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.github.mianalysis.mia.expectedobjects.ExpectedObjects;
import io.github.mianalysis.mia.expectedobjects.Objects3D;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.module.ModuleTest;
import io.github.mianalysis.mia.module.objects.measure.spatial.MeasureObjectCentroid;
import io.github.mianalysis.mia.object.Obj;
import io.github.mianalysis.mia.object.Objs;
import io.github.mianalysis.mia.object.Workspace;
import io.github.mianalysis.mia.object.Workspaces;
import io.github.sjcross.sjcommon.object.volume.VolumeType;

/**
 * Created by Stephen Cross on 10/09/2017.
 */

public class MeasureObjectCentroidTest extends ModuleTest {
    private double tolerance = 1E-2;

    @BeforeAll
    public static void setVerbose() {
        Module.setVerbose(false);
    }

    @Override
    public void testGetHelp() {
        assertNotNull(new MeasureObjectCentroid(null).getDescription());

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void calculateCentroidMean(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Setting object parameters
        String inputObjectsName = "Test_objects";
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "um";

        // Creating objects and adding to workspace
        Objs testObjects = new Objects3D(volumeType).getObjects(inputObjectsName, ExpectedObjects.Mode.EIGHT_BIT,dppXY,dppZ,calibratedUnits,true);
        workspace.addObjects(testObjects);

        // Initialising MeasureObjectCentroid
        MeasureObjectCentroid measureObjectCentroid = new MeasureObjectCentroid(null);
        measureObjectCentroid.initialiseParameters();
        measureObjectCentroid.updateParameterValue(MeasureObjectCentroid.INPUT_OBJECTS,inputObjectsName);

        // Running MeasureObjectCentroid
        measureObjectCentroid.execute(workspace);

        // Running through each object, checking it has the expected number of children and the expected value
        for (Obj testObject:testObjects.values()) {
            // Testing measurements
            double expected = testObject.getMeasurement(Objects3D.Measures.EXP_X_MEAN.name()).getValue();
            double actual = testObject.getMeasurement(MeasureObjectCentroid.Measurements.MEAN_X_PX).getValue();
            assertEquals(expected,actual,tolerance);

            expected = testObject.getMeasurement(Objects3D.Measures.EXP_Y_MEAN.name()).getValue();
            actual = testObject.getMeasurement(MeasureObjectCentroid.Measurements.MEAN_Y_PX).getValue();
            assertEquals(expected,actual,tolerance);

            expected = testObject.getMeasurement(Objects3D.Measures.EXP_Z_MEAN.name()).getValue();
            actual = testObject.getMeasurement(MeasureObjectCentroid.Measurements.MEAN_Z_SLICE).getValue();
            assertEquals(expected,actual,tolerance);

        }
    }
}
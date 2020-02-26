package wbif.sjx.MIA.Module.ObjectMeasurements.Spatial;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleTest;
import wbif.sjx.MIA.Object.Obj;
import wbif.sjx.MIA.Object.ObjCollection;
import wbif.sjx.MIA.Object.SpatCal;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.common.Object.Volume.PointOutOfRangeException;
import wbif.sjx.common.Object.Volume.VolumeType;

import static org.junit.jupiter.api.Assertions.*;

public class MeasureObjectOverlapTest extends ModuleTest {

    private double tolerance = 1E-2;

    @BeforeAll
    public static void setVerbose() {
        Module.setVerbose(true);
    }

    @Override
    public void testGetHelp() {
        assertNotNull(new MeasureObjectOverlap(null).getDescription());
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testGetNOverlappingPointsNoOverlap(VolumeType volumeType) throws PointOutOfRangeException {
        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(20,22,32);
        object2_2.add(20,21,32);
        object2_2.add(20,22,33);
        object2_2.add(19,22,32);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(10,22,32);
        object2_3.add(10,21,32);
        object2_3.add(10,22,33);
        object2_3.add(9,22,32);
        objects2.add(object2_3);

        int actual = MeasureObjectOverlap.getNOverlappingPoints(object1_1,objects1,objects2,false);
        int expected = 0;

        assertEquals(expected,actual);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testGetNOverlappingPointsPartialSingleObjectOverlap(VolumeType volumeType) throws PointOutOfRangeException {
        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(10,22,32);
        object2_3.add(10,21,32);
        object2_3.add(10,22,33);
        object2_3.add(9,22,32);
        objects2.add(object2_3);

        int actual = MeasureObjectOverlap.getNOverlappingPoints(object1_1,objects1,objects2,false);
        int expected = 4;

        assertEquals(expected,actual);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testGetNOverlappingPointsPartialMultipleObjectOverlap(VolumeType volumeType) throws PointOutOfRangeException {
        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(9,13,33);
        object2_3.add(11,12,33);
        object2_3.add(11,13,34);
        object2_3.add(10,12,34);
        object2_3.add(10,12,35);
        objects2.add(object2_3);

        int actual = MeasureObjectOverlap.getNOverlappingPoints(object1_1,objects1,objects2,false);
        int expected = 7;

        assertEquals(expected,actual);

    }

    /**
     * In this test, two of the test objects share the same pixel.  This shouldn't lead to an increase in the overlap
     * volume of the main object.
     * @throws PointOutOfRangeException
     */
    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testGetNOverlappingPointsPartialMultipleObjectOverlapWithInternalClash(VolumeType volumeType) throws PointOutOfRangeException {
        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(9,13,33);
        object2_3.add(10,13,33);
        object2_3.add(11,12,33);
        object2_3.add(11,13,34);
        object2_3.add(10,12,34);
        object2_3.add(10,12,35);
        objects2.add(object2_3);

        int actual = MeasureObjectOverlap.getNOverlappingPoints(object1_1,objects1,objects2,false);
        int expected = 7;

        assertEquals(expected,actual);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testGetNOverlappingPointsTotalOverlap(VolumeType volumeType) throws PointOutOfRangeException {
// Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(10,12,32);
        object2_2.add(11,12,32);
        object2_2.add(10,13,32);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(9,13,33);
        object2_3.add(10,13,33);
        object2_3.add(11,12,33);
        object2_3.add(11,13,34);
        object2_3.add(10,12,34);
        object2_3.add(10,12,35);
        objects2.add(object2_3);

        int actual = MeasureObjectOverlap.getNOverlappingPoints(object1_1,objects1,objects2,false);
        int expected = 10;

        assertEquals(expected,actual);
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunNoOverlap(VolumeType volumeType) throws PointOutOfRangeException {
        // Creating a new workspace
        Workspace workspace = new Workspace(0,null,1);

        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);
        
        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(20,22,32);
        object2_2.add(20,21,32);
        object2_2.add(20,22,33);
        object2_2.add(19,22,32);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(10,22,32);
        object2_3.add(10,21,32);
        object2_3.add(10,22,33);
        object2_3.add(9,22,32);
        objects2.add(object2_3);

        workspace.addObjects(objects1);
        workspace.addObjects(objects2);

        // Initialising MeasureObjectOverlap
        MeasureObjectOverlap measureObjectOverlap = new MeasureObjectOverlap(null);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_1,objectsName1);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_2,objectsName2);

        // Running MeasureObjectOverlap
        measureObjectOverlap.execute(workspace);

        // Getting the measurement for each object and checking it is as expected
        String measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_1);
        double actual = object1_1.getMeasurement(measurementName).getValue();
        double expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_VOX_1);
        actual = object1_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunPartialSingleObjectOverlap(VolumeType volumeType) throws PointOutOfRangeException {
        // Creating a new workspace
        Workspace workspace = new Workspace(0,null,1);

        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(10,22,32);
        object2_3.add(10,21,32);
        object2_3.add(10,22,33);
        object2_3.add(9,22,32);
        objects2.add(object2_3);

        workspace.addObjects(objects1);
        workspace.addObjects(objects2);

        // Initialising MeasureObjectOverlap
        MeasureObjectOverlap measureObjectOverlap = new MeasureObjectOverlap(null);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_1,objectsName1);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_2,objectsName2);

        // Running MeasureObjectOverlap
        measureObjectOverlap.execute(workspace);

        // Getting the measurement for each object and checking it is as expected
        String measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_1);
        double actual = object1_1.getMeasurement(measurementName).getValue();
        double expected = 40;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_VOX_1);
        actual = object1_1.getMeasurement(measurementName).getValue();
        expected = 4;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 57.14;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 4;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunPartialMultipleObjectOverlap(VolumeType volumeType) throws PointOutOfRangeException {
        // Creating a new workspace
        Workspace workspace = new Workspace(0,null,1);

        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(9,13,33);
        object2_3.add(11,12,33);
        object2_3.add(11,13,34);
        object2_3.add(10,12,34);
        object2_3.add(10,12,35);
        objects2.add(object2_3);

        workspace.addObjects(objects1);
        workspace.addObjects(objects2);

        // Initialising MeasureObjectOverlap
        MeasureObjectOverlap measureObjectOverlap = new MeasureObjectOverlap(null);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_1,objectsName1);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_2,objectsName2);

        // Running MeasureObjectOverlap
        measureObjectOverlap.execute(workspace);

        // Getting the measurement for each object and checking it is as expected
        String measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_1);
        double actual = object1_1.getMeasurement(measurementName).getValue();
        double expected = 70;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_VOX_1);
        actual = object1_1.getMeasurement(measurementName).getValue();
        expected = 7;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 57.14;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 4;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 60;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 3;
        assertEquals(expected,actual,tolerance);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunPartialMultipleObjectOverlapWithInternalClash(VolumeType volumeType) throws PointOutOfRangeException {
        // Creating a new workspace
        Workspace workspace = new Workspace(0,null,1);

        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(9,13,33);
        object2_3.add(10,13,33);
        object2_3.add(11,12,33);
        object2_3.add(11,13,34);
        object2_3.add(10,12,34);
        object2_3.add(10,12,35);
        objects2.add(object2_3);

        workspace.addObjects(objects1);
        workspace.addObjects(objects2);

        // Initialising MeasureObjectOverlap
        MeasureObjectOverlap measureObjectOverlap = new MeasureObjectOverlap(null);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_1,objectsName1);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_2,objectsName2);

        // Running MeasureObjectOverlap
        measureObjectOverlap.execute(workspace);

        // Getting the measurement for each object and checking it is as expected
        String measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_1);
        double actual = object1_1.getMeasurement(measurementName).getValue();
        double expected = 70;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_VOX_1);
        actual = object1_1.getMeasurement(measurementName).getValue();
        expected = 7;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 57.14;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 4;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 66.67;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 4;
        assertEquals(expected,actual,tolerance);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunTotalOverlap(VolumeType volumeType) throws PointOutOfRangeException {
        // Creating a new workspace
        Workspace workspace = new Workspace(0,null,1);

        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.add(10,12,32);
        object2_2.add(11,12,32);
        object2_2.add(10,13,32);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.add(9,13,33);
        object2_3.add(10,13,33);
        object2_3.add(11,12,33);
        object2_3.add(11,13,34);
        object2_3.add(10,12,34);
        object2_3.add(10,12,35);
        objects2.add(object2_3);

        workspace.addObjects(objects1);
        workspace.addObjects(objects2);

        // Initialising MeasureObjectOverlap
        MeasureObjectOverlap measureObjectOverlap = new MeasureObjectOverlap(null);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_1,objectsName1);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_2,objectsName2);

        // Running MeasureObjectOverlap
        measureObjectOverlap.execute(workspace);

        // Getting the measurement for each object and checking it is as expected
        String measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_1);
        double actual = object1_1.getMeasurement(measurementName).getValue();
        double expected = 100;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_VOX_1);
        actual = object1_1.getMeasurement(measurementName).getValue();
        expected = 10;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 70;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 7;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 66.67;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 4;
        assertEquals(expected,actual,tolerance);

    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunPartialMultipleObjectOverlapMultipleTimepoints(VolumeType volumeType) throws PointOutOfRangeException {
        // Creating a new workspace
        Workspace workspace = new Workspace(0,null,1);

        // Setting object parameters
        String objectsName1 = "Test objects 1";
        String objectsName2 = "Test objects 2";

        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        SpatCal calibration = new SpatCal(dppXY,dppZ,calibratedUnits,30,50,50,1);

        // Creating a single test object
        ObjCollection objects1 = new ObjCollection(objectsName1,calibration);
        Obj object1_1 = new Obj(volumeType,objectsName1,1,calibration);
        object1_1.setT(2);
        object1_1.add(10,12,32);
        object1_1.add(11,12,32);
        object1_1.add(10,13,32);
        object1_1.add(10,14,32);
        object1_1.add(11,13,32);
        object1_1.add(10,12,33);
        object1_1.add(10,13,33);
        object1_1.add(11,12,33);
        object1_1.add(11,13,34);
        object1_1.add(10,12,34);
        objects1.add(object1_1);

        // Creating a collection of multiple objects to test against
        ObjCollection objects2 = new ObjCollection(objectsName2,calibration);
        Obj object2_1 = new Obj(volumeType,objectsName2,1,calibration);
        object2_1.setT(2);
        object2_1.add(20,12,32);
        object2_1.add(20,11,32);
        object2_1.add(20,12,33);
        object2_1.add(19,12,32);
        objects2.add(object2_1);

        Obj object2_2 = new Obj(volumeType,objectsName2,2,calibration);
        object2_2.setT(3);
        object2_2.add(9,13,32);
        object2_2.add(9,14,32);
        object2_2.add(10,14,32);
        object2_2.add(11,13,32);
        object2_2.add(10,12,33);
        object2_2.add(10,13,33);
        object2_2.add(9,13,33);
        objects2.add(object2_2);

        Obj object2_3 = new Obj(volumeType,objectsName2,3,calibration);
        object2_3.setT(2);
        object2_3.add(9,13,33);
        object2_3.add(11,12,33);
        object2_3.add(11,13,34);
        object2_3.add(10,12,34);
        object2_3.add(10,12,35);
        objects2.add(object2_3);

        workspace.addObjects(objects1);
        workspace.addObjects(objects2);

        // Initialising MeasureObjectOverlap
        MeasureObjectOverlap measureObjectOverlap = new MeasureObjectOverlap(null);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_1,objectsName1);
        measureObjectOverlap.updateParameterValue(MeasureObjectOverlap.OBJECT_SET_2,objectsName2);

        // Running MeasureObjectOverlap
        measureObjectOverlap.execute(workspace);

        // Getting the measurement for each object and checking it is as expected
        String measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_1);
        double actual = object1_1.getMeasurement(measurementName).getValue();
        double expected = 30;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName2,MeasureObjectOverlap.Measurements.OVERLAP_VOX_1);
        actual = object1_1.getMeasurement(measurementName).getValue();
        expected = 3;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_1.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_2.getMeasurement(measurementName).getValue();
        expected = 0;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_PERCENT_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 60;
        assertEquals(expected,actual,tolerance);

        measurementName = MeasureObjectOverlap.getFullName(objectsName1,MeasureObjectOverlap.Measurements.OVERLAP_VOX_2);
        actual = object2_3.getMeasurement(measurementName).getValue();
        expected = 3;
        assertEquals(expected,actual,tolerance);

    }
}
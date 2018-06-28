package wbif.sjx.ModularImageAnalysis.Module.ObjectMeasurements.Intensity;

import ij.IJ;
import ij.ImagePlus;
import org.junit.Test;
import wbif.sjx.ModularImageAnalysis.ExpectedObjects.ExpectedObjects;
import wbif.sjx.ModularImageAnalysis.ExpectedObjects.Objects2D;
import wbif.sjx.ModularImageAnalysis.Object.Image;
import wbif.sjx.ModularImageAnalysis.Object.Obj;
import wbif.sjx.ModularImageAnalysis.Object.ObjCollection;
import wbif.sjx.ModularImageAnalysis.Object.Workspace;

import java.net.URLDecoder;

import static org.junit.Assert.*;

public class MeasureObjectColocalisationTest {
    private double tolerance  = 1E-2;

    @Test
    public void testGetFullName() {
        String inputImage1Name = "image1";
        String inputImage2Name = "Im2";
        String measurement = MeasureObjectColocalisation.Measurements.PCC;

        String expected = "COLOCALISATION // image1_Im2_PCC";
        String actual = MeasureObjectColocalisation.getFullName(inputImage1Name,inputImage2Name,measurement);

        assertEquals(expected,actual);

    }

    @Test
    public void testMeasurePCC() throws Exception {
        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects2D().getObjects("Expected",ExpectedObjects.Mode.EIGHT_BIT,dppXY,dppZ,calibratedUnits,true);

        // Loading images
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/MeasureColocalisation/ColocalisationChannel1_2D_8bit.tif").getPath(),"UTF-8");
        ImagePlus ipl1 = IJ.openImage(pathToImage);
        Image image1 = new Image("Im1",ipl1);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/MeasureColocalisation/ColocalisationChannel2_2D_8bit.tif").getPath(),"UTF-8");
        ImagePlus ipl2 = IJ.openImage(pathToImage);
        Image image2 = new Image("Im2",ipl2);

        // Running through each object, checking it has the expected number of measurements and the expected value
        String measurementName = MeasureObjectColocalisation.getFullName("Im1","Im2",MeasureObjectColocalisation.Measurements.PCC);
        for (Obj testObject:expectedObjects.values()) {
            MeasureObjectColocalisation.measurePCC(testObject,image1,image2);
            double expected = testObject.getMeasurement(Objects2D.Measures.PCC.name()).getValue();
            double actual = testObject.getMeasurement(measurementName).getValue();
            assertEquals("Measurement value", expected, actual, tolerance);

        }
    }

    @Test
    public void testGetTitle() {
        assertNotNull(new MeasureObjectColocalisation().getTitle());
    }
}
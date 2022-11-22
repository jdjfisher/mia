package io.github.mianalysis.mia.module.images.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URLDecoder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ij.IJ;
import ij.ImagePlus;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.module.ModuleTest;
import io.github.mianalysis.mia.module.Modules;
import io.github.mianalysis.mia.object.Measurement;
import io.github.mianalysis.mia.object.Workspace;
import io.github.mianalysis.mia.object.Workspaces;
import io.github.mianalysis.mia.object.image.Image;
import io.github.mianalysis.mia.object.image.ImageFactory;


public class ImageMathTest extends ModuleTest {
    @BeforeAll
    public static void setVerbose() {
        Module.setVerbose(false);
    }

    @Override
    public void testGetHelp() {
        assertNotNull(new ImageMath(null).getDescription());
    }

    @Test
    public void testRunAddPositive2D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient2D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient2D_Add50_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,50d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddPositive3D8bit() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Add50_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,50d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddPositive3D16bit() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_16bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Add50_16bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,50d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddPositive3D32bit() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_32bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Add50_32bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,50d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddPositive4D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient4D_ZT_8bit_C1.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient5D_Add50_8bit_C1.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,50d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddPositive5D() throws Exception {
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient5D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient5D_Add50_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,50d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddPositiveToInput5D() throws Exception {
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient5D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient5D_Add50_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,true);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,50d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(1,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_image");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddMeasurement5D() throws Exception {
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient5D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        // Adding a measurement to the input image
        image.addMeasurement(new Measurement("Test meas",12.4));

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient5D_AddMeas_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE, ImageMath.ValueSources.MEASUREMENT);
        imageMath.updateParameterValue(ImageMath.IMAGE_FOR_MEASUREMENT,"Test_image");
        imageMath.updateParameterValue(ImageMath.MEASUREMENT,"Test meas");

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunAddNegative3D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Add-5_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.ADD);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,-5d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunSubtractPositive3D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Subtract12_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.SUBTRACT);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,12d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunSubtractNegative3D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Subtract-12_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.SUBTRACT);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,-12d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunMultiplyPositive3D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Multiply2p3_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.MULTIPLY);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,2.3d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunMultiplyNegative3D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Multiply-2p3_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.MULTIPLY);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,-2.3d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunMultiplyNegative3D32bit() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_32bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Multiply-2p3_32bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.MULTIPLY);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,-2.3d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRunDividePositive3D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Divide0p4_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.DIVIDE);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,0.4d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRuDivideNegative3D() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_8bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Divide-0p6_8bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.DIVIDE);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,-0.6d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }

    @Test
    public void testRuDivideNegative3D32bit() throws Exception {
        // Creating a new workspace
        Workspaces workspaces = new Workspaces();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/noisygradient/NoisyGradient3D_32bit.zip").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = ImageFactory.createImage("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/imagemath/NoisyGradient3D_Divide-0p6_32bit.zip").getPath(),"UTF-8");
        Image expectedImage = ImageFactory.createImage("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        ImageMath imageMath = new ImageMath(new Modules());
        imageMath.initialiseParameters();
        imageMath.updateParameterValue(ImageMath.INPUT_IMAGE,"Test_image");
        imageMath.updateParameterValue(ImageMath.OUTPUT_IMAGE,"Test_output");
        imageMath.updateParameterValue(ImageMath.APPLY_TO_INPUT,false);
        imageMath.updateParameterValue(ImageMath.CALCULATION_MODE,ImageMath.CalculationModes.DIVIDE);
        imageMath.updateParameterValue(ImageMath.VALUE_SOURCE,ImageMath.ValueSources.FIXED);
        imageMath.updateParameterValue(ImageMath.MATH_VALUE,-0.6d);

        // Running Module
        imageMath.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }
}
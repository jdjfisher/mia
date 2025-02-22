package io.github.mianalysis.mia.module.images.process.binary;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.Resizer;
import ij.plugin.SubHyperstackMaker;
import inra.ijpb.binary.distmap.ChamferDistanceTransform3DFloat;
import inra.ijpb.binary.distmap.ChamferMask3D;
import io.github.mianalysis.mia.module.Categories;
import io.github.mianalysis.mia.module.Category;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.module.Modules;
import io.github.mianalysis.mia.module.images.process.ImageMath;
import io.github.mianalysis.mia.module.images.process.ImageTypeConverter;
import io.github.mianalysis.mia.module.images.process.InvertIntensity;
import io.github.mianalysis.mia.module.images.transform.InterpolateZAxis;
import io.github.mianalysis.mia.object.Workspace;
import io.github.mianalysis.mia.object.image.Image;
import io.github.mianalysis.mia.object.image.ImageFactory;
import io.github.mianalysis.mia.object.parameters.BooleanP;
import io.github.mianalysis.mia.object.parameters.ChoiceP;
import io.github.mianalysis.mia.object.parameters.InputImageP;
import io.github.mianalysis.mia.object.parameters.OutputImageP;
import io.github.mianalysis.mia.object.parameters.Parameters;
import io.github.mianalysis.mia.object.parameters.SeparatorP;
import io.github.mianalysis.mia.object.parameters.choiceinterfaces.BinaryLogicInterface;
import io.github.mianalysis.mia.object.parameters.choiceinterfaces.SpatialUnitsInterface;
import io.github.mianalysis.mia.object.refs.collections.ImageMeasurementRefs;
import io.github.mianalysis.mia.object.refs.collections.MetadataRefs;
import io.github.mianalysis.mia.object.refs.collections.ObjMeasurementRefs;
import io.github.mianalysis.mia.object.refs.collections.ParentChildRefs;
import io.github.mianalysis.mia.object.refs.collections.PartnerRefs;
import io.github.mianalysis.mia.object.system.Status;

@Plugin(type = Module.class, priority = Priority.LOW, visible = true)
public class DistanceMap extends Module {
    public static final String INPUT_SEPARATOR = "Image input/output";
    public static final String INPUT_IMAGE = "Input image";
    public static final String OUTPUT_IMAGE = "Output image";

    public static final String DISTANCE_MAP_SEPARATOR = "Distance map controls";
    public static final String WEIGHT_MODE = "Weight modes";
    public static final String MATCH_Z_TO_X = "Match Z to XY";
    public static final String SPATIAL_UNITS_MODE = "Spatial units mode";
    public static final String BINARY_LOGIC = "Binary logic";

    public interface WeightModes {
        String BORGEFORS = "Borgefors (3,4,5)";
        String CHESSBOARD = "Chessboard (1,1,1)";
        String CITY_BLOCK = "City-Block (1,2,3)";
        // String QUASI_EUCLIDEAN = "Quasi-Euclidean (1,1.41,1.73)";
        String WEIGHTS_3_4_5_7 = "Svensson (3,4,5,7)";

        // String[] ALL = new String[] { BORGEFORS, CHESSBOARD, CITY_BLOCK,
        // QUASI_EUCLIDEAN,
        // WEIGHTS_3_4_5_7 };
        String[] ALL = new String[] { BORGEFORS, CHESSBOARD, CITY_BLOCK, WEIGHTS_3_4_5_7 };

    }

    public interface SpatialUnitsModes extends SpatialUnitsInterface {
    }

    public interface BinaryLogic extends BinaryLogicInterface {
    }

    public DistanceMap(Modules modules) {
        super("Calculate distance map", modules);
    }

    public static ImagePlus process(ImagePlus inputIpl, String outputImageName, boolean blackBackground,
            String weightMode, boolean matchZToXY, boolean verbose) {
        return process(ImageFactory.createImage(inputIpl.getTitle(), inputIpl), outputImageName, blackBackground,
                weightMode,
                matchZToXY, verbose).getImagePlus();
    }

    public static Image process(Image inputImage, String outputImageName, boolean blackBackground, String weightMode,
            boolean matchZToXY, boolean verbose) {
        String name = new DistanceMap(null).getName();

        ImagePlus inputIpl = inputImage.getImagePlus();

        // Calculating the distance map using MorphoLibJ
        ChamferMask3D weights = getFloatWeights(weightMode);

        // Calculating the distance map, one frame at a time
        int count = 0;
        int nChannels = inputIpl.getNChannels();
        int nFrames = inputIpl.getNFrames();
        int nSlices = inputIpl.getNSlices();

        // Creating a duplicate of the input image
        ImagePlus outputIpl = IJ.createHyperStack(inputIpl.getTitle(), inputIpl.getWidth(), inputIpl.getHeight(),
                inputIpl.getNChannels(), nSlices, nFrames, 32);
        ImageStack outputIst = outputIpl.getStack();

        ChamferDistanceTransform3DFloat transform = new ChamferDistanceTransform3DFloat(weights, true);

        for (int c = 0; c < nChannels; c++) {
            for (int t = 0; t < nFrames; t++) {
                // Getting the mask image at this timepoint
                ImagePlus currentIpl = SubHyperstackMaker
                        .makeSubhyperstack(inputIpl, String.valueOf(c + 1), "1-" + nSlices, String.valueOf(t + 1))
                        .duplicate();

                currentIpl.setCalibration(inputIpl.getCalibration());

                if (!blackBackground)
                    InvertIntensity.process(currentIpl);

                // If necessary, interpolating the image in Z to match the XY spacing
                if (matchZToXY && nSlices > 1)
                    currentIpl = InterpolateZAxis.matchZToXY(currentIpl, InterpolateZAxis.InterpolationModes.NONE);

                ImageStack ist = transform.distanceMap(currentIpl.getStack().duplicate());
                currentIpl.setStack(ist);

                // If the input image as interpolated, it now needs to be returned to the
                // original scaling
                if (matchZToXY && nSlices > 1) {
                    Resizer resizer = new Resizer();
                    resizer.setAverageWhenDownsizing(true);
                    currentIpl = resizer.zScale(currentIpl, nSlices, Resizer.IN_PLACE);
                }

                // Putting the image back into the distanceMapImage
                ImageStack currentIst = currentIpl.getStack();
                for (int z = 0; z < currentIpl.getNSlices(); z++) {
                    int currentIdx = currentIpl.getStackIndex(1, z + 1, 1);
                    int outputIdx = outputIpl.getStackIndex(c + 1, z + 1, t + 1);
                    outputIst.setProcessor(currentIst.getProcessor(currentIdx), outputIdx);
                }

                if (verbose)
                    writeProgressStatus(++count, nFrames, "timepoints", name);

            }
        }

        outputIpl.setStack(outputIst);
        outputIpl.setPosition(1, 1, 1);
        outputIpl.updateAndDraw();

        Calibration inputCalibration = inputIpl.getCalibration();
        Calibration outputCalibration = new Calibration();
        outputCalibration.fps = inputCalibration.fps;
        outputCalibration.frameInterval = inputCalibration.frameInterval;
        outputCalibration.pixelDepth = inputCalibration.pixelDepth;
        outputCalibration.pixelWidth = inputCalibration.pixelWidth;
        outputCalibration.pixelHeight = inputCalibration.pixelHeight;
        outputCalibration.setUnit(inputCalibration.getUnit());

        outputIpl.setCalibration(outputCalibration);

        return ImageFactory.createImage(outputImageName, outputIpl);

    }

    static ChamferMask3D getFloatWeights(String weightMode) {
        switch (weightMode) {
            case WeightModes.BORGEFORS:
                return ChamferMask3D.BORGEFORS;
            case WeightModes.CHESSBOARD:
                return ChamferMask3D.CHESSBOARD;
            case WeightModes.CITY_BLOCK:
                return ChamferMask3D.CITY_BLOCK;
            // case WeightModes.QUASI_EUCLIDEAN:
            // return ChamferMask3D.QUASI_EUCLIDEAN;
            case WeightModes.WEIGHTS_3_4_5_7:
            default:
                return ChamferMask3D.SVENSSON_3_4_5_7;
        }
    }

    public static void applyCalibratedUnits(Image inputImage, double dppXY) {
        ImageTypeConverter.process(inputImage, 32, ImageTypeConverter.ScalingModes.CLIP);
        ImageMath.process(inputImage, ImageMath.CalculationModes.MULTIPLY, dppXY);

    }

    @Override
    public Category getCategory() {
        return Categories.IMAGES_PROCESS_BINARY;
    }

    @Override
    public String getDescription() {
        return "Creates a 32-bit greyscale image from an input binary image, where the value of each foreground pixel in the input image is equal to its Euclidean distance to the nearest background pixel.  This image will be 8-bit with binary logic determined by the \""
                + BINARY_LOGIC
                + "\" parameter.  The output image will have pixel values of 0 coincident with background pixels in the input image and values greater than zero coincident with foreground pixels.  Uses the plugin \"<a href=\"https://github.com/ijpb/MorphoLibJ\">MorphoLibJ</a>\".";

    }

    @Override
    public Status process(Workspace workspace) {
        // Getting input image
        String inputImageName = parameters.getValue(INPUT_IMAGE, workspace);
        Image inputImage = workspace.getImages().get(inputImageName);

        // Getting parameters
        String outputImageName = parameters.getValue(OUTPUT_IMAGE, workspace);
        String weightMode = parameters.getValue(WEIGHT_MODE, workspace);
        boolean matchZToXY = parameters.getValue(MATCH_Z_TO_X, workspace);
        String spatialUnits = parameters.getValue(SPATIAL_UNITS_MODE, workspace);
        String binaryLogic = parameters.getValue(BINARY_LOGIC, workspace);
        boolean blackBackground = binaryLogic.equals(BinaryLogic.BLACK_BACKGROUND);

        // Running distance map
        Image distanceMap = process(inputImage, outputImageName, blackBackground, weightMode, matchZToXY, true);

        // Applying spatial calibration
        if (spatialUnits.equals(SpatialUnitsModes.CALIBRATED)) {
            double dppXY = inputImage.getImagePlus().getCalibration().pixelWidth;
            applyCalibratedUnits(distanceMap, dppXY);
        }

        // If the image is being saved as a new image, adding it to the workspace
        writeStatus("Adding image (" + outputImageName + ") to workspace");
        workspace.addImage(distanceMap);
        if (showOutput)
            distanceMap.show();

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new SeparatorP(INPUT_SEPARATOR, this));
        parameters.add(new InputImageP(INPUT_IMAGE, this));
        parameters.add(new OutputImageP(OUTPUT_IMAGE, this));

        parameters.add(new SeparatorP(DISTANCE_MAP_SEPARATOR, this));
        parameters.add(new ChoiceP(WEIGHT_MODE, this, WeightModes.WEIGHTS_3_4_5_7, WeightModes.ALL));
        parameters.add(new BooleanP(MATCH_Z_TO_X, this, true));
        parameters.add(new ChoiceP(SPATIAL_UNITS_MODE, this, SpatialUnitsModes.PIXELS, SpatialUnitsModes.ALL));
        parameters.add(new ChoiceP(BINARY_LOGIC, this, BinaryLogic.BLACK_BACKGROUND, BinaryLogic.ALL));

        addParameterDescriptions();

    }

    @Override
    public Parameters updateAndGetParameters() {
        return parameters;

    }

    @Override
    public ImageMeasurementRefs updateAndGetImageMeasurementRefs() {
        return null;
    }

    @Override
    public ObjMeasurementRefs updateAndGetObjectMeasurementRefs() {
        return null;
    }

    @Override
    public MetadataRefs updateAndGetMetadataReferences() {
        return null;
    }

    @Override
    public ParentChildRefs updateAndGetParentChildRefs() {
        return null;
    }

    @Override
    public PartnerRefs updateAndGetPartnerRefs() {
        return null;
    }

    @Override
    public boolean verify() {
        return true;
    }

    void addParameterDescriptions() {
        parameters.get(INPUT_IMAGE).setDescription(
                "Image from workspace to calculate distance map for.  This image will be 8-bit with binary logic determined by the \""
                        + BINARY_LOGIC + "\" parameter.");

        parameters.get(OUTPUT_IMAGE).setDescription(
                "The output distance map will be saved to the workspace with this name.  This image will be 32-bit format.");

        parameters.get(WEIGHT_MODE).setDescription(
                "The pre-defined set of weights that are used to compute the 3D distance transform using chamfer approximations of the euclidean metric (descriptions taken from <a href=\"https://ijpb.github.io/MorphoLibJ/javadoc/\">https://ijpb.github.io/MorphoLibJ/javadoc/</a>):<br><ul>"
                        + "<li>\"" + WeightModes.BORGEFORS
                        + "\" Use weight values of 3 for orthogonal neighbors, 4 for diagonal neighbors and 5 for cube-diagonals (best approximation for 3-by-3-by-3 masks).</li>"

                        + "<li>\"" + WeightModes.CHESSBOARD + "\" Use weight values of 1 for all neighbours.</li>"

                        + "<li>\"" + WeightModes.CITY_BLOCK
                        + "\" Use weight values of 1 for orthogonal neighbors, 2 for diagonal neighbors and 3 for cube-diagonals.</li>"

                        + "<li>\"" + WeightModes.WEIGHTS_3_4_5_7
                        + "\" Use weight values of 3 for orthogonal neighbors, 4 for diagonal neighbors, 5 for cube-diagonals and 7 for (2,1,1) shifts. Good approximation using only four weights, and keeping low value of orthogonal weight.</li></ul>");

        parameters.get(MATCH_Z_TO_X).setDescription(
                "When selected, an image is interpolated in Z (so that all pixels are isotropic) prior to calculation of the distance map.  This prevents warping of the distance map along the Z-axis if XY and Z sampling aren't equal.");

        parameters.get(SPATIAL_UNITS_MODE).setDescription(SpatialUnitsInterface.getDescription());

        parameters.get(BINARY_LOGIC).setDescription(BinaryLogicInterface.getDescription());

    }
}

package io.github.mianalysis.mia.module.objectprocessing.identification;

import java.util.ArrayList;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;
import io.github.mianalysis.mia.module.Categories;
import io.github.mianalysis.mia.module.Category;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.module.Modules;
import io.github.mianalysis.mia.object.Image;
import io.github.mianalysis.mia.object.Measurement;
import io.github.mianalysis.mia.object.Obj;
import io.github.mianalysis.mia.object.Objs;
import io.github.mianalysis.mia.object.Status;
import io.github.mianalysis.mia.object.Workspace;
import io.github.mianalysis.mia.object.parameters.BooleanP;
import io.github.mianalysis.mia.object.parameters.Parameters;
import io.github.mianalysis.mia.object.parameters.SeparatorP;
import io.github.mianalysis.mia.object.parameters.text.DoubleP;
import io.github.mianalysis.mia.object.parameters.text.IntegerP;
import io.github.mianalysis.mia.object.parameters.text.StringP;
import io.github.mianalysis.mia.object.units.TemporalUnit;
import io.github.sjcross.common.exceptions.IntegerOverflowException;
import io.github.sjcross.common.mathfunc.Indexer;
import io.github.sjcross.common.object.volume.PointOutOfRangeException;
import io.github.sjcross.common.object.volume.SpatCal;
import io.github.sjcross.common.object.volume.VolumeType;
import io.github.sjcross.common.object.voxels.MidpointCircle;
import io.github.sjcross.common.process.IntensityMinMax;
import io.github.sjcross.common.process.houghtransform.transforms.CircleTransform;

/**
 * Created by sc13967 on 15/01/2018.
 */
@Plugin(type = Module.class, priority = Priority.LOW, visible = true)
public class CircleHoughDetection extends AbstractHoughDetection {
    public static final String DETECTION_SEPARATOR = "Hough-based circle detection";
    public static final String RADIUS_RANGE = "Radius range (px)";
    public static final String DOWNSAMPLE_FACTOR = "Downsample factor";
    public static final String DETECTION_THRESHOLD = "Detection threshold";
    public static final String EXCLUSION_RADIUS = "Exclusion radius (px)";
    public static final String ENABLE_MULTITHREADING = "Enable multithreading";

    public static final String POST_PROCESSING_SEPARATOR = "Object post processing";
    public static final String RADIUS_RESIZE = "Output radius resize (px)";


    public CircleHoughDetection(Modules modules) {
        super("Circle detection", modules);
    }

    @Override
    public Category getCategory() {
        return Categories.OBJECT_PROCESSING_IDENTIFICATION;
    }

    @Override
    public String getDescription() {
        return "Detects circles within grayscale images using the Hough transform.  Input images can be of binary or grayscale format, but the circle features must be brighter than their surrounding background and have dark centres (i.e. be rings).  For solid circles, a gradient filter or equivalent should be applied to the image first.  Detected circles are output to the workspace as solid objects.  Circles are detected within a user-defined radius range and must exceed a user-defined threshold score (based on the intensity of the circle feartures in the input image and the feature circularity).";

    }

    @Override
    public Status process(Workspace workspace) {
        // Getting input image
        String inputImageName = parameters.getValue(INPUT_IMAGE);
        Image inputImage = workspace.getImage(inputImageName);
        ImagePlus ipl = inputImage.getImagePlus();

        // Getting parameters
        String outputObjectsName = parameters.getValue(OUTPUT_OBJECTS);
        boolean outputTransformImage = parameters.getValue(OUTPUT_TRANSFORM_IMAGE);
        String outputImageName = parameters.getValue(OUTPUT_IMAGE);

        // Getting parameters
        String radiusRange = parameters.getValue(RADIUS_RANGE);
        int samplingRate = parameters.getValue(DOWNSAMPLE_FACTOR);
        boolean multithread = parameters.getValue(ENABLE_MULTITHREADING);
        double detectionThreshold = parameters.getValue(DETECTION_THRESHOLD);
        int exclusionRadius = parameters.getValue(EXCLUSION_RADIUS);
        int radiusResize = parameters.getValue(RADIUS_RESIZE);
        boolean showTransformImage = parameters.getValue(SHOW_TRANSFORM_IMAGE);
        boolean showDetectionImage = parameters.getValue(SHOW_DETECTION_IMAGE);
        boolean showHoughScore = parameters.getValue(SHOW_HOUGH_SCORE);
        int labelSize = parameters.getValue(LABEL_SIZE);

        // Storing the image calibration
        SpatCal cal = SpatCal.getFromImage(ipl);
        int nFrames = ipl.getNFrames();
        double frameInterval = ipl.getCalibration().frameInterval;
        Objs outputObjects = new Objs(outputObjectsName, cal, nFrames, frameInterval, TemporalUnit.getOMEUnit());

        int nThreads = multithread ? Prefs.getThreads() : 1;

        radiusRange = resampleRange(radiusRange, samplingRate);

        // Iterating over all images in the ImagePlus
        int count = 1;
        int total = ipl.getNChannels() * ipl.getNSlices() * ipl.getNFrames();

        for (int c = 0; c < ipl.getNChannels(); c++) {
            for (int z = 0; z < ipl.getNSlices(); z++) {
                for (int t = 0; t < ipl.getNFrames(); t++) {
                    ipl.setPosition(c + 1, z + 1, t + 1);

                    // Applying scaling
                    ImageProcessor ipr = ipl.getProcessor();
                    if (samplingRate != 1)
                        ipr = ipr.resize(ipr.getWidth() / samplingRate);
                    
                    // Initialising the Hough transform
                    String[] paramRanges = new String[] { "0-" + (ipr.getWidth() - 1), "0-" + (ipr.getHeight() - 1),radiusRange};
                    CircleTransform transform = new CircleTransform(ipr, paramRanges);
                    transform.setnThreads(nThreads);

                    // Running the transforms
                    transform.run();

                    // Normalising scores based on the number of points in that circle
                    transform.normaliseScores();

                    // Getting the accumulator as an image
                    if (outputTransformImage || (showOutput && showTransformImage)) {
                        ImagePlus showIpl = new Duplicator().run(transform.getAccumulatorAsImage());

                        if (outputTransformImage) {
                            Image outputImage = new Image(outputImageName, showIpl);
                            workspace.addImage(outputImage);
                        }
                        if (showOutput && showTransformImage) {
                            IntensityMinMax.run(showIpl, true);
                            showIpl.setTitle("Accumulator");
                            showIpl.show();
                        }
                    }

                    // Getting circle objects and adding to workspace
                    ArrayList<double[]> circles = transform.getObjects(detectionThreshold, exclusionRadius);
                    Indexer indexer = new Indexer(ipl.getWidth(), ipl.getHeight());
                    for (double[] circle : circles) {
                        // Initialising the object
                        Obj outputObject = outputObjects.createAndAddNewObject(VolumeType.QUADTREE);

                        // Getting circle parameters
                        int x = (int) Math.round(circle[0])*samplingRate;
                        int y = (int) Math.round(circle[1])*samplingRate;
                        int r = (int) Math.round(circle[2])*samplingRate + radiusResize;
                        double score = circle[3];

                        // Getting coordinates corresponding to circle
                        MidpointCircle midpointCircle = new MidpointCircle(r);
                        int[] xx = midpointCircle.getXCircleFill();
                        int[] yy = midpointCircle.getYCircleFill();

                        for (int i = 0; i < xx.length; i++) {
                            int idx = indexer.getIndex(new int[] { xx[i] + x, yy[i] + y });
                            if (idx == -1)
                                continue;

                            try {
                                try {
                                    outputObject.add(xx[i] + x, yy[i] + y, z);
                                } catch (PointOutOfRangeException e) {
                                }
                            } catch (IntegerOverflowException e) {
                                return Status.FAIL;
                            }
                        }

                        // Adding measurements
                        outputObject.setT(t);
                        outputObject.addMeasurement(new Measurement(Measurements.SCORE, score));

                    }

                    writeProgressStatus(count++, total, "images");

                }
            }
        }

        ipl.setPosition(1, 1, 1);
        workspace.addObjects(outputObjects);

        if (showOutput && showDetectionImage)
            showDetectionImage(inputImage, outputObjects, showHoughScore, labelSize);

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        super.initialiseParameters();

        parameters.add(new SeparatorP(DETECTION_SEPARATOR, this));
        parameters.add(new StringP(RADIUS_RANGE, this, "10-20-1"));
        parameters.add(new IntegerP(DOWNSAMPLE_FACTOR,this,1));
        parameters.add(new DoubleP(DETECTION_THRESHOLD, this, 1.0));
        parameters.add(new IntegerP(EXCLUSION_RADIUS, this, 10));
        parameters.add(new BooleanP(ENABLE_MULTITHREADING, this, true));

        parameters.add(new SeparatorP(POST_PROCESSING_SEPARATOR, this));
        parameters.add(new IntegerP(RADIUS_RESIZE, this, 0));

        addParameterDescriptions();

    }

    @Override
    public Parameters updateAndGetParameters() {
        Parameters returnedParameters = new Parameters();

        returnedParameters.addAll(updateAndGetInputParameters());
        
        returnedParameters.add(parameters.getParameter(DETECTION_SEPARATOR));
        returnedParameters.add(parameters.getParameter(RADIUS_RANGE));
        returnedParameters.add(parameters.getParameter(DOWNSAMPLE_FACTOR));
        returnedParameters.add(parameters.getParameter(DETECTION_THRESHOLD));
        returnedParameters.add(parameters.getParameter(EXCLUSION_RADIUS));
        returnedParameters.add(parameters.getParameter(ENABLE_MULTITHREADING));

        returnedParameters.add(parameters.getParameter(POST_PROCESSING_SEPARATOR));
        returnedParameters.add(parameters.getParameter(RADIUS_RESIZE));

        returnedParameters.addAll(updateAndGetVisualisationParameters());

        return returnedParameters;

    }

    void addParameterDescriptions() {
        super.addParameterDescriptions();

        parameters.get(OUTPUT_IMAGE).setDescription("If \"" + OUTPUT_TRANSFORM_IMAGE
                + "\" is selected, this will be the name assigned to the transform image added to the workspace.  The transform image has XY dimensions equal to the input image and an equal number of Z-slices to the number of radii tested.  Circluar features in the input image appear as bright points, where the XYZ location of the point corresponds to the XYR (i.e. X, Y, radius) parameters for the circle.");

        parameters.get(RADIUS_RANGE)
                .setDescription("Range of radius values to be tested.  Radii can be specified as a comma-separated list, using a range (e.g. \"4-7\" will extract relative indices 4,5,6 and 7) or as a range extracting every nth slice (e.g. \"4-10-2\" will extract slices 4,6,8 and 10).  Radii are specified in pixel units.");

        parameters.get(DETECTION_THRESHOLD).setDescription(
                "The minimum score a detected circle must have to be stored.  Scores are the sum of all pixel intensities lying on the perimeter of the circle.  As such, higher scores correspond to brighter circles, circles with high circularity (where all points lie on the perimeter of the detected circle) and circles with continuous intensity along their perimeter (no gaps).");

        parameters.get(EXCLUSION_RADIUS).setDescription(
                "The minimum distance between adjacent circles.  For multiple candidate points within this range, the circle with the highest score will be retained.  Specified in pixel units.");

        parameters.get(ENABLE_MULTITHREADING).setDescription(
                "Process multiple radii simultaneously.  This can provide a speed improvement when working on a computer with a multi-core CPU.");

        parameters.get(RADIUS_RESIZE).setDescription(
                "Radius of output objects will be adjusted by this value.  For example, a detected circle of radius 5 with a \"radius resize\" of 2 will have an output radius of 7.  Similarly, setting \"radius resize\" to -3 would produce a circle of radius 2.");

    }
}

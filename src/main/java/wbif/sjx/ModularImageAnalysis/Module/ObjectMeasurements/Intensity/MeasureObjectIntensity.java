// TODO: Could add an optional parameter to select the channel of the input image to use for measurement

package wbif.sjx.ModularImageAnalysis.Module.ObjectMeasurements.Intensity;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.SubHyperstackMaker;
import ij.process.ImageProcessor;
import wbif.sjx.ModularImageAnalysis.Module.ImageMeasurements.MeasureIntensityDistribution;
import wbif.sjx.ModularImageAnalysis.Module.ImageProcessing.Pixel.BinaryOperations;
import wbif.sjx.ModularImageAnalysis.Module.ImageProcessing.Pixel.ImageCalculator;
import wbif.sjx.ModularImageAnalysis.Module.ImageProcessing.Pixel.ImageMath;
import wbif.sjx.ModularImageAnalysis.Module.ImageProcessing.Pixel.InvertIntensity;
import wbif.sjx.ModularImageAnalysis.Module.Module;
import wbif.sjx.ModularImageAnalysis.Module.PackageNames;
import wbif.sjx.ModularImageAnalysis.Object.*;
import wbif.sjx.common.Analysis.IntensityCalculator;
import wbif.sjx.common.MathFunc.CumStat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by sc13967 on 05/05/2017.
 */
public class MeasureObjectIntensity extends Module {
    public static final String INPUT_OBJECTS = "Input objects";
    public static final String INPUT_IMAGE = "Input image";
    public static final String MEASURE_MEAN = "Measure mean";
    public static final String MEASURE_STDEV = "Measure standard deviation";
    public static final String MEASURE_MIN = "Measure minimum";
    public static final String MEASURE_MAX = "Measure maximum";
    public static final String MEASURE_SUM = "Measure sum";
    public static final String MEASURE_WEIGHTED_CENTRE = "Measure weighted centre";
    public static final String MEASURE_WEIGHTED_EDGE_DISTANCE = "Measure weighted distance to edge";
    public static final String EDGE_DISTANCE_MODE = "Edge distance mode";
    public static final String MEASURE_EDGE_INTENSITY_PROFILE = "Measure intensity profile from edge";
    public static final String MINIMUM_DISTANCE = "Minimum distance";
    public static final String MAXIMUM_DISTANCE = "Maximum distance";
    public static final String CALIBRATED_DISTANCES = "Calibrated distances";
    public static final String NUMBER_OF_MEASUREMENTS = "Number of measurements";

    public interface Measurements {
        String MEAN = "MEAN";
        String MIN = "MIN";
        String MAX = "MAX";
        String SUM = "SUM";
        String STDEV = "STDEV";

        String X_CENT_MEAN = "X_CENTRE_MEAN (PX)";
        String X_CENT_STDEV = "X_CENTRE_STDEV (PX)";
        String Y_CENT_MEAN = "Y_CENTRE_MEAN (PX)";
        String Y_CENT_STDEV = "Y_CENTRE_STDEV (PX)";
        String Z_CENT_MEAN = "Z_CENTRE_MEAN (SLICE)";
        String Z_CENT_STDEV = "Z_CENTRE_STDEV (SLICE)";

        String MEAN_EDGE_DISTANCE_PX = "MEAN_EDGE_DISTANCE (PX)";
        String MEAN_EDGE_DISTANCE_CAL = "MEAN_EDGE_DISTANCE (${CAL})";
        String STD_EDGE_DISTANCE_PX = "STD_EDGE_DISTANCE (PX)";
        String STD_EDGE_DISTANCE_CAL = "STD_EDGE_DISTANCE (${CAL})";

        String EDGE_PROFILE = "EDGE_PROFILE";

    }

    public interface EdgeDistanceModes extends MeasureIntensityDistribution.EdgeDistanceModes {}


    public static String getFullName(String imageName, String measurement) {
        return "INTENSITY // "+imageName+"_"+measurement;
    }

    private double[] getProfileBins(double minDist, double maxDist, int nMeasurements) {
        double[] binNames = new double[nMeasurements];

        double binWidth = (maxDist-minDist)/(nMeasurements-1);
        for (int i=0;i<nMeasurements;i++) binNames[i] = (i*binWidth)+minDist;

        return binNames;

    }

    private String getBinNameFormat(boolean calibratedDistances) {
        if (calibratedDistances) {
            return "0.00E0";
        } else {
            return "#.00";
        }
    }

    private void measureIntensity(Obj object, ImagePlus ipl) {
        // Getting parameters
        String imageName = parameters.getValue(INPUT_IMAGE);

        // Running through all pixels in this object and adding the intensity to the MultiCumStat object
        int t = object.getT()+1;
        int nSlices = ipl.getNSlices();
        ImageStack timeStack = SubHyperstackMaker.makeSubhyperstack(ipl, "1-1", "1-"+nSlices, t+"-"+t).getStack();
        CumStat cs = IntensityCalculator.calculate(timeStack,object);

        // Calculating mean, std, min and max intensity
        if (parameters.getValue(MEASURE_MEAN))
            object.addMeasurement(new Measurement(getFullName(imageName,Measurements.MEAN), cs.getMean()));
        if (parameters.getValue(MEASURE_MIN))
            object.addMeasurement(new Measurement(getFullName(imageName,Measurements.MIN), cs.getMin()));
        if (parameters.getValue(MEASURE_MAX))
            object.addMeasurement(new Measurement(getFullName(imageName,Measurements.MAX), cs.getMax()));
        if (parameters.getValue(MEASURE_STDEV))
            object.addMeasurement(new Measurement(getFullName(imageName,Measurements.STDEV), cs.getStd(CumStat.SAMPLE)));
        if (parameters.getValue(MEASURE_SUM))
            object.addMeasurement(new Measurement(getFullName(imageName,Measurements.SUM), cs.getSum()));

    }

    private void measureWeightedCentre(Obj object, ImagePlus ipl) {
        // Getting parameters
        String imageName = parameters.getValue(INPUT_IMAGE);

        // Initialising the cumulative statistics objects to store pixel intensities in each direction.
        CumStat csX = new CumStat();
        CumStat csY = new CumStat();
        CumStat csZ = new CumStat();

        // Getting pixel coordinates
        ArrayList<Integer> x = object.getXCoords();
        ArrayList<Integer> y = object.getYCoords();
        ArrayList<Integer> z = object.getZCoords();
        int tPos = object.getT();

        // Running through all pixels in this object and adding the intensity to the MultiCumStat object
        for (int i=0;i<x.size();i++) {
            ipl.setPosition(1,z.get(i)+1,tPos+1);
            csX.addMeasure(x.get(i),ipl.getProcessor().getPixelValue(x.get(i),y.get(i)));
            csY.addMeasure(y.get(i),ipl.getProcessor().getPixelValue(x.get(i),y.get(i)));
            csZ.addMeasure(z.get(i),ipl.getProcessor().getPixelValue(x.get(i),y.get(i)));

        }

        object.addMeasurement(new Measurement(getFullName(imageName,Measurements.X_CENT_MEAN), csX.getMean()));
        object.addMeasurement(new Measurement(getFullName(imageName,Measurements.X_CENT_STDEV), csX.getStd()));
        object.addMeasurement(new Measurement(getFullName(imageName,Measurements.Y_CENT_MEAN), csY.getMean()));
        object.addMeasurement(new Measurement(getFullName(imageName,Measurements.Y_CENT_STDEV), csY.getStd()));
        object.addMeasurement(new Measurement(getFullName(imageName,Measurements.Z_CENT_MEAN), csZ.getMean()));
        object.addMeasurement(new Measurement(getFullName(imageName,Measurements.Z_CENT_STDEV), csZ.getStd()));

    }

    private void measureWeightedEdgeDistance(Obj object, Image image) {
        // Getting parameters
        String imageName = parameters.getValue(INPUT_IMAGE);
        String edgeDistanceMode = parameters.getValue(EDGE_DISTANCE_MODE);

        ObjCollection collection = new ObjCollection(object.getName());
        collection.add(object);
        CumStat cs = MeasureIntensityDistribution.measureIntensityWeightedProximity(collection,image,edgeDistanceMode);

        double distPerPxXY = object.getDistPerPxXY();

        object.addMeasurement(new Measurement(getFullName(imageName, Measurements.MEAN_EDGE_DISTANCE_PX), cs.getMean()));
        object.addMeasurement(new Measurement(Units.replace(getFullName(imageName, Measurements.MEAN_EDGE_DISTANCE_CAL)), cs.getMean()*distPerPxXY));
        object.addMeasurement(new Measurement(getFullName(imageName, Measurements.STD_EDGE_DISTANCE_PX), cs.getStd()));
        object.addMeasurement(new Measurement(Units.replace(getFullName(imageName, Measurements.STD_EDGE_DISTANCE_CAL)), cs.getStd()*distPerPxXY));

    }

    private void measureEdgeIntensityProfile(Obj object, ImagePlus intensityIpl) {
        // Getting parameters
        String imageName = parameters.getValue(INPUT_IMAGE);
        double minDist = parameters.getValue(MINIMUM_DISTANCE);
        double maxDist = parameters.getValue(MAXIMUM_DISTANCE);
        boolean calibratedDistances = parameters.getValue(CALIBRATED_DISTANCES);
        int nMeasurements = parameters.getValue(NUMBER_OF_MEASUREMENTS);
        double distPerPxXY = object.getDistPerPxXY();

        // Setting up CumStats to hold results
        LinkedHashMap<Double,CumStat> cumStats = new LinkedHashMap<>();
        double binWidth = (maxDist-minDist)/(nMeasurements-1);
        double[] bins = getProfileBins(minDist,maxDist,nMeasurements);
        for (int i=0;i<nMeasurements;i++) cumStats.put(bins[i],new CumStat());

        // Creating an object image
        ImagePlus objIpl = object.convertObjToImage("Inside dist", intensityIpl).getImagePlus();

        // Calculating the distance maps.  The inside map is set to negative
        ImagePlus outsideDistIpl = BinaryOperations.getDistanceMap3D(objIpl,true);
        InvertIntensity.process(objIpl);
        BinaryOperations.applyStockBinaryTransform(objIpl,BinaryOperations.OperationModes.ERODE_2D,1);
        ImagePlus insideDistIpl = BinaryOperations.getDistanceMap3D(objIpl,true);
        ImageMath.process(insideDistIpl,ImageMath.CalculationTypes.MULTIPLY,-1.0);
        ImagePlus distIpl = new ImageCalculator().process(insideDistIpl,outsideDistIpl,
                ImageCalculator.CalculationMethods.ADD,ImageCalculator.OverwriteModes.CREATE_NEW,true,true);

        // Iterating over each pixel in the image, adding that intensity value to the corresponding bin
        int nChannels = distIpl.getNChannels();
        int nSlices = distIpl.getNSlices();
        int nFrames = distIpl.getNFrames();

        // Checking the number of dimensions.  If a dimension of image2 is 1 this dimension is used for all images.
        for (int z = 1; z <= nSlices; z++) {
            for (int c = 1; c <= nChannels; c++) {
                for (int t = 1; t <= nFrames; t++) {
                    distIpl.setPosition(c,z,t);
                    intensityIpl.setPosition(c,z,t);

                    ImageProcessor distIpr = distIpl.getProcessor();
                    ImageProcessor intensityIpr = intensityIpl.getProcessor();

                    for (int x=0;x<distIpl.getWidth();x++) {
                        for (int y=0;y<distIpl.getHeight();y++) {
                            // Determining which bin to use
                            double dist = distIpr.getf(x,y);

                            // If using calibrated distances, this must be converted back to calibrated units from px
                            if (calibratedDistances) dist = dist*distPerPxXY;
                            double bin = Math.round((dist-minDist)/binWidth)*binWidth+minDist;

                            // Ensuring the bin is within the specified range
                            bin = Math.min(bin,maxDist);
                            bin = Math.max(bin,minDist);

                            // Adding the measurement to the relevant bin
                            double intensity = intensityIpr.getf(x,y);
                            cumStats.get(bin).addMeasure(intensity);
                        }
                    }
                }
            }
        }

        int nDigits = (int) Math.log10(bins.length)+1;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<nDigits;i++) stringBuilder.append("0");
        DecimalFormat intFormat = new DecimalFormat(stringBuilder.toString());
        String units = parameters.getValue(CALIBRATED_DISTANCES) ? Units.getOMEUnits().getSymbol() : "PX";

        DecimalFormat decFormat = new DecimalFormat(getBinNameFormat(calibratedDistances));

        int count = 0;
        for (CumStat cumStat:cumStats.values()) {
            String profileMeasName = Measurements.EDGE_PROFILE + "_BIN" + intFormat.format(count+1)
                    +"_("+decFormat.format(bins[count++])+units+")";
            object.addMeasurement(new Measurement(getFullName(imageName,profileMeasName), cumStat.getMean()));
        }
    }

    @Override
    public String getTitle() {
        return "Measure object intensity";

    }

    @Override
    public String getPackageName() {
        return PackageNames.OBJECT_MEASUREMENTS_INTENSITY;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void run(Workspace workspace) {
        // Getting input objects
        String objectName = parameters.getValue(INPUT_OBJECTS);
        ObjCollection objects = workspace.getObjects().get(objectName);

        // Getting input image
        String imageName = parameters.getValue(INPUT_IMAGE);
        Image image = workspace.getImages().get(imageName);
        ImagePlus ipl = image.getImagePlus();

        // Measuring intensity for each object and adding the measurement to that object
        for (Obj object:objects.values()) measureIntensity(object,ipl);

        // If specified, measuring weighted centre for intensity
        if (parameters.getValue(MEASURE_WEIGHTED_CENTRE)) {
            for (Obj object:objects.values()) measureWeightedCentre(object,ipl);
        }

        // If specified, measuring weighted distance to the object edge
        if (parameters.getValue(MEASURE_WEIGHTED_EDGE_DISTANCE)) {
            for (Obj object:objects.values()) measureWeightedEdgeDistance(object,image);
        }

        // If specified, measuring intensity profiles relative to the object edge
        if (parameters.getValue(MEASURE_EDGE_INTENSITY_PROFILE)) {
            for (Obj object:objects.values()) measureEdgeIntensityProfile(object,ipl);
        }
    }

    @Override
    public void initialiseParameters() {
        parameters.add(new Parameter(INPUT_OBJECTS, Parameter.INPUT_OBJECTS,null));
        parameters.add(new Parameter(INPUT_IMAGE, Parameter.INPUT_IMAGE,null));
        parameters.add(new Parameter(MEASURE_MEAN, Parameter.BOOLEAN, true));
        parameters.add(new Parameter(MEASURE_MIN, Parameter.BOOLEAN, true));
        parameters.add(new Parameter(MEASURE_MAX, Parameter.BOOLEAN, true));
        parameters.add(new Parameter(MEASURE_STDEV, Parameter.BOOLEAN, true));
        parameters.add(new Parameter(MEASURE_SUM, Parameter.BOOLEAN, true));
        parameters.add(new Parameter(MEASURE_WEIGHTED_CENTRE, Parameter.BOOLEAN, false));
        parameters.add(new Parameter(MEASURE_WEIGHTED_EDGE_DISTANCE, Parameter.BOOLEAN, false));
        parameters.add(new Parameter(EDGE_DISTANCE_MODE,Parameter.CHOICE_ARRAY,EdgeDistanceModes.INSIDE_AND_OUTSIDE,EdgeDistanceModes.ALL));
        parameters.add(new Parameter(MEASURE_EDGE_INTENSITY_PROFILE,Parameter.BOOLEAN,false));
        parameters.add(new Parameter(MINIMUM_DISTANCE,Parameter.DOUBLE,0d));
        parameters.add(new Parameter(MAXIMUM_DISTANCE,Parameter.DOUBLE,1d));
        parameters.add(new Parameter(CALIBRATED_DISTANCES, Parameter.BOOLEAN,false));
        parameters.add(new Parameter(NUMBER_OF_MEASUREMENTS,Parameter.INTEGER,10));

    }

    @Override
    public ParameterCollection updateAndGetParameters() {
        ParameterCollection returnedParameters = new ParameterCollection();

        returnedParameters.add(parameters.getParameter(INPUT_OBJECTS));
        returnedParameters.add(parameters.getParameter(INPUT_IMAGE));
        returnedParameters.add(parameters.getParameter(MEASURE_MEAN));
        returnedParameters.add(parameters.getParameter(MEASURE_MIN));
        returnedParameters.add(parameters.getParameter(MEASURE_MAX));
        returnedParameters.add(parameters.getParameter(MEASURE_STDEV));
        returnedParameters.add(parameters.getParameter(MEASURE_SUM));
        returnedParameters.add(parameters.getParameter(MEASURE_WEIGHTED_CENTRE));
        returnedParameters.add(parameters.getParameter(MEASURE_WEIGHTED_EDGE_DISTANCE));

        if (parameters.getValue(MEASURE_WEIGHTED_EDGE_DISTANCE)) {
            returnedParameters.add(parameters.getParameter(EDGE_DISTANCE_MODE));
        }

        returnedParameters.add(parameters.getParameter(MEASURE_EDGE_INTENSITY_PROFILE));
        if (parameters.getValue(MEASURE_EDGE_INTENSITY_PROFILE)) {
            returnedParameters.add(parameters.getParameter(MINIMUM_DISTANCE));
            returnedParameters.add(parameters.getParameter(MAXIMUM_DISTANCE));
            returnedParameters.add(parameters.getParameter(CALIBRATED_DISTANCES));
            returnedParameters.add(parameters.getParameter(NUMBER_OF_MEASUREMENTS));
        }

        return returnedParameters;

    }

    @Override
    public MeasurementReferenceCollection updateAndGetImageMeasurementReferences() {
        return null;
    }

    @Override
    public MeasurementReferenceCollection updateAndGetObjectMeasurementReferences() {
        String inputObjectsName = parameters.getValue(INPUT_OBJECTS);
        String inputImageName = parameters.getValue(INPUT_IMAGE);

        objectMeasurementReferences.setAllCalculated(false);

        if (parameters.getValue(MEASURE_MEAN)) {
            String name = getFullName(inputImageName,Measurements.MEAN);
            MeasurementReference mean = objectMeasurementReferences.getOrPut(name);
            mean.setImageObjName(inputObjectsName);
            mean.setCalculated(true);
            mean.setDescription("Mean intensity of pixels from the image \""+inputImageName+"\" contained within each" +
                    " \""+inputObjectsName+"\" object");
        }

        if (parameters.getValue(MEASURE_MIN)) {
            String name = getFullName(inputImageName,Measurements.MIN);
            MeasurementReference min = objectMeasurementReferences.getOrPut(name);
            min.setImageObjName(inputObjectsName);
            min.setCalculated(true);
            min.setDescription("Minimum intensity of pixels from the image \""+inputImageName+"\" contained within each" +
                    " \""+inputObjectsName+"\" object");
        }

        if (parameters.getValue(MEASURE_MAX)) {
            String name = getFullName(inputImageName,Measurements.MAX);
            MeasurementReference max = objectMeasurementReferences.getOrPut(name);
            max.setImageObjName(inputObjectsName);
            max.setCalculated(true);
            max.setDescription("Maximum intensity of pixels from the image \""+inputImageName+"\" contained within each" +
                    " \""+inputObjectsName+"\" object");
        }

        if (parameters.getValue(MEASURE_STDEV)) {
            String name = getFullName(inputImageName,Measurements.STDEV);
            MeasurementReference stdev = objectMeasurementReferences.getOrPut(name);
            stdev.setImageObjName(inputObjectsName);
            stdev.setCalculated(true);
            stdev.setDescription("Standard deviation of intensity of pixels from the image \""+inputImageName+"\" " +
                    "contained within each \""+inputObjectsName+"\" object");
        }

        if (parameters.getValue(MEASURE_SUM)) {
            String name = getFullName(inputImageName,Measurements.SUM);
            MeasurementReference sum = objectMeasurementReferences.getOrPut(name);
            sum.setImageObjName(inputObjectsName);
            sum.setCalculated(true);
            sum.setDescription("Sum intensity of pixels from the image \""+inputImageName+"\" contained within each" +
                    " \""+inputObjectsName+"\" object");
        }

        if (parameters.getValue(MEASURE_WEIGHTED_CENTRE)) {
            String name = getFullName(inputImageName, Measurements.X_CENT_MEAN);
            MeasurementReference reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Mean intensity weighted x-position for each \""+inputObjectsName+"\" object, " +
                    "with weighting coming from the image \""+inputImageName+"\".  Measured in pixel units.");

            name = getFullName(inputImageName, Measurements.X_CENT_STDEV);
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Standard deviation of intensity weighted x-position for each \""
                    +inputObjectsName+"\" object, with weighting coming from the image \""+inputImageName+"\".  " +
                    "Measured in pixel units.");

            name = getFullName(inputImageName, Measurements.Y_CENT_MEAN);
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Mean intensity weighted y-position for each \""+inputObjectsName+"\" object, " +
                    "with weighting coming from the image \""+inputImageName+"\".  Measured in pixel units.");

            name = getFullName(inputImageName, Measurements.Y_CENT_STDEV);
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Standard deviation of intensity weighted y-position for each \""
                    +inputObjectsName+"\" object, with weighting coming from the image \""+inputImageName+"\".  " +
                    "Measured in pixel units.");

            name = getFullName(inputImageName, Measurements.Z_CENT_MEAN);
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Mean intensity weighted z-position for each \""+inputObjectsName+"\" object, " +
                    "with weighting coming from the image \""+inputImageName+"\".  Measured in slice units.");

            name = getFullName(inputImageName, Measurements.Z_CENT_STDEV);
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Standard deviation of intensity weighted z-position for each \""
                    +inputObjectsName+"\" object, with weighting coming from the image \""+inputImageName+"\".  " +
                    "Measured in slice units.");

        }

        if (parameters.getValue(MEASURE_WEIGHTED_EDGE_DISTANCE)) {
            String name = getFullName(inputImageName, Measurements.MEAN_EDGE_DISTANCE_PX);
            MeasurementReference reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Mean intensity-weighted distance of all signal in the image, \""+inputImageName
                    +"\", to each object, \""+inputObjectsName+"\".  This value will get smaller as the brightest " +
                    "regions of the image get closer to the input objects.  Measured in pixel units.");

            name = Units.replace(getFullName(inputImageName, Measurements.MEAN_EDGE_DISTANCE_CAL));
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Mean intensity-weighted distance of all signal in the image, \""+inputImageName
                    +"\", to each object, \""+inputObjectsName+"\".  This value will get smaller as the brightest " +
                    "regions of the image get closer to the input objects.  Measured in calibrated ("
                    +Units.getOMEUnits().getSymbol()+") units.");

            name = getFullName(inputImageName, Measurements.STD_EDGE_DISTANCE_PX);
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Standard deviation intensity-weighted distance of all signal in the image, \""
                    +inputImageName+"\", to each object, \""+inputObjectsName+"\".  This value will get smaller as " +
                    "the brightest regions of the image get closer to the input objects.  Measured in pixel units.");

            name = Units.replace(getFullName(inputImageName, Measurements.STD_EDGE_DISTANCE_CAL));
            reference = objectMeasurementReferences.getOrPut(name);
            reference.setImageObjName(inputObjectsName);
            reference.setCalculated(true);
            reference.setDescription("Standard deviation intensity-weighted distance of all signal in the image, \""
                    +inputImageName+"\", to each object, \""+inputObjectsName+"\".  This value will get smaller as " +
                    "the brightest regions of the image get closer to the input objects.  Measured in calibrated ("
                    +Units.getOMEUnits().getSymbol()+") units.");

        }

        if (parameters.getValue(MEASURE_EDGE_INTENSITY_PROFILE)) {
            double minDist = parameters.getValue(MINIMUM_DISTANCE);
            double maxDist = parameters.getValue(MAXIMUM_DISTANCE);
            int nMeasurements = parameters.getValue(NUMBER_OF_MEASUREMENTS);
            double[] bins = getProfileBins(minDist,maxDist,nMeasurements);
            String units = parameters.getValue(CALIBRATED_DISTANCES) ? Units.getOMEUnits().getSymbol() : "PX";

            // Bin names must be in alphabetical order (for the MeasurementReferenceCollection TreeMap)
            int nDigits = (int) Math.log10(bins.length)+1;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<nDigits;i++) stringBuilder.append("0");
            DecimalFormat intFormat = new DecimalFormat(stringBuilder.toString());

            String nameFormat = getBinNameFormat(parameters.getValue(CALIBRATED_DISTANCES));
            DecimalFormat decFormat = new DecimalFormat(nameFormat);

            for (int i=0;i<nMeasurements;i++) {
                String profileMeasName = Measurements.EDGE_PROFILE+"_BIN"+intFormat.format(i+1)
                        +"_("+decFormat.format(bins[i])+units+")";

                String name = getFullName(inputImageName,profileMeasName);
                MeasurementReference reference = objectMeasurementReferences.getOrPut(name);
                reference.setImageObjName(inputObjectsName);
                reference.setCalculated(true);

                String minBin = i==0 ? "0" :decFormat.format(bins[i-1]);
                String maxBin = i== nMeasurements-1 ? "Inf." : decFormat.format(bins[i]+1);

                reference.setDescription("Mean intensity of the image, \""+inputImageName+"\" between "+minBin+" "
                        +units+" and "+maxBin+" "+units+" from each \""+inputObjectsName+"\" object.  This is the " +
                        intFormat.format(i+1)+" bin.");
            }
        }

        return objectMeasurementReferences;

    }

    @Override
    public MetadataReferenceCollection updateAndGetMetadataReferences() {
        return null;
    }

    @Override
    public void addRelationships(RelationshipCollection relationships) {

    }
}

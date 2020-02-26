package wbif.sjx.MIA.Object;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Object.References.ObjMeasurementRef;
import wbif.sjx.MIA.Object.References.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Process.ColourFactory;
import wbif.sjx.common.Object.LUTs;
import wbif.sjx.common.Object.Point;
import wbif.sjx.common.Object.Volume.SpatCal;
import wbif.sjx.common.Object.Volume.VolumeType;

import java.util.*;

/**
 * Created by sc13967 on 12/05/2017.
 */
public class ObjCollection extends LinkedHashMap<Integer,Obj> {
    private String name;
    private int maxID = 0;
    private final SpatCal cal;
    private final int nFrames;

    public ObjCollection(String name, SpatCal cal, int nFrames) {
        this.name = name;
        this.cal = cal;
        this.nFrames = nFrames;

    }

    public ObjCollection(String name, ObjCollection exampleCollection) {
        this.name = name;
        this.cal = exampleCollection.getCal();
        this.nFrames = exampleCollection.getnFrames();

    }

    public Obj createAndAddNewObject(VolumeType volumeType) {
        Obj newObject = new Obj(volumeType, name, getAndIncrementID(), cal, nFrames);
        add(newObject);

        return newObject;

    }

    public String getName() {
        return name;
    }

    synchronized public void add(Obj object) {
        put(object.getID(),object);

    }

    public SpatCal getCal() {
        return cal;
    }

    public int getAndIncrementID() {
        maxID++;
        return maxID;
    }

    public Obj getFirst() {
        if (size() == 0) return null;

        return values().iterator().next();

    }

    public int[][] getSpatialLimits() {
        // Taking limits from the first object, otherwise returning null
        if (size() == 0) return null;

        return new int[][]{{0,getFirst().getWidth()-1},{0,getFirst().getHeight()-1},{0,getFirst().getNSlices()-1}};

    }

    public int[] getTemporalLimits() {
        // Finding the first and last frame of all objects in the inputObjects set
        int[] limits = new int[2];
        limits[0] = Integer.MAX_VALUE;
        limits[1] = -Integer.MAX_VALUE;

        for (Obj object:values()) {
            if (object.getT() < limits[0]) limits[0] = object.getT();
            if (object.getT() > limits[1]) limits[1] = object.getT();

        }

        return limits;

    }

    public int getLargestID() {
        int largestID = 0;
        for (Obj obj:values()) {
            if (obj.getID() > largestID) largestID = obj.getID();
        }

        return largestID;

    }

    public Image convertToImage(String outputName, HashMap<Integer,Float> hues, int bitDepth, boolean nanBackground) {
                // Create output image
        ImagePlus ipl = createImage(outputName,bitDepth);

        // If it's a 32-bit image, set all background pixels to NaN
        if (bitDepth == 32 && nanBackground) setNaNBackground(ipl);

        // Labelling pixels in image
        for (Obj object:values()) {
            int tPos = object.getT();
            for (Point<Integer> point:object.getCoordinateSet()) {
                int xPos = point.x;
                int yPos = point.y;
                int zPos = point.z;

                ipl.setPosition(1,zPos+1,tPos+1);

                float hue = hues.get(object.getID());
                switch (bitDepth) {
                    case 8:
                    case 16:
                        ipl.getProcessor().putPixel(xPos,yPos,Math.round(hue*255));
                        break;
                    case 32:
                        ipl.getProcessor().putPixelValue(xPos,yPos,hue);
                        break;
                }
            }
        }

        // Assigning the spatial cal from the cal
        setCal(ipl);

        return new Image(outputName,ipl);

    }

    public Image convertToImageRandomColours() {
        HashMap<Integer,Float> hues = ColourFactory.getRandomHues(this);
        Image dispImage = convertToImage(name,hues,8,false);

        if (dispImage == null) return null;
        if (dispImage.getImagePlus() == null) return null;

        ImagePlus dispIpl = dispImage.getImagePlus();
        dispIpl.setLut(LUTs.Random(true));
        dispIpl.setPosition(1,1,1);
        dispIpl.updateChannelAndDraw();

        return dispImage;

    }

    public Image convertCentroidsToImage(String outputName, HashMap<Integer,Float> hues, int bitDepth, boolean nanBackground) {
        // Create output image
        ImagePlus ipl = createImage(outputName,bitDepth);

        // If it's a 32-bit image, set all background pixels to NaN
        if (bitDepth == 32 && nanBackground) setNaNBackground(ipl);

        // Labelling pixels in image
        for (Obj object:values()) {
            int tPos = object.getT();
            int xPos = (int) Math.round(object.getXMean(true));
            int yPos = (int) Math.round(object.getYMean(true));
            int zPos = (int) Math.round(object.getZMean(true,false));

            ipl.setPosition(1,zPos+1,tPos+1);

            float hue = hues.get(object.getID());
            switch (bitDepth) {
                case 8:
                case 16:
                    ipl.getProcessor().putPixel(xPos,yPos,Math.round(hue*255));
                    break;
                case 32:
                    ipl.getProcessor().putPixelValue(xPos,yPos,hue);
                    break;
            }
        }

        // Assigning the spatial cal from the cal
        setCal(ipl);

        return new Image(outputName,ipl);

    }

    public void applyCalibration(Image image) {
        Obj obj = getFirst();
        if (obj == null) return;

        Calibration calibration = image.getImagePlus().getCalibration();
        calibration.pixelWidth = obj.getDppXY();
        calibration.pixelHeight = obj.getDppXY();
        calibration.pixelDepth = obj.getDppZ();
        calibration.setUnit(obj.getUnits());

    }

    ImagePlus createImage(String outputName, int bitDepth) {
            // Creating a new image
            return IJ.createHyperStack(outputName, cal.getWidth(), cal.getHeight(),1, cal.getnSlices(),nFrames,bitDepth);

    }

    void setNaNBackground(ImagePlus ipl) {
        for (int z = 1; z <= ipl.getNSlices(); z++) {
            for (int c = 1; c <= ipl.getNChannels(); c++) {
                for (int t = 1; t <= ipl.getNFrames(); t++) {
                    for (int x=0;x<ipl.getWidth();x++) {
                        for (int y=0;y<ipl.getHeight();y++) {
                            ipl.setPosition(c,z,t);
                            ipl.getProcessor().putPixelValue(x,y,Double.NaN);
                        }
                    }
                }
            }
        }
    }

    void setCal(ImagePlus ipl) {
            ipl.getCalibration().pixelWidth = cal.getDppXY();
            ipl.getCalibration().pixelHeight = cal.getDppXY();
            ipl.getCalibration().pixelDepth = cal.getDppZ();
            ipl.getCalibration().setUnit(cal.getUnits());
    }

    /*
     * Returns the Obj with coordinates matching the Obj passed as an argument.  Useful for unit tests.
     */
    public Obj getByEquals(Obj referenceObj) {
        for (Obj testObj:values()) {
            if (testObj.equals(referenceObj)) return testObj;
        }

        return null;

    }

    public void resetCollection() {
        clear();
        maxID = 0;
    }

    /**
     * Displays measurement values from a specific Module
     * @param module
     */
    public void showMeasurements(Module module, ModuleCollection modules) {
        // Getting MeasurementReferences
        ObjMeasurementRefCollection measRefs = module.updateAndGetObjectMeasurementRefs();
        if (measRefs == null) return;

        // Creating a new ResultsTable for these values
        ResultsTable rt = new ResultsTable();

        // Getting a list of all measurements relating to this object collection
        LinkedHashSet<String> measNames = new LinkedHashSet<>();
        for (ObjMeasurementRef measRef:measRefs.values()) {
            if (measRef.getObjectsName().equals(name)) measNames.add(measRef.getName());
        }

        // Iterating over each measurement, adding all the values
        int row = 0;
        for (Obj obj:values()) {
            if (row != 0) rt.incrementCounter();

            // Setting some common values
            rt.setValue("ID",row,obj.getID());
            rt.setValue("X_CENTROID (PX)",row,obj.getXMean(true));
            rt.setValue("Y_CENTROID (PX)",row,obj.getYMean(true));
            rt.setValue("Z_CENTROID (SLICE)",row,obj.getZMean(true,false));
            rt.setValue("TIMEPOINT",row,obj.getT());

            // Setting the measurements from the Module
            for (String measName : measNames) {
                Measurement measurement = obj.getMeasurement(measName);
                double value = measurement == null ? Double.NaN : measurement.getValue();

                // Setting value
                rt.setValue(measName,row,value);

            }

            row++;

        }

        // Displaying the results table
        rt.show("\""+module.getName()+" \"measurements for \""+name+"\"");

    }

    public void showAllMeasurements() {
        // Creating a new ResultsTable for these values
        ResultsTable rt = new ResultsTable();

        // Iterating over each measurement, adding all the values
        int row = 0;
        for (Obj obj:values()) {
            if (row != 0) rt.incrementCounter();

            // Setting some common values
            rt.setValue("ID",row,obj.getID());
            rt.setValue("X_CENTROID (PX)",row,obj.getXMean(true));
            rt.setValue("Y_CENTROID (PX)",row,obj.getYMean(true));
            rt.setValue("Z_CENTROID (SLICE)",row,obj.getZMean(true,false));
            rt.setValue("TIMEPOINT",row,obj.getT());

            // Setting the measurements from the Module
            Set<String> measNames = obj.getMeasurements().keySet();
            for (String measName : measNames) {
                Measurement measurement = obj.getMeasurement(measName);
                double value = measurement == null ? Double.NaN : measurement.getValue();

                // Setting value
                rt.setValue(measName,row,value);

            }

            row++;

        }

        // Displaying the results table
        rt.show("All measurements for \""+name+"\"");

    }

    public void removeParents(String parentObjectsName) {
        for (Obj obj:values()) obj.removeParent(parentObjectsName);

    }

    public void removeChildren(String childObjectsName) {
        for (Obj obj:values()) obj.removeChildren(childObjectsName);
    }

    public boolean containsPoint(Point<Integer> point) {
        for (Obj obj:values()) {
            if (obj.contains(point)) return true;
        }

        return false;

    }

    public boolean containsPoint(int x, int y, int z) {
        Point<Integer> point = new Point<>(x,y,z);

        return containsPoint(point);

    }

    public Obj getLargestObject(int t) {
        Obj referenceObject = null;
        int objSize = Integer.MIN_VALUE;

        // Iterating over each object, checking it's size against the current reference values
        for (Obj currReferenceObject:values()) {
            // Only check objects in the current frame (if required - if frame doesn't matter, t = -1)
            if (t != -1 && currReferenceObject.getT() != t) continue;
            if (currReferenceObject.size() > objSize) {
                objSize = currReferenceObject.size();
                referenceObject = currReferenceObject;
            }
        }

        return referenceObject;

    }

    public Obj getSmallestObject(int t) {
        Obj referenceObject = null;
        int objSize = Integer.MAX_VALUE;

        // Iterating over each object, checking it's size against the current reference values
        for (Obj currReferenceObject:values()) {
            // Only check objects in the current frame (if required - if frame doesn't matter, t = -1)
            if (t != -1 && currReferenceObject.getT() != t) continue;

            if (currReferenceObject.size() < objSize) {
                objSize = currReferenceObject.size();
                referenceObject = currReferenceObject;
            }
        }

        return referenceObject;

    }

    public int getnFrames() {
        return nFrames;
    }
}

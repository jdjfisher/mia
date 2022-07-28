package io.github.mianalysis.mia.object.image;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.drew.lang.annotations.Nullable;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.measure.ResultsTable;
import ij.process.LUT;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.object.Measurement;
import io.github.mianalysis.mia.object.Obj;
import io.github.mianalysis.mia.object.Objs;
import io.github.mianalysis.mia.object.VolumeTypesInterface;
import io.github.mianalysis.mia.object.refs.ImageMeasurementRef;
import io.github.mianalysis.mia.object.refs.collections.ImageMeasurementRefs;
import io.github.sjcross.sjcommon.object.volume.VolumeType;
import net.imagej.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * Created by stephen on 30/04/2017.
 */
public abstract class Image <T extends RealType<T> & NativeType<T>> {
    protected String name;
    protected LinkedHashMap<String, Measurement> measurements = new LinkedHashMap<>();

    // Abstract methods

    public abstract void showImage(String title, @Nullable LUT lut, boolean normalise, boolean composite);

    public abstract void showImage(String title, @Nullable LUT lut, boolean normalise, boolean composite,
            Overlay overlay);

    public abstract ImagePlus getImagePlus();

    public abstract void setImagePlus(ImagePlus imagePlus);

    public abstract ImgPlus<T> getImgPlus();

    public abstract void setImgPlus(ImgPlus<T> img);

    public abstract Objs initialiseEmptyObjs(String outputObjectsName);

    public abstract Objs convertImageToSingleObjects(String type, String outputObjectsName, boolean blackBackground);

    public abstract Objs convertImageToObjects(String type, String outputObjectsName, boolean singleObject);

    public abstract void addObject(Obj obj, float hue);

    public abstract void addObjectCentroid(Obj obj, float hue);

    public abstract Image<T> duplicate(String outputImageName);

    public abstract Overlay getOverlay();

    public abstract void setOverlay(Overlay overlay);


    // PUBLIC METHODS

    public Objs convertImageToObjects(String outputObjectsName) {
        String type = getVolumeType(VolumeType.POINTLIST);
        return convertImageToObjects(type, outputObjectsName, false);
    }

    public Objs convertImageToObjects(String outputObjectsName, boolean singleObject) {
        String type = getVolumeType(VolumeType.POINTLIST);
        return convertImageToObjects(type, outputObjectsName, singleObject);
    }

    public Objs convertImageToObjects(VolumeType volumeType, String outputObjectsName) {
        String type = getVolumeType(volumeType);
        return convertImageToObjects(type, outputObjectsName, false);
    }

    public Objs convertImageToObjects(VolumeType volumeType, String outputObjectsName, boolean singleObject) {
        String type = getVolumeType(volumeType);
        return convertImageToObjects(type, outputObjectsName, singleObject);
    }

    public void addMeasurement(Measurement measurement) {
        measurements.put(measurement.getName(), measurement);

    }

    public Measurement getMeasurement(String name) {
        return measurements.get(name);

    }

    public void showImage(String title, LUT lut, Overlay overlay) {
        showImage(title, lut, true, false, overlay);
    }

    public void showImage(String title, LUT lut) {
        showImage(title, lut, true, false);
    }

    public void showImage(String title) {
        showImage(title, LUT.createLutFromColor(Color.WHITE));
    }

    public void showImage(LUT lut) {
        showImage(name, lut);
    }

    public void showImage() {
        showImage(name, null);
    }

    public void showImage(Overlay overlay) {
        showImage(name, null, overlay);
    }

    /**
     * Displays measurement values from a specific Module
     *
     * @param module
     */
    public void showMeasurements(Module module) {
        // Getting MeasurementReferences
        ImageMeasurementRefs measRefs = module.updateAndGetImageMeasurementRefs();

        // Creating a new ResultsTable for these values
        ResultsTable rt = new ResultsTable();

        // Getting a list of all measurements relating to this object collection
        LinkedHashSet<String> measNames = new LinkedHashSet<>();
        for (ImageMeasurementRef measRef : measRefs.values()) {
            if (measRef.getImageName().equals(name))
                measNames.add(measRef.getName());
        }

        // Setting the measurements from the Module
        for (String measName : measNames) {
            Measurement measurement = getMeasurement(measName);
            double value = measurement == null ? Double.NaN : measurement.getValue();

            // Setting value
            rt.setValue(measName, 0, value);

        }

        // Displaying the results table
        rt.show("\"" + module.getName() + " \"measurements for \"" + name + "\"");

    }

    public void showAllMeasurements() {
        // Creating a new ResultsTable for these values
        ResultsTable rt = new ResultsTable();

        // Getting a list of all measurements relating to this object collection
        Set<String> measNames = getMeasurements().keySet();

        // Setting the measurements from the Module
        int row = 0;
        for (String measName : measNames) {
            Measurement measurement = getMeasurement(measName);
            double value = measurement == null ? Double.NaN : measurement.getValue();

            // Setting value
            rt.setValue(measName, row, value);

        }

        // Displaying the results table
        rt.show("All measurements for \"" + name + "\"");

    }

    // PACKAGE PRIVATE METHODS

    public static VolumeType getVolumeType(String volumeType) {
        switch (volumeType) {
            case VolumeTypesInterface.OCTREE:
                return VolumeType.OCTREE;
            // case VolumeTypes.OPTIMISED:
            // return null;
            case VolumeTypesInterface.POINTLIST:
            default:
                return VolumeType.POINTLIST;
            case VolumeTypesInterface.QUADTREE:
                return VolumeType.QUADTREE;
        }
    }

    public static String getVolumeType(VolumeType volumeType) {
        switch (volumeType) {
            case OCTREE:
                return VolumeTypesInterface.OCTREE;
            case POINTLIST:
            default:
                return VolumeTypesInterface.POINTLIST;
            case QUADTREE:
                return VolumeTypesInterface.QUADTREE;
        }
    }

    // GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public HashMap<String, Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(LinkedHashMap<String, Measurement> singleMeasurements) {
        this.measurements = singleMeasurements;
    }
}
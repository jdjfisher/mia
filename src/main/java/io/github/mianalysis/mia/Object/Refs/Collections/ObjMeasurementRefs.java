package io.github.mianalysis.mia.Object.Refs.Collections;

import java.util.TreeMap;

import io.github.mianalysis.mia.Object.Measurement;
import io.github.mianalysis.mia.Object.Obj;
import io.github.mianalysis.mia.Object.Refs.ObjMeasurementRef;
import io.github.mianalysis.mia.Object.Units.SpatialUnit;
import io.github.mianalysis.mia.Object.Units.TemporalUnit;

public class ObjMeasurementRefs extends TreeMap<String, ObjMeasurementRef>
        implements Refs<ObjMeasurementRef> {
    /**
     *
     */
    private static final long serialVersionUID = 225316245096553320L;

    public void updateImageObjectName(String measurementName, String objectsName) {
        get(measurementName).setObjectsName(objectsName);
    }

    public String[] getMeasurementNames() {
        return keySet().toArray(new String[0]);
    }

    public ObjMeasurementRef getOrPut(String key) {
        // Stripping placeholders for units
        key = SpatialUnit.replace(key);
        key = TemporalUnit.replace(key);

        putIfAbsent((String) key, new ObjMeasurementRef((String) key));

        return get(key);

    }

    public boolean hasExportedMeasurements() {
        return size() >= 1;

    }

    public boolean add(ObjMeasurementRef ref) {
        put(ref.getName(), ref);
        return true;
    }

    public void addBlankMeasurements(Obj obj) {
        for (ObjMeasurementRef ref : values())
            obj.addMeasurement(new Measurement(ref.getName(), Double.NaN));
    }
}

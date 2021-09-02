package io.github.mianalysis.mia.Object.Measurements;

import io.github.mianalysis.mia.Object.Measurement;
import io.github.mianalysis.mia.Object.Obj;

public class ParentIDMeasurement extends Measurement {
    private Obj obj;
    private String parentName;

    public ParentIDMeasurement(String name, Obj obj, String parentName) {
        super(name);
        this.obj = obj;
        this.parentName = parentName;
    }
    
    public double getValue() {
        Obj parentObj = obj.getParent(parentName);

        if (parentObj == null)
            return Double.NaN;

        return parentObj.getID();

    }
}

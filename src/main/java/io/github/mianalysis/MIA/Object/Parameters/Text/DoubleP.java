package io.github.mianalysis.mia.Object.Parameters.Text;

import io.github.mianalysis.mia.MIA;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.module.Miscellaneous.GlobalVariables;
import io.github.mianalysis.mia.Object.Parameters.Abstract.Parameter;
import io.github.mianalysis.mia.Object.Parameters.Abstract.TextType;

public class DoubleP extends TextType {
    protected String value;

    public DoubleP(String name, Module module, double value) {
        super(name, module);
        this.value = String.valueOf(value);
    }

    public DoubleP(String name, Module module, String value) {
        super(name, module);
        this.value = value;
    }

    public DoubleP(String name, Module module, double value, String description) {
        super(name, module, description);
        this.value = String.valueOf(value);
    }

    public DoubleP(String name, Module module, String value, String description) {
        super(name, module, description);
        this.value = value;
    }

    public void setValue(double value) {
        this.value = String.valueOf(value);
    }

    public void setValue(String value) throws NumberFormatException {
        // Checking this is valid
        if (GlobalVariables.containsValue(value) || containsCalculation(value)) {
            this.value = value;
        } else {
            try {
                Double.parseDouble(value);
                this.value = value;
            } catch (NumberFormatException e) {
                MIA.log.writeWarning("Module \"" + module.getName() + "\", parameter \"" + getName()
                        + " \". Must either:" + "\n    - Be a double-precision number"
                        + "\n    - Be a global variable handle (e.g. V{name}) "
                        + "\n    - Contain a calculation (e.g. C{3-6}.  "
                        + "\nNote: Global variables and calculations can be combined (e.g. C{V{name1} + V{name2} - 4})");
            }
        }
    }

    @Override
    public String getRawStringValue() {
        return value;

    }

    @Override
    public <T extends Parameter> T duplicate(Module newModule) {
        DoubleP newParameter = new DoubleP(name, newModule, value, getDescription());
        newParameter.setNickname(getNickname());
        newParameter.setVisible(isVisible());
        newParameter.setExported(isExported());

        return (T) newParameter;

    }

    @Override
    public void setValueFromString(String value) {
        setValue(value);
    }

    @Override
    public <T> T getValue() throws NumberFormatException {
        String converted1 = GlobalVariables.convertString(value, module.getModules());
        String converted2 = applyCalculation(converted1);

        return (T) (Double) Double.parseDouble(converted2);

    }

    @Override
    public <T> void setValue(T value) {
        this.value = String.valueOf(value);

    }
}

package io.github.mianalysis.mia.Object.Parameters.Objects;

import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.Object.Parameters.InputObjectsP;

import com.drew.lang.annotations.NotNull;
import java.util.LinkedHashSet;

public class InputSkeletonObjectsP extends InputObjectsP {
    public InputSkeletonObjectsP(String name, Module module) {
        super(name, module);
    }

    public InputSkeletonObjectsP(String name, Module module, @NotNull String choice) {
        super(name, module, choice);
    }

    public InputSkeletonObjectsP(String name, Module module, @NotNull String choice, String description) {
        super(name, module, choice, description);
    }

    @Override
    public String[] getChoices() {
        LinkedHashSet<OutputObjectsP> objects = module.getModules().getAvailableObjects(module,OutputSkeletonObjectsP.class);
        return objects.stream().map(OutputObjectsP::getObjectsName).distinct().toArray(String[]::new);
    }
}

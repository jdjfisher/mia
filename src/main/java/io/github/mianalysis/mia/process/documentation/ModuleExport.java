package io.github.mianalysis.mia.process.documentation;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.json.JSONObject;

import io.github.mianalysis.mia.MIA;
import io.github.mianalysis.mia.module.AvailableModules;
import io.github.mianalysis.mia.module.Categories;
import io.github.mianalysis.mia.module.Category;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.module.Modules;
import io.github.mianalysis.mia.object.parameters.abstrakt.Parameter;

public class ModuleExport {
    private static final String OUTPUT_PATH = "./modules.json";
    private static final int JSON_INDENTATION = 2;

    public static void main(String[] args) {
        TreeMap<String, Module> modules = getModules();

        Category rootCategory = Categories.getRootCategory();

        List<JSONObject> moduleList = modules.values()
                .stream()
                .map(ModuleExport::generateModule)
                .collect(Collectors.toList());

        JSONObject json = new JSONObject()
                .put("modules", moduleList)
                .put("categories", generateCategory(rootCategory));

        export(json);
    }

    private static void export(JSONObject json) {
        String jsonString = json.toString(JSON_INDENTATION);

        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_PATH))) {
            out.write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject generateCategory(Category category) {
        List<JSONObject> children = category.getChildren()
                .stream()
                .map(ModuleExport::generateCategory)
                .collect(Collectors.toList());

        return new JSONObject()
                .put("name", category.getName())
                .put("slug", slugify(category.getName()))
                .put("description", category.getDescription())
                .put("children", children);

    }

    private static JSONObject generateModule(Module module) {
        List<JSONObject> parameters = module.getAllParameters()
                .values()
                .stream()
                .map(ModuleExport::generateParameter)
                .collect(Collectors.toList());

        return new JSONObject()
                .put("name", module.getName())
                .put("slug", slugify(module.getName()))
                .put("shortDescription", module.getShortDescription())
                .put("fullDescription", module.getDescription())
                .put("parameters", parameters);
    }

    private static JSONObject generateParameter(Parameter parameter) {
        return new JSONObject()
                .put("name", parameter.getName())
                .put("description", parameter.getDescription());
    }

    private static String slugify(String name) {
        return name.toLowerCase()
                .replaceAll("[/\\\\?%*:|\"<>]", "")
                .replaceAll(" ", "-");
    }

    private static TreeMap<String, Module> getModules() {
        // Get a list of Modules
        List<String> moduleNames = AvailableModules.getModuleNames(false);

        // Converting the list of classes to a list of Modules
        TreeMap<String, Module> modules = new TreeMap<>();
        Modules tempCollection = new Modules();
        for (String className : moduleNames) {
            try {
                Class<Module> clazz = (Class<Module>) Class.forName(className);

                // Skip any abstract Modules
                if (Modifier.isAbstract(clazz.getModifiers()))
                    continue;

                Constructor<Module> constructor = clazz.getDeclaredConstructor(Modules.class);
                Module module = (Module) constructor.newInstance(tempCollection);
                modules.put(module.getName(), module);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                MIA.log.writeError(e);
            }
        }

        return modules;
    }
}

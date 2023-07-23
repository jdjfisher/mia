package io.github.mianalysis.mia.process.documentation;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
    private static TreeMap<Category, List<Module>> modulesByCategory;

    public static void main(String[] args) {
        modulesByCategory = getModules();

        JSONObject json = generateCategory(Categories.getRootCategory());

        export(json);
    }

    private static void export(JSONObject json) {
        final String jsonString = json.toString(JSON_INDENTATION);

        try (PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_PATH))) {
            out.write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject generateCategory(Category category) {
        final List<JSONObject> children = category.getChildren()
                .stream()
                .map(ModuleExport::generateCategory)
                .collect(Collectors.toList());

        final List<JSONObject> modules = modulesByCategory.getOrDefault(category, new ArrayList<>())
                .stream()
                .map(ModuleExport::generateModule)
                .collect(Collectors.toList());

        return new JSONObject()
                .put("name", category.getName())
                .put("slug", slugify(category.getName()))
                .put("description", category.getDescription())
                .put("sub_categories", children)
                .put("modules", modules);
    }

    private static JSONObject generateModule(Module module) {
        final List<JSONObject> parameters = module.getAllParameters()
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

    private static TreeMap<Category, List<Module>> getModules() {
        // Get a list of Modules
        List<String> moduleNames = AvailableModules.getModuleNames(false);

        // Converting the list of classes to a list of Modules
        TreeMap<Category, List<Module>> modulesByCategory = new TreeMap<>();

        Modules tempCollection = new Modules();

        for (String className : moduleNames) {
            try {
                Class<Module> clazz = (Class<Module>) Class.forName(className);

                // Skip any abstract Modules
                if (Modifier.isAbstract(clazz.getModifiers()))
                    continue;

                Constructor<Module> constructor = clazz.getDeclaredConstructor(Modules.class);
                Module module = (Module) constructor.newInstance(tempCollection);

                modulesByCategory.compute(module.getCategory(), (category, modules) -> {
                    if (modules == null)
                        modules = new ArrayList<>();

                    modules.add(module);

                    return modules;
                });

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                    | InvocationTargetException e) {
                MIA.log.writeError(e);
            }
        }

        return modulesByCategory;
    }
}

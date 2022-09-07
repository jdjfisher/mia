package io.github.mianalysis.mia;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import ij.Prefs;
import io.github.mianalysis.mia.gui.GUI;
import io.github.mianalysis.mia.module.LostAndFound;
import io.github.mianalysis.mia.module.Module;
import io.github.mianalysis.mia.module.core.InputControl;
import io.github.mianalysis.mia.moduledependencies.Dependencies;
import io.github.mianalysis.mia.object.system.Preferences;
import io.github.mianalysis.mia.process.DependencyValidator;
import io.github.mianalysis.mia.process.analysishandling.Analysis;
import io.github.mianalysis.mia.process.analysishandling.AnalysisReader;
import io.github.mianalysis.mia.process.analysishandling.AnalysisRunner;
import io.github.mianalysis.mia.process.logging.BasicLogRenderer;
import io.github.mianalysis.mia.process.logging.ConsoleRenderer;
import io.github.mianalysis.mia.process.logging.HeadlessRenderer;
import io.github.mianalysis.mia.process.logging.Log;
import io.github.mianalysis.mia.process.logging.LogHistory;
import io.github.mianalysis.mia.process.logging.LogRenderer;
import net.imagej.ImageJ;
import net.imagej.ImageJService;

/**
 * Created by Stephen Cross on 14/07/2017.
 */
@Plugin(type = Command.class, menuPath = "Plugins>ModularImageAnalysis (MIA)", visible = true)
public class MIA implements Command {
    private static String version = "";
    private static boolean debug = false;
    private static LogRenderer mainRenderer = new BasicLogRenderer();
    private static LogHistory logHistory = new LogHistory();
    private static boolean headless = false; // Determines if there is a GUI
    private static Preferences preferences;
    private static Dependencies dependencies; // Maps module dependencies and reports if a
    // module's requirements aren't satisfied
    private static LostAndFound lostAndFound; // Maps missing modules and parameters to
    // replacements (e.g. if a module was renamed)

    public static Log log = new Log(mainRenderer); // This is for testing and headless modes

    /*
     * Gearing up for the transition from ImagePlus to ImgLib2 formats. Modules can
     * use this to addRef compatibility.
     */
    private static final boolean imagePlusMode = true;

    @Parameter
    public static ImageJService ijService;

    @Parameter(label = "Workflow file path", required = true)
    public String workflowPath;

    @Parameter(label = "Input file path", required = false)
    public String inputFilePath;

    @Parameter(label = "showDebug", required = false)
    public boolean showDebug = false;

    @Parameter(label = "showMemory", required = false)
    public boolean showMemory = false;

    @Parameter(label = "showMessage", required = false)
    public boolean showMessage = true;

    @Parameter(label = "showStatus", required = false)
    public boolean showStatus = true;

    @Parameter(label = "showWarning", required = false)
    public boolean showWarning = true;

    @Parameter(label = "verbose", required = false)
    public boolean verbose = false;

    public static void main(String[] args) throws Exception {
        debug = true;

        try {
            if (args.length == 0) {
                new ij.ImageJ();
                new ImageJ().command().run("io.github.mianalysis.mia.MIA", false);
            } else if (args.length == 1) {
                Analysis analysis = AnalysisReader.loadAnalysis(args[0]);
                new AnalysisRunner().run(analysis);
            } else if (args.length == 2) {
                Analysis analysis = AnalysisReader.loadAnalysis(args[0]);
                analysis.getModules().getInputControl().updateParameterValue(InputControl.INPUT_PATH, args[1]);
                new AnalysisRunner().run(analysis);
            }

        } catch (Exception e) {
            MIA.log.writeError(e);
        }
    }

    @Override
    public void run() {
        // If parameters are specified, running in headless mode
        if (workflowPath == null)
            runInteractive();
        else
            runHeadless();

    }

    public void runHeadless() {
        headless = true;

        try {
            // Before removing the old renderer we want to check the new one can be created
            HeadlessRenderer newRenderer = new HeadlessRenderer();
            HeadlessRenderer.setShowProgress(true);
            HeadlessRenderer.setProgress(0);

            newRenderer.setWriteEnabled(LogRenderer.Level.DEBUG, showDebug);
            newRenderer.setWriteEnabled(LogRenderer.Level.MEMORY, showMemory);
            newRenderer.setWriteEnabled(LogRenderer.Level.MESSAGE, showMessage);
            newRenderer.setWriteEnabled(LogRenderer.Level.STATUS, showStatus);
            newRenderer.setWriteEnabled(LogRenderer.Level.WARNING, showWarning);

            log.removeRenderer(mainRenderer);
            log.addRenderer(newRenderer);

            mainRenderer = newRenderer;
            
            Module.setVerbose(verbose);

            if (inputFilePath == null) {
                Analysis analysis = AnalysisReader.loadAnalysis(new File(workflowPath));
                new AnalysisRunner().run(analysis);
            } else {
                MIA.log.writeDebug(inputFilePath);
                Analysis analysis = AnalysisReader.loadAnalysis(new File(workflowPath));
                analysis.getModules().getInputControl().updateParameterValue(InputControl.INPUT_PATH,
                        inputFilePath);
                new AnalysisRunner().run(analysis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.lang.System.exit(0);

    }

    public void runInteractive() {
        try {
            String theme = Prefs.get("MIA.GUI.theme", io.github.mianalysis.mia.gui.Themes.getDefaultTheme());
            UIManager.setLookAndFeel(io.github.mianalysis.mia.gui.Themes.getThemeClass(theme));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        try {
            // Before removing the old renderer we want to check the new one can be created
            UIService uiService = ijService.context().getService(UIService.class);
            LogRenderer newRenderer = new ConsoleRenderer(uiService);
            log.removeRenderer(mainRenderer);

            mainRenderer = newRenderer;
            mainRenderer.setWriteEnabled(LogRenderer.Level.DEBUG, debug);
            log.addRenderer(mainRenderer);
        } catch (Exception e) {
            // If any exception was thrown, just don't apply the ConsoleRenderer.
        }

        preferences = new Preferences(null);

        log.addRenderer(logHistory);

        // Determining the version number from the pom file
        try {
            if (new File("pom.xml").exists()) {
                FileReader reader = new FileReader("pom.xml");
                Model model = new MavenXpp3Reader().read(reader);
                reader.close();
                version = model.getVersion();
            } else {
                version = getClass().getPackage().getImplementationVersion();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        // Run the dependency validator. If updates were required, return.
        if (DependencyValidator.run())
            return;

        try {
            new GUI();
        } catch (Exception e) {
            MIA.log.writeError(e);
        }
    }

    public static boolean isImagePlusMode() {
        return imagePlusMode;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        MIA.debug = debug;
    }

    public static Log getLog() {
        return log;
    }

    public static LogRenderer getMainRenderer() {
        return mainRenderer;
    }

    public static LogHistory getLogHistory() {
        return logHistory;
    }

    public static void clearLogHistory() {
        logHistory.clearLogHistory();
    }

    public static void setLog(Log log) {
        MIA.log = log;
    }

    public static boolean isHeadless() {
        return headless;
    }

    public static Preferences getPreferences() {
        if (preferences == null)
            preferences = new Preferences(null);

        return preferences;
    }

    public static Dependencies getDependencies() {
        if (dependencies == null)
            dependencies = new Dependencies();

        return dependencies;

    }

    public static LostAndFound getLostAndFound() {
        if (lostAndFound == null)
            lostAndFound = new LostAndFound();

        return lostAndFound;

    }
}
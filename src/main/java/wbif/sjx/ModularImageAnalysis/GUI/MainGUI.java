// TODO: Add controls for all parameter types (hashsets, etc.)
// TODO: If an assigned image/object name is no longer available, flag up the module button in red

package wbif.sjx.ModularImageAnalysis.GUI;

import org.reflections.Reflections;
import wbif.sjx.ModularImageAnalysis.Exceptions.GenericMIAException;
import wbif.sjx.ModularImageAnalysis.Module.*;
import wbif.sjx.ModularImageAnalysis.Object.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

import java.io.*;
import java.util.*;

/**
 * Created by Stephen on 20/05/2017.
 */
public class MainGUI {
    private int frameWidth = 1100;
    private int frameHeight = 750;
    private int elementHeight = 25;

    private ComponentFactory componentFactory;
    private Workspace testWorkspace = new Workspace(1,null);
    private HCModule activeModule = null;
    private JFrame frame = new JFrame();
    private JMenuBar menuBar = new JMenuBar();
    private JPanel controlPanel = new JPanel();
    private JPanel inputPanel = new JPanel();
    private JPanel modulesPanel = new JPanel();
    private JScrollPane modulesScrollPane = new JScrollPane(modulesPanel);
    private JPanel paramsPanel = new JPanel();
    private JScrollPane paramsScrollPane = new JScrollPane(paramsPanel);
    private JPanel statusPanel = new JPanel();
    private JPanel basicControlPanel = new JPanel();
    private JPanel basicModulesPanel = new JPanel();
    private JScrollPane basicModulesScrollPane = new JScrollPane(basicModulesPanel);
    private JPopupMenu moduleListMenu = new JPopupMenu();
    private int lastModuleEval = -1;
    private boolean basicGUI = true;

    private GUIAnalysis analysis = new GUIAnalysis();
    private ModuleCollection modules = analysis.modules;

    public MainGUI() throws InstantiationException, IllegalAccessException {
        componentFactory = new ComponentFactory(this,elementHeight);

        // Setting location of panel
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - frameWidth) / 2, (screenSize.height - frameHeight) / 2);

        frame.setLayout(new GridBagLayout());
        frame.setTitle("Modular image analysis (version "+getClass().getPackage().getImplementationVersion()+")");

        // Creating the menu bar
        initialiseMenuBar();
        frame.setJMenuBar(menuBar);

//        renderBasicMode();
        renderEditingMode();

        // Final bits for listeners
//        frame.addMouseListener(this);
        frame.setVisible(true);

        // Populating the list containing all available modules
        listAvailableModules();
        moduleListMenu.show(frame,0,0);
        moduleListMenu.setVisible(false);

    }

    private void initialiseMenuBar() {
        // Creating the file menu
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        menu.add(new AnalysisMenuItem(this,AnalysisMenuItem.LOAD_ANALYSIS));
        menu.add(new AnalysisMenuItem(this,AnalysisMenuItem.SAVE_ANALYSIS));

        // Creating the analysis menu
        menu = new JMenu("Analysis");
        menuBar.add(menu);

        menu.add(new AnalysisMenuItem(this,AnalysisMenuItem.SET_FILE_TO_ANALYSE));
        menu.add(new AnalysisMenuItem(this,AnalysisMenuItem.START_ANALYSIS));
        menu.add(new AnalysisMenuItem(this,AnalysisMenuItem.STOP_ANALYSIS));

        // Creating the new menu
        menu = new JMenu("View");
        menuBar.add(menu);

        ButtonGroup group = new ButtonGroup();
        ViewControlButton rbMenuItem = new ViewControlButton(this,ViewControlButton.BASIC_MODE);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new ViewControlButton(this,ViewControlButton.EDITING_MODE);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

    }

    private void clearFrame() {
        frame.remove(controlPanel);
        frame.remove(modulesPanel);
        frame.remove(modulesScrollPane);
        frame.remove(statusPanel);
        frame.remove(paramsPanel);
        frame.remove(paramsScrollPane);
        frame.remove(basicControlPanel);
        frame.remove(basicModulesScrollPane);

    }

    void renderBasicMode() {
        basicGUI = true;

        clearFrame();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,0,5);
        c.gridx = 0;
        c.gridy = 0;

        // Initialising the control panel
        initialiseBasicControlPanel();
        frame.add(basicControlPanel,c);

        // Initialising the parameters panel
        initialiseBasicModulesPanel();
        c.gridy++;
        frame.add(basicModulesScrollPane,c);

        // Initialising the status panel
//        initialiseStatusPanel(500);
//        c.gridx = 0;
//        c.gridy++;
//        c.gridwidth = 1;
//        c.insets = new Insets(5,5,5,5);
//        frame.add(statusPanel,c);

        frame.pack();
        frame.revalidate();
        frame.repaint();

        populateBasicModules();

    }

    void renderEditingMode() throws InstantiationException, IllegalAccessException {
        basicGUI = false;

        clearFrame();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,0);
        c.gridx = 0;
        c.gridy = 0;

        // Creating buttons to add and remove modules
        initialiseControlPanel();
        frame.add(controlPanel,c);

        // Initialising the module list
        initialisingModulesPanel();
        c.gridx++;
        frame.add(modulesScrollPane,c);

        // Initialising the parameters panel
        initialiseParametersPanel();
        c.gridx++;
        c.insets = new Insets(5,5,5,5);
        frame.add(paramsScrollPane,c);

        // Initialising the status panel
//        initialiseStatusPanel(1090);
//        c.gridx = 0;
//        c.gridy++;
//        c.gridwidth = 3;
//        c.insets = new Insets(0,5,5,5);
//        frame.add(statusPanel,c);

        frame.pack();
        frame.revalidate();
        frame.repaint();

        populateModuleList();
        populateModuleParameters();

    }

    private void initialiseControlPanel() {
        controlPanel = new JPanel();
        int buttonSize = 50;

        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(buttonSize + 15, frameHeight-50));
        controlPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.PAGE_START;

        // Add module button
        ModuleControlButton.setButtonSize(buttonSize);
        ModuleControlButton addModuleButton = new ModuleControlButton(this,ModuleControlButton.ADD_MODULE);
        addModuleButton.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,20));
        controlPanel.add(addModuleButton, c);

        // Remove module button
        ModuleControlButton removeModuleButton = new ModuleControlButton(this,ModuleControlButton.REMOVE_MODULE);
        removeModuleButton.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,20));
        c.gridy++;
        controlPanel.add(removeModuleButton, c);

        // Move module up button
        ModuleControlButton moveModuleUpButton = new ModuleControlButton(this,ModuleControlButton.MOVE_MODULE_UP);
        moveModuleUpButton.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,16));
        c.gridy++;
        controlPanel.add(moveModuleUpButton, c);

        // Move module down button
        ModuleControlButton moveModuleDownButton = new ModuleControlButton(this,ModuleControlButton.MOVE_MODULE_DOWN);
        moveModuleDownButton.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,16));
        c.gridy++;
        controlPanel.add(moveModuleDownButton, c);

        // Load analysis protocol button
        AnalysisControlButton loadAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.LOAD_ANALYSIS);
        c.gridy++;
        c.weighty = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        controlPanel.add(loadAnalysisButton, c);

        // Save analysis protocol button
        AnalysisControlButton saveAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.SAVE_ANALYSIS);
        c.gridy++;
        c.weighty = 0;
        controlPanel.add(saveAnalysisButton, c);

        // Start analysis button
        AnalysisControlButton startAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.START_ANALYSIS);
        c.gridy++;
        controlPanel.add(startAnalysisButton, c);

        // Stop analysis button
        AnalysisControlButton stopAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.STOP_ANALYSIS);
        c.gridy++;
        controlPanel.add(stopAnalysisButton, c);

        controlPanel.validate();
        controlPanel.repaint();

    }

    private void initialiseInputPanel() {


    }

    private void initialisingModulesPanel() {
        modulesScrollPane = new JScrollPane(modulesPanel);
        int buttonWidth = 300;

        // Initialising the scroll panel
        modulesScrollPane.setPreferredSize(new Dimension(buttonWidth + 15, frameHeight-50));
        modulesScrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        modulesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        modulesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Initialising the panel for module buttons
        modulesPanel.setLayout(new GridBagLayout());
        modulesPanel.validate();
        modulesPanel.repaint();

        modulesScrollPane.validate();
        modulesScrollPane.repaint();

    }

    private void initialiseParametersPanel() {
        paramsScrollPane = new JScrollPane(paramsPanel);

        // Initialising the scroll panel
        paramsScrollPane.setPreferredSize(new Dimension(700, frameHeight-50));
        paramsScrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        paramsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paramsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        paramsPanel.removeAll();

        paramsPanel.setLayout(new GridBagLayout());

        // Adding placeholder text
        JTextField textField = new JTextField("Select a module to edit its parameters");
        textField.setFont(new Font(Font.SANS_SERIF,Font.BOLD,12));
        textField.setBorder(null);
        textField.setEditable(false);
        paramsPanel.add(textField);

        paramsPanel.validate();
        paramsPanel.repaint();

        paramsScrollPane.validate();
        paramsScrollPane.repaint();

    }

    private void initialiseStatusPanel(int width) {
        statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(width,40));
        statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        statusPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        JTextField textField = new JTextField();
        textField.setBackground(null);
        textField.setPreferredSize(new Dimension(width-20,25));
        textField.setBorder(null);
        textField.setText("Modular image analysis (version "+getClass().getPackage().getImplementationVersion()+")");
        textField.setFont(new Font(Font.SANS_SERIF,Font.BOLD,12));
        statusPanel.add(textField,c);

        OutputStreamTextField outputStreamTextField = new OutputStreamTextField(textField);
        PrintStream printStream = new PrintStream(outputStreamTextField);
        System.setOut(printStream);

    }

    private void initialiseBasicControlPanel() {
        basicControlPanel = new JPanel();
        int buttonSize = 50;

        basicControlPanel = new JPanel();
        basicControlPanel.setPreferredSize(new Dimension(500, buttonSize + 15));
        basicControlPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        basicControlPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.FIRST_LINE_START;

        // Load analysis protocol button
        AnalysisControlButton.setButtonSize(buttonSize);
        AnalysisControlButton loadAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.LOAD_ANALYSIS);
        c.gridx++;
        c.anchor = GridBagConstraints.PAGE_END;
        basicControlPanel.add(loadAnalysisButton, c);

        // Save analysis protocol button
        AnalysisControlButton saveAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.SAVE_ANALYSIS);
        c.gridx++;
        basicControlPanel.add(saveAnalysisButton, c);

        // Start analysis button
        AnalysisControlButton startAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.START_ANALYSIS);
        c.gridx++;
        c.weightx = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        basicControlPanel.add(startAnalysisButton, c);

        // Stop analysis button
        AnalysisControlButton stopAnalysisButton = new AnalysisControlButton(this,AnalysisControlButton.STOP_ANALYSIS);
        c.gridx++;
        c.weightx = 0;
        basicControlPanel.add(stopAnalysisButton, c);

        basicControlPanel.validate();
        basicControlPanel.repaint();

    }

    private void initialiseBasicModulesPanel() {
        int elementWidth = 500;

        // Initialising the scroll panel
        basicModulesScrollPane.setPreferredSize(new Dimension(elementWidth, frameHeight-165));
        basicModulesScrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        basicModulesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        basicModulesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Initialising the panel for module buttons
        basicModulesPanel.setLayout(new GridBagLayout());
        basicModulesPanel.validate();
        basicModulesPanel.repaint();

        basicModulesScrollPane.validate();
        basicModulesScrollPane.repaint();

    }

    void populateInputMode() {

    }

    void populateModuleList() {
        modulesPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;

        ButtonGroup group = new ButtonGroup();

        // Adding module buttons
        for (HCModule module : modules) {
            Color color;
            int idx = modules.indexOf(module);

            if (!module.isEnabled()) {
                color = Color.LIGHT_GRAY;
            } else {
                if (idx <= lastModuleEval) {
                    color = Color.getHSBColor(0.27f,1f,0.6f);
                } else {
                    color = Color.getHSBColor(0f,1f,0.6f);
                }
            }

            if (idx == modules.size()-1) c.weighty = 1;

            JPanel modulePanel = componentFactory.createAdvancedModuleControl(module,group,activeModule,color,300-25);
            modulesPanel.add(modulePanel, c);
            c.gridy++;

        }

        modulesPanel.validate();
        modulesPanel.repaint();
        modulesScrollPane.validate();
        modulesScrollPane.repaint();

    }

    void populateModuleParameters() {
        paramsPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(0,0,0,5);
        c.fill = GridBagConstraints.HORIZONTAL;

        // If the active module is set to null (i.e. we're looking at the analysis options panel) exit this method
        if (activeModule == null) {
            return;
        }

        // If the active module hasn't got parameters enabled, skip it
        if (activeModule.getActiveParameters() != null) {
            Iterator<Parameter> iterator = activeModule.getActiveParameters().values().iterator();
            while (iterator.hasNext()) {
                Parameter parameter = iterator.next();

                c.gridx = 0;
                JPanel paramPanel = componentFactory.createParameterControl(parameter,modules,activeModule,635);
                paramsPanel.add(paramPanel,c);

                // Adding a checkbox to determine if the parameter should be visible to the user
                c.gridx++;
                paramsPanel.add(new VisibleCheck(parameter),c);

                c.gridy++;

            }
        }

        // Creating the notes/help field at the bottom of the panel
        JTabbedPane notesHelpPane = new JTabbedPane();
        notesHelpPane.setPreferredSize(new Dimension(-1, elementHeight*3));

        String help = activeModule.getHelp();
        JTextArea helpArea = new JTextArea(help);
        helpArea.setEditable(false);
        notesHelpPane.addTab("Help", null, helpArea);

        String notes = activeModule.getNotes();
        NotesArea notesArea = new NotesArea(this,notes);
        notesHelpPane.addTab("Notes", null, notesArea);

        c.anchor = GridBagConstraints.LAST_LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weighty = 1;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        paramsPanel.add(notesHelpPane,c);

        paramsPanel.validate();
        paramsPanel.repaint();

        paramsScrollPane.validate();
        paramsScrollPane.repaint();

    }

    void populateBasicModules() {
        basicModulesPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;

        // Adding module buttons
        for (HCModule module : modules) {
            Color color;
            int idx = modules.indexOf(module);
            if (!module.isEnabled()) {
                color = Color.LIGHT_GRAY;
            } else {
                if (idx <= lastModuleEval) {
                    color = Color.getHSBColor(0.27f,1f,0.6f);
                } else {
                    color = Color.getHSBColor(0f,1f,0.6f);
                }
            }

            if (idx == modules.size()-1) c.weighty = 1;

            // Only show if the module is enabled
            if (!module.isEnabled()) continue;

            // Only displaying the module title if it has at least one visible parameter
            boolean hasVisibleParameters = false;
            for (Parameter parameter:module.getActiveParameters().values()) {
                if (parameter.isVisible()) hasVisibleParameters = true;
            }
            if (!hasVisibleParameters) continue;

            JPanel titlePanel = componentFactory.createBasicModuleHeading(module,color,500-50);

            c.gridy++;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            basicModulesPanel.add(titlePanel,c);

            for (Parameter parameter:module.getActiveParameters().values()) {
                if (parameter.isVisible()) {
                    JPanel paramPanel = componentFactory.createParameterControl(parameter, modules, module, 500-80);

                    c.gridy++;
                    c.anchor = GridBagConstraints.FIRST_LINE_END;
                    basicModulesPanel.add(paramPanel, c);

                }
            }

            c.gridy++;
            JSeparator separator = new JSeparator();
            separator.setPreferredSize(new Dimension(0,15));
            basicModulesPanel.add(separator,c);

        }

        c.gridy++;
        c.weighty = 1;
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(0,0));
        basicModulesPanel.add(separator,c);

        basicModulesPanel.validate();
        basicModulesPanel.repaint();
        basicModulesScrollPane.validate();
        basicModulesScrollPane.repaint();

    }

    private void listAvailableModules() throws IllegalAccessException, InstantiationException {
        // Using Reflections tool to get list of classes extending HCModule
        Reflections.log = null;
        Reflections reflections = new Reflections("wbif.sjx.ModularImageAnalysis");
        Set<Class<? extends HCModule>> availableModules = reflections.getSubTypesOf(HCModule.class);

        // Creating new instances of these classes and adding to ArrayList
        TreeMap<String,ArrayList<HCModule>> availableModulesList = new TreeMap<>();
        for (Class clazz : availableModules) {
            String[] names = clazz.getPackage().getName().split("\\.");
            String pkg = names[names.length-1];

            availableModulesList.putIfAbsent(pkg,new ArrayList<>());
            availableModulesList.get(pkg).add((HCModule) clazz.newInstance());

        }

        // Sorting the ArrayList based on module title
        for (ArrayList<HCModule> modules:availableModulesList.values()) {
            Collections.sort(modules, Comparator.comparing(HCModule::getTitle));
        }

        // Adding the modules to the list
        for (String pkgName:availableModulesList.keySet()) {
            ArrayList<HCModule> modules = availableModulesList.get(pkgName);
            ModuleListMenu packageMenu = new ModuleListMenu(this,pkgName,modules);

            moduleListMenu.add(packageMenu);

        }
    }

    void addModule() {
        moduleListMenu.setLocation(MouseInfo.getPointerInfo().getLocation());
        moduleListMenu.setVisible(true);

    }

    void removeModule() {
        if (activeModule != null) {
            // Removing a module resets all the current evaluation
            int idx = modules.indexOf(activeModule);
            if (idx < lastModuleEval) lastModuleEval = -1;

            modules.remove(activeModule);
            activeModule = null;

            populateModuleList();
            populateModuleParameters();

        }
    }

    void moveModuleUp() {
        if (activeModule != null) {
            int idx = modules.indexOf(activeModule);
            if (idx != 0) {
                if (idx-1 <= lastModuleEval) lastModuleEval = idx-2;

                modules.remove(activeModule);
                modules.add(idx - 1, activeModule);
                populateModuleList();

            }
        }
    }

    void moveModuleDown() {
        if (activeModule != null) {
            int idx = modules.indexOf(activeModule);
            if (idx != modules.size()) {
                if (idx <= lastModuleEval) lastModuleEval = idx-1;

                modules.remove(activeModule);
                modules.add(idx + 1, activeModule);
                populateModuleList();
            }
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    HCModule getActiveModule() {
        return activeModule;
    }

    GUIAnalysis getAnalysis() {
        return analysis;
    }

    void setActiveModule(HCModule activeModule) {
        this.activeModule = activeModule;
    }

    boolean isBasicGUI() {
        return basicGUI;
    }

    ModuleCollection getModules() {
        return modules;
    }

    void setModules(ModuleCollection modules) {
        this.modules = modules;
    }

    int getLastModuleEval() {
        return lastModuleEval;
    }

    void setLastModuleEval(int lastModuleEval) {
        this.lastModuleEval = lastModuleEval;
    }

    Workspace getTestWorkspace() {
        return testWorkspace;
    }

    void setTestWorkspace(Workspace testWorkspace) {
        this.testWorkspace = testWorkspace;
    }

    JPopupMenu getModuleListMenu() {
        return moduleListMenu;
    }

    void evaluateModule(HCModule module) throws GenericMIAException {
        module.execute(testWorkspace,true);
        lastModuleEval = modules.indexOf(module);

        if (basicGUI) {
            populateBasicModules();

        } else {
            populateModuleList();

        }
    }

}

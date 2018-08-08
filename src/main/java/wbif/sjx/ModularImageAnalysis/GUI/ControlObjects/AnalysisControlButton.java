package wbif.sjx.ModularImageAnalysis.GUI.ControlObjects;

import ij.IJ;
import org.xml.sax.SAXException;
import wbif.sjx.ModularImageAnalysis.Exceptions.GenericMIAException;
import wbif.sjx.ModularImageAnalysis.GUI.Layouts.GUI;
import wbif.sjx.ModularImageAnalysis.Process.Analysis;
import wbif.sjx.ModularImageAnalysis.Process.AnalysisReader;
import wbif.sjx.ModularImageAnalysis.Process.AnalysisRunner;
import wbif.sjx.ModularImageAnalysis.Process.AnalysisWriter;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by steph on 28/07/2017.
 */
public class AnalysisControlButton extends JButton implements ActionListener {
    public static final String LOAD_ANALYSIS = "Load";
    public static final String SAVE_ANALYSIS = "Save";
    public static final String START_ANALYSIS = "Run";
    public static final String STOP_ANALYSIS = "Stop";


    public AnalysisControlButton(String command, int buttonSize) {
        addActionListener(this);
        setFocusPainted(false);
        setMargin(new Insets(0,0,0,0));
        setText(command);
        setPreferredSize(new Dimension(buttonSize, buttonSize));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            switch (getText()) {
                case LOAD_ANALYSIS:
                    Analysis newAnalysis = AnalysisReader.loadAnalysis();
                    if (newAnalysis == null) return;
                    GUI.setAnalysis(newAnalysis);

                    if (GUI.isBasicGUI()) {
                        GUI.populateBasicModules();
                    } else {
                        GUI.populateModuleList();
                        GUI.populateModuleParameters();
                    }

                    GUI.setLastModuleEval(-1);
                    GUI.updateTestFile();
                    GUI.populateModuleList();

                    break;

                case SAVE_ANALYSIS:
                    AnalysisWriter.saveAnalysis(GUI.getAnalysis());
                    break;

                case START_ANALYSIS:
                    Thread t = new Thread(() -> {
                        try {
                            AnalysisRunner.startAnalysis(GUI.getAnalysis());
                        } catch (IOException | InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (GenericMIAException e1) {
                            IJ.showMessage(e1.getMessage());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                    t.start();
                    break;

                case STOP_ANALYSIS:
                    System.out.println("Shutting system down");
                    AnalysisRunner.stopAnalysis();
                    break;
            }

        } catch (IOException | ClassNotFoundException | ParserConfigurationException | SAXException
                | IllegalAccessException | InstantiationException | TransformerException e1) {
            e1.printStackTrace();
        }
    }
}

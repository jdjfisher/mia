package wbif.sjx.ModularImageAnalysis.GUI.ControlObjects;

import wbif.sjx.ModularImageAnalysis.GUI.Layouts.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by steph on 28/07/2017.
 */
public class ModuleControlButton extends JButton implements ActionListener {
    public static final String ADD_MODULE = "+";
    public static final String REMOVE_MODULE = "-";
    public static final String MOVE_MODULE_UP = "▲";
    public static final String MOVE_MODULE_DOWN = "▼";

    private MainGUI gui;
    private static int buttonSize = 50;

    public ModuleControlButton(MainGUI gui, String command) {
        this.gui = gui;

        setText(command);
        addActionListener(this);
        setMargin(new Insets(0,0,0,0));
        setFocusPainted(false);
        setMargin(new Insets(0,0,0,0));
        setPreferredSize(new Dimension(buttonSize, buttonSize));

    }

    public static int getButtonSize() {
        return buttonSize;
    }

    public static void setButtonSize(int buttonSize) {
        ModuleControlButton.buttonSize = buttonSize;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (getText()) {
            case ADD_MODULE:
                gui.addModule();
                break;

            case REMOVE_MODULE:
                gui.removeModule();
                break;

            case MOVE_MODULE_UP:
                gui.moveModuleUp();
                break;

            case MOVE_MODULE_DOWN:
                gui.moveModuleDown();
                break;

        }
    }
}
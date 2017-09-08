package it.sijmen.cluino.runconfigurations.upload;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static it.sijmen.cluino.Util.getSerailPorts;

public class UploadSettingsEditor extends SettingsEditor<UploadRunConfiguration> {

    private final TextFieldWithBrowseButton hexFile, configFile;
    private final JBCheckBox verbose, disableAutoErase;
    private final ComboBox<String> port;
    private final JBTextField programmerType, baudRate, avrDevice;

    UploadSettingsEditor(Project project) {
        hexFile = new TextFieldWithBrowseButton();
        hexFile.addBrowseFolderListener(
                "Hex File", "Select the hex file of the program you want to upload.",
                project, new FileChooserDescriptor(true, false, false, false, false, false));

        configFile = new TextFieldWithBrowseButton();
        configFile.addBrowseFolderListener(
                "AVRDude Config File", "Configuration file for avrdude",
                project, new FileChooserDescriptor(true, false, false, false, false, false));

        verbose = new JBCheckBox("Verbose output", true);
        disableAutoErase = new JBCheckBox("Disable auto-erase", true);
        port = new ComboBox<>(getSerailPorts());
        programmerType = new JBTextField();
        baudRate = new JBTextField();
        avrDevice = new JBTextField();
    }

    @Override
    protected void resetEditorFrom(@NotNull UploadRunConfiguration rc) {
        hexFile.setText(rc.getHexFile());
        configFile.setText(rc.getConfigFile());

        verbose.setSelected(rc.isVerbose());
        disableAutoErase.setSelected(rc.isDisableAutoErase());

        port.setSelectedItem(rc.getPort());
        programmerType.setText(rc.getProgrammerType());
        baudRate.setText(rc.getBaudRate());
        avrDevice.setText(rc.getAvrDevice());
    }

    @Override
    protected void applyEditorTo(@NotNull UploadRunConfiguration rc)
            throws ConfigurationException {
        rc.setHexFile(hexFile.getText());
        rc.setConfigFile(configFile.getText());

        rc.setVerbose(verbose.isSelected());
        rc.setDisableAutoErase(disableAutoErase.isSelected());

        rc.setPort((String) port.getSelectedItem());
        rc.setProgrammerType(programmerType.getText());
        rc.setBaudRate(baudRate.getText());
        rc.setAvrDevice(avrDevice.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return FormBuilder.createFormBuilder()

                .addLabeledComponent("&Hex file:", hexFile)
                .addTooltip("This is the hex file that is uploaded to the arduino. This file mostly has a .hex extension.")

                .addLabeledComponent("Serail device", port)

                .addSeparator()
                    .addLabeledComponent("&avrdude config file:", configFile)
                    .addTooltip("<html>The configuration file for avrdude. This file is provided by the arduino ide.<br>" +
                            "Often this file can be found in ~/.arduino15/packages/arduino/tools/avrdude/VERSION-arduino/etc</html>")


                    .addLabeledComponent("Programmer type", programmerType)
                    .addLabeledComponent("AVR Device", avrDevice)
                    .addLabeledComponent("baud rate (RS-232)", baudRate)
                    .addLabeledComponent("Disable auto-erase", disableAutoErase)
                    .addLabeledComponent("Verbose output", verbose)
                .getPanel();
    }

}

package it.sijmen.cluino.runconfigurations.compile;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import it.sijmen.cluino.ext.ArduinoBuilder;
import it.sijmen.cluino.ui.FileListPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.intellij.ide.IdeTooltipManager.setBorder;

public class CompileSettingsEditor extends SettingsEditor<CompileRunConfiguration> {

    private final TextFieldWithBrowseButton mainFile;

    private final JBTextField fqbn, ideVersion;
    private final JBIntSpinner debugLevel;
    private final ComboBox<ArduinoBuilder.Logger> logger;
    private final ComboBox<ArduinoBuilder.Warnings> warnings;
    private final JBCheckBox verbose;
    private final FileListPanel hardwares;
    private final FileListPanel tools;
    private final FileListPanel prefs;
    private final FileListPanel libraries;

    public CompileSettingsEditor(Project project) {
        mainFile = new TextFieldWithBrowseButton();
        mainFile.addBrowseFolderListener(
                "Primary Source File", "Select the main source file of the program you are compiling. Also known as the entry point.",
                project, new FileChooserDescriptor(true, false, false, false, false, false));

        logger = new ComboBox<>(ArduinoBuilder.Logger.values());
        fqbn = new JBTextField();
        ideVersion = new JBTextField();
        warnings = new ComboBox<>(ArduinoBuilder.Warnings.values());
        debugLevel = new JBIntSpinner(5, 0, 10);

        verbose = new JBCheckBox();

        hardwares = new FileListPanel("Hardwares", "No hardware folders selected.", project);
        tools = new FileListPanel("Tools", "No hardware folders selected.", project);
        prefs = new FileListPanel("Preferences", "No preferences overwrites folders selected.", project);
        libraries = new FileListPanel("Libraries", "No extra library folders selected.", project);
    }

    @Override
    protected void resetEditorFrom(@NotNull CompileRunConfiguration rc) {
        mainFile.setText(rc.getMainFile());

        logger.setSelectedItem(rc.getLogger());
        fqbn.setText(rc.getFqbn());
        ideVersion.setText(rc.getIdeVersion());
        warnings.setSelectedItem(rc.getWarnings());
        debugLevel.setNumber(rc.getDebugLevel());

        verbose.setSelected(rc.isVerbose());

        hardwares.setItems(rc.getHardwares());
        tools.setItems(rc.getTools());
        prefs.setItems(rc.getPrefs());
        libraries.setItems(rc.getLibraries());
    }

    @Override
    protected void applyEditorTo(@NotNull CompileRunConfiguration rc)
            throws ConfigurationException {
        rc.setMainFile(mainFile.getText());

        rc.setLogger((ArduinoBuilder.Logger) logger.getSelectedItem());
        rc.setFqbn(fqbn.getText());
        rc.setIdeVersion(ideVersion.getText());
        rc.setWarnings((ArduinoBuilder.Warnings) warnings.getSelectedItem());
        rc.setDebugLevel(debugLevel.getNumber());

        rc.setVerbose(verbose.isSelected());

        rc.setHardwares(hardwares.getItems());
        rc.setTools(tools.getItems());
        rc.setPrefs(prefs.getItems());
        rc.setLibraries(libraries.getItems());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("&Main file:", mainFile)
                .addComponent(libraries.getPanel())
                .addTooltip("Select folder(s) container arduino libraries.")
                .addSeparator()
                .addLabeledComponent("Board Code:", fqbn)
                .addTooltip("Fully Qualified Board Name, e.g.: arduino:avr:uno")
                .addLabeledComponent("Arduino IDE version:", ideVersion)
                .addTooltip("The version of the installed ide. Leave empty if unsure")
                .addLabeledComponent("Warnings:", warnings)
                .addTooltip("Used to tell gcc which warning level to use (-W flag).")
                .addLabeledComponent("Debug level:", debugLevel)
                .addTooltip("Used for debugging. Set it to 10 when submitting an issue.")
                .addLabeledComponent("Logging type:", logger)
                .addLabeledComponent("Verbose output:", verbose)
                .addSeparator()
                .addLabeledComponent("Hardwares:", hardwares.getPanel())
                .addTooltip("<html>Folder(s) containing Arduino platforms. An example is the hardware folder shipped with the Arduino IDE, or the packages <br>" +
                        "folder created by Arduino Boards Manager. Can be pecified multiple times. If conflicting hardware definitions are specified, the last one wins.</html>")
                .addLabeledComponent("Tools:", tools.getPanel())
                .addTooltip("<html>Folder(s) containing Arduino tools (gcc, avrdude...). An example is the hardware/tools folder shipped with the Arduino IDE, <br>" +
                        "or the packages folder created by Arduino Boards Manager.</html>")
                .addLabeledComponent("Extra preferences:", prefs.getPanel())
                .addTooltip("Optional. It allows to override some build properties. Use the 'arduino-builder -dump-prefs' command to list some available properties.")
                .getPanel();
        JBScrollPane pane = new JBScrollPane(panel);
        pane.setBorder(null);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        return pane;
    }

}

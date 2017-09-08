package it.sijmen.cluino.runconfigurations.compile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import it.sijmen.cluino.ext.ArduinoBuilder;
import it.sijmen.cluino.runconfigurations.GenericRunnerState;
import org.jdom.Content;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static it.sijmen.cluino.Util.required;

public class CompileRunConfiguration extends RunConfigurationBase {

    private String mainFile;

    private ArduinoBuilder.Logger logger;
    private ArduinoBuilder.Warnings warnings;

    private String fqbn, ideVersion;
    private int debugLevel;
    private boolean verbose;
    private List<String> hardwares, tools, prefs, libraries;

    public CompileRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);

        logger = ArduinoBuilder.Logger.HUMAN;
        warnings = ArduinoBuilder.Warnings.NONE;
        fqbn = "arduino:avr:uno";
        ideVersion = ""; //todo: auto-detect?
        debugLevel = 5;
        verbose = true;
        hardwares = ArduinoBuilder.getDefaultHardwares();
        tools = ArduinoBuilder.getDefaultTools();

        prefs = new ArrayList<>();
        libraries = new ArrayList<>();
    }

    @NotNull
    @Override
    public SettingsEditor<? extends CompileRunConfiguration> getConfigurationEditor() {
        return new CompileSettingsEditor(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        required("main file", mainFile);
        required("logger", logger != null);
        required("fqbn", fqbn);
        required("warnings", warnings != null);
        required("debug level", debugLevel >= 0 && debugLevel <= 10);
        required("hardware list", hardwares != null && !hardwares.isEmpty());
        required("tools list", tools != null && !tools.isEmpty());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment)
            throws ExecutionException {
        return new GenericRunnerState(getProject(), executionEnvironment, this::run);
    }

    @Override
    public void readExternal(Element e) throws InvalidDataException {
        super.readExternal(e);
        setMainFile(e.getAttributeValue("mainfile"));
        
        setLogger(ArduinoBuilder.Logger.valueOf(e.getAttributeValue("logger")));
        setFqbn(e.getAttributeValue("fqbn"));
        setIdeVersion(e.getAttributeValue("ideversion"));
        setWarnings(ArduinoBuilder.Warnings.valueOf(e.getAttributeValue("warnings")));
        
        setDebugLevel(Integer.parseInt(e.getAttributeValue("debuglevel")));
        setVerbose("true".equals(e.getAttributeValue("verbose")));

        setHardwares(getSubitems(e.getChild("hardwares")));
        setTools(getSubitems(e.getChild("tools")));
        setPrefs(getSubitems(e.getChild("prefs")));
        setLibraries(getSubitems(e.getChild("libs")));
    }

    public static List<String> getSubitems(Element e){
        if(e == null)
            return new ArrayList<>();
        return e.getContent().stream().map(Content::getValue).collect(Collectors.toList());
    }

    public static Element makeElement(String key, List<String> elements){
        return new Element(key+"s").addContent(
                elements.stream().map(c -> new Element(key).setText(c)).collect(Collectors.toList())
        );
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        element.setAttribute("mainfile", getMainFile());
        
        element.setAttribute("logger", getLogger().toString());
        element.setAttribute("fqbn", getFqbn());
        element.setAttribute("ideversion", getIdeVersion());
        element.setAttribute("warnings", getWarnings().toString());
        
        element.setAttribute("debuglevel", String.valueOf(getDebugLevel()));
        element.setAttribute("verbose", isVerbose() ? "true" : "false");

        element.addContent(makeElement("hardware", getHardwares()));
        element.addContent(makeElement("tool", getTools()));
        element.addContent(makeElement("pref", getPrefs()));
        element.addContent(makeElement("lib", getLibraries()));
    }
    
    private GeneralCommandLine run() throws ExecutionException {
        ArduinoBuilder arduinoBuilder = new ArduinoBuilder(
                getLogger(),
                getFqbn(),
                getIdeVersion(),
                getWarnings(),
                isVerbose(),
                getDebugLevel(),
                getHardwares(),
                getTools(),
                getPrefs(),
                getLibraries()
        );

        try {
            arduinoBuilder.prepareDirs(this, getProject());
            return new GeneralCommandLine(arduinoBuilder.getCompileCommand(getMainFile(), this.getProject()));
        } catch (IOException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/arduino-compile.png");
    }

    void setMainFile(String mainFile) {
        this.mainFile = mainFile;
    }

    String getMainFile() {
        return mainFile == null ? "" : mainFile;
    }
    
    ArduinoBuilder.Logger getLogger() {
        return logger;
    }

    void setLogger(ArduinoBuilder.Logger logger) {
        this.logger = logger;
    }

    String getFqbn() {
        return fqbn == null ? "" : fqbn;
    }

    void setFqbn(String fqbn) {
        this.fqbn = fqbn;
    }

    String getIdeVersion() {
        return ideVersion == null ? "" : ideVersion;
    }

    void setIdeVersion(String ideVersion) {
        this.ideVersion = ideVersion;
    }

    ArduinoBuilder.Warnings getWarnings() {
        return warnings;
    }

    void setWarnings(ArduinoBuilder.Warnings warnings) {
        this.warnings = warnings;
    }

    int getDebugLevel() {
        return debugLevel;
    }

    void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    boolean isVerbose() {
        return verbose;
    }

    void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    List<String> getHardwares() {
        return hardwares;
    }

    void setHardwares(List<String> hardwares) {
        this.hardwares = hardwares;
    }

    List<String> getTools() {
        return tools;
    }

    void setTools(List<String> tools) {
        this.tools = tools;
    }

    List<String> getPrefs() {
        return prefs;
    }

    void setPrefs(List<String> prefs) {
        this.prefs = prefs;
    }

    List<String> getLibraries() {
        return libraries;
    }

    void setLibraries(List<String> libraries) {
        this.libraries = libraries;
    }
}

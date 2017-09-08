package it.sijmen.cluino.runconfigurations.upload;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import it.sijmen.cluino.ext.AvrDude;
import it.sijmen.cluino.runconfigurations.GenericRunnerState;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

import static it.sijmen.cluino.Util.getFileOrFail;
import static it.sijmen.cluino.Util.required;

public class UploadRunConfiguration extends RunConfigurationBase {

    private String hexFile;
    private String configFile;
    private String avrDevice;
    private String programmerType;
    private String port;
    private String baudRate;
    private boolean verbose;
    private boolean disableAutoErase;

    public UploadRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);

        this.configFile = AvrDude.guessConfigPath();
        this.avrDevice = "atmega328p";
        this.programmerType = "arduino";
        this.baudRate = "115200";
        this.verbose = false;
        this.disableAutoErase = true;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends UploadRunConfiguration> getConfigurationEditor() {
        return new UploadSettingsEditor(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        //todo: check if files exist
        required("hex file", hexFile);
        required("config file", configFile);
        required("avr device", avrDevice);
        required("programmer type", programmerType);
        required("port", port);
        required("baud rate", baudRate);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment)
            throws ExecutionException {
        return new GenericRunnerState(getProject(), executionEnvironment, this::run);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        setHexFile(element.getAttributeValue("hexfile"));
        setConfigFile(element.getAttributeValue("configfile"));
        setAvrDevice(element.getAttributeValue("avrdevice"));
        setProgrammerType(element.getAttributeValue("programmertype"));
        setPort(element.getAttributeValue("port"));
        setBaudRate(element.getAttributeValue("baudrate"));
        setVerbose("true".equals(element.getAttributeValue("verbose")));
        setDisableAutoErase("true".equals(element.getAttributeValue("disableAutoErase")));
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        element.setAttribute("hexfile", getHexFile());
        element.setAttribute("configfile", getConfigFile());
        element.setAttribute("avrdevice", getAvrDevice());
        element.setAttribute("programmertype", getProgrammerType());
        element.setAttribute("port", getPort());
        element.setAttribute("baudrate", getBaudRate());
        element.setAttribute("verbose", isVerbose() ? "true": "false");
        element.setAttribute("disableAutoErase", isDisableAutoErase() ? "true": "false");
    }

    private GeneralCommandLine run() throws ExecutionException {
        VirtualFile hexFile;

        try {
            hexFile = getFileOrFail(getHexFile());

            AvrDude dude = new AvrDude(
                    getConfigFile(),
                    isVerbose(),
                    getAvrDevice(),
                    getProgrammerType(),
                    getPort(),
                    getBaudRate(),
                    isDisableAutoErase()
            );

            return new GeneralCommandLine(
                    dude.getCompileCommand(hexFile.getPath())
            );
        } catch (IOException e) {
            throw new ExecutionException(e.getMessage());
        }
    }


    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/arduino-upload.png");
    }

    void setHexFile(String hexFile) {
        this.hexFile = hexFile;
    }

    String getHexFile() {
        return hexFile == null ? "" : hexFile;
    }

    public String getConfigFile() {
        return configFile == null ? "" : configFile;
    }

    public UploadRunConfiguration setConfigFile(String configFile) {
        this.configFile = configFile;
        return this;
    }

    public String getAvrDevice() {
        return avrDevice == null ? "" : avrDevice;
    }

    public UploadRunConfiguration setAvrDevice(String avrDevice) {
        this.avrDevice = avrDevice;
        return this;
    }

    public String getProgrammerType() {
        return programmerType == null ? "" : programmerType;
    }

    public UploadRunConfiguration setProgrammerType(String programmerType) {
        this.programmerType = programmerType;
        return this;
    }

    public String getPort() {
        return port == null ? "" : port;
    }

    public UploadRunConfiguration setPort(String port) {
        this.port = port;
        return this;
    }

    public String getBaudRate() {
        return baudRate == null ? "" : baudRate;
    }

    public UploadRunConfiguration setBaudRate(String baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public UploadRunConfiguration setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public boolean isDisableAutoErase() {
        return disableAutoErase;
    }

    public UploadRunConfiguration setDisableAutoErase(boolean disableAutoErase) {
        this.disableAutoErase = disableAutoErase;
        return this;
    }
}

package it.sijmen.cluino.runconfigurations;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import it.sijmen.cluino.runconfigurations.compile.CompileRunConfiguration;
import it.sijmen.cluino.runconfigurations.upload.UploadRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ArduinoRunConfigurationType implements ConfigurationType {

    @Override
    public String getDisplayName() {
        return "Arduino";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Compile and upload to the arduino";
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/arduino.png");
    }

    @NotNull
    @Override
    public String getId() {
        return "arduino";
    }

    @Override
    public com.intellij.execution.configurations.ConfigurationFactory[] getConfigurationFactories() {
        return new com.intellij.execution.configurations.ConfigurationFactory[]{
                new ConfigurationFactory(this) {

                    @Override
                    @NotNull
                    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                        return new CompileRunConfiguration(project, this, "Compile c++/ino to Arduino HEX code");
                    }

                    @Override
                    public String getName() {
                        return "Compile";
                    }

                    @Override
                    public Icon getIcon() {
                        return IconLoader.getIcon("/icons/arduino-compile.png");
                    }
                },
                new ConfigurationFactory(this) {

                    @Override
                    @NotNull
                    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                        return new UploadRunConfiguration(project, this, "Upload Arduino HEX code to device");
                    }

                    @Override
                    public String getName() {
                        return "Upload";
                    }

                    @Override
                    public Icon getIcon() {
                        return IconLoader.getIcon("/icons/arduino-upload.png");
                    }
                }
        };
    }
}

package it.sijmen.cluino.runconfigurations;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class GenericRunnerState extends CommandLineState {

    private final Project project;
    private final GetCmd getCmd;

    public GenericRunnerState(@NotNull Project project, @NotNull ExecutionEnvironment executionEnvironment, @NotNull GetCmd getCmd) {
        super(executionEnvironment);
        this.project = project;
        this.getCmd = getCmd;
    }

    @FunctionalInterface
    public interface GetCmd {
        GeneralCommandLine getCmd() throws ExecutionException;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine cmd = getCmd.getCmd();

        OSProcessHandler handler = new OSProcessHandler(cmd);
        ProcessTerminatedListener.attach(handler, this.project);

        ConsoleView console = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        console.print(cmd.getCommandLineString(), ConsoleViewContentType.SYSTEM_OUTPUT);

        console.attachToProcess(handler);

        return handler;
    }

}

package it.sijmen.cluino.ext;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.sijmen.cluino.Util.*;

public class ArduinoBuilder {

    private static final String OUT_BUILD = "arduino-build", OUT_CACHE = "arduino-cache", OUT = "out";

    /**
     *  Can be "human", "humantags" or "machine". Defaults to "human".
     *  If "humantags" the messages are qualified with a prefix that indicates
     *  their level (info, debug, error). If "machine", messages emitted will be
     *  in a format which the Arduino IDE understands and that it uses for I18N.
     */
    private Logger logger;

    /**
     * Fully Qualified Board Name, e.g.: arduino:avr:uno
     */
    private String fqbn;

    /**
     * The version of the Arduino IDE which is using this tool.
     */
    private String ideVersion;

    /**
     * Can be "none", "default", "more" and "all".
     * Defaults to "none". Used to tell gcc which warning
     * level to use (-W flag).
     */
    private Warnings warnings;

    /**
     *  Optional, turns on verbose mode.
     */
    private boolean verbose;

    /**
     * Optional, defaults to "5". Used for debugging. Set it to 10 when submitting an issue.
     */
    private int debugLevel;

    /**
     * Mandatory. Folder containing Arduino platforms.
     * An example is the hardware folder shipped with the Arduino IDE,
     * or the packages folder created by Arduino Boards Manager. Can be
     * specified multiple times. If conflicting hardware definitions are
     * specified, the last one wins.
     */
    private List<String> hardwares;

    /**
     * Mandatory. Folder containing Arduino tools (gcc, avrdude...).
     * An example is the hardware/tools folder shipped with the Arduino
     * IDE, or the packages folder created by Arduino Boards Manager.
     * Can be specified multiple times.
     */
    private List<String> tools;

    /**
     * Optional. It allows to override some build properties.
     */
    private List<String> prefs;

    /**
     * Optional. Folder containing Arduino libraries.
     * An example is the libraries folder shipped with the Arduino IDE.
     * Can be specified multiple times.
     */
    private List<String> libraries;

    public ArduinoBuilder(Logger logger, String fqbn, String ideVersion, Warnings warnings, boolean verbose,
                          int debugLevel, List<String> hardwares, List<String> tools, List<String> prefs, List<String> libraries) {
        this.logger = logger;
        this.fqbn = fqbn;
        this.ideVersion = ideVersion;
        this.warnings = warnings;
        this.verbose = verbose;
        this.debugLevel = debugLevel;
        this.hardwares = hardwares;
        this.tools = tools;
        this.prefs = prefs;
        this.libraries = libraries;
    }

    public List<String> getCompileCommand(String file, Project project) throws IOException {
        List<String> out = new ArrayList<>();
        VirtualFile outPath = getOrFail(project.getBaseDir(), "out");

        out.add("arduino-builder");
        out.add("-compile");

        for(String tool : tools)
            out.add("-tools="+getFileOrFail(tool).getPath());

        for(String hardware : hardwares)
            out.add("-hardware="+getFileOrFail(hardware).getPath());

        for(String lib : libraries)
            out.add("-libraries="+getFileOrFail(lib).getPath());

        out.add("-logger=" + logger.toString().toLowerCase());
        out.add("-fqbn="+fqbn);
        if(ideVersion != null && !ideVersion.isEmpty())
            out.add("-ide-version="+ideVersion);
        out.add("-warnings="+warnings.toString().toLowerCase());
        out.add("-debug-level="+debugLevel);

        out.add("-build-path=" + getOrFail(outPath, OUT_BUILD).getPath());
        out.add("-build-cache=" + getOrFail(outPath, OUT_CACHE).getPath());

        prefs.forEach(p -> out.add("-prefs=" + p));

        if(verbose)
            out.add("-verbose");

        out.add(file);

        return out;
    }

    public void prepareDirs(Object requestor, Project project) throws IOException {
        VirtualFile out = getOrCreateDir(requestor, project.getBaseDir(), OUT);

        getOrCreateDir(requestor, out, OUT_BUILD);
        getOrCreateDir(requestor, out, OUT_CACHE);
    }

    public enum Logger { HUMAN, HUMANTAGS, MACHINE;}

    public enum Warnings { NONE, DEFAUlT, MORE, ALL;}

    public static List<String> getDefaultPrefs() {
        List<String> out = new ArrayList<>();

        out.add("build.warn_data_percentage=75");
        Path userdir = getUserHomeArduinoPackages().resolve("arduino/tools");
        if(Files.exists(userdir)) {
            String p = userdir.toAbsolutePath().toString();
            out.add("runtime.tools.arduinoOTA.path=" + p + "/arduinoOTA/1.1.1");
            out.add("runtime.tools.avrdude.path=" + p + "/avrdude/6.3.0-arduino9");
            out.add("runtime.tools.avr-gcc.path=" + p + "/avr-gcc/4.9.2-atmel3.5.4-arduino2");
        }
        return out;
    }

    public static List<String> getDefaultHardwares(){
        List<String> out = new ArrayList<>();
        if(!isWindows())
            out.add("/usr/share/arduino/hardware");
        Path userdir = getUserHomeArduinoPackages();
        if(Files.exists(userdir))
            out.add(userdir.toAbsolutePath().toString());
        return out;
    }

    public static List<String> getDefaultTools(){
        List<String> out = new ArrayList<>();
        if(!isWindows())
            out.add("/usr/share/arduino/tools-builder");
        Path userdir = getUserHomeArduinoPackages();
        if(Files.exists(userdir))
            out.add(userdir.toAbsolutePath().toString());
        return out;
    }

    private static Path getUserHomeArduinoPackages(){
        return Paths.get(System.getProperty("user.home")).resolve(".arduino15/packages");
    }

    public ArduinoBuilder setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public ArduinoBuilder setFqbn(String fqbn) {
        this.fqbn = fqbn;
        return this;
    }

    public ArduinoBuilder setIdeVersion(String ideVersion) {
        this.ideVersion = ideVersion;
        return this;
    }

    public ArduinoBuilder setWarnings(Warnings warnings) {
        this.warnings = warnings;
        return this;
    }

    public ArduinoBuilder setHardwares(List<String> hardware) {
        this.hardwares = hardware;
        return this;
    }

    public ArduinoBuilder setTools(List<String> tools) {
        this.tools = tools;
        return this;
    }

    public ArduinoBuilder setPrefs(List<String> prefs) {
        this.prefs = prefs;
        return this;
    }

    public ArduinoBuilder setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public ArduinoBuilder setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
        return this;
    }

    public ArduinoBuilder setLibraries(List<String> libraries) {
        this.libraries = libraries;
        return this;
    }
}

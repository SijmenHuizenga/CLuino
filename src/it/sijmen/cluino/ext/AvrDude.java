package it.sijmen.cluino.ext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static it.sijmen.cluino.Util.isWindows;

public class AvrDude {

    /**
     * Specify location of configuration file.
     */
    private String configFile;

    /**
     * Verbose output.
     */
    private boolean verbose;

    /**
     * Required. Specify AVR device.
     */
    private String avrDevice;

    /**
     * Specify programmer type.
     */
    private String programmerType;

    /**
     * Specify connection port.
     */
    private String port;

    /**
     * Override RS-232 baud rate.
     */
    private String baudRate;

    /**
     * Disable auto-erase
     */
    private boolean disableAutoErase;

    /**
     * Memory operation specification.
     * <memtype>:r|w|v:<filename>[:format]
     */
    private String memFormat = "Uflash:w:%s:i";

    public AvrDude(String configFile, boolean verbose, String avrDevice, String programmerType, String port,
                   String baudRate, boolean disableAutoErase) {
        this.configFile = configFile;
        this.verbose = verbose;
        this.avrDevice = avrDevice;
        this.programmerType = programmerType;
        this.port = port;
        this.baudRate = baudRate;
        this.disableAutoErase = disableAutoErase;
    }

    public List<String> getCompileCommand(String file) throws IOException {
        List<String> out = new ArrayList<>();

        out.add("avrdude");

        out.add("-C"+configFile);
        out.add("-c"+programmerType);
        out.add("-p"+avrDevice);
        out.add("-P"+port);
        out.add("-b"+baudRate);

        if(verbose) {
            out.add("-v");
            out.add("-v");
        }

        if(disableAutoErase)
            out.add("-D");

        out.add(String.format(memFormat, file));

        return out;
    }

    public static String guessConfigPath(){
        if(isWindows()){
            //todo
        }else{
            Path userdir = Paths.get(System.getProperty("user.home"));
            Path avrdude = userdir.resolve(".arduino15/packages/arduino/tools/avrdude/");
            Path avrdudeinstalldir = null;

            try {
                for (Path path : Files.newDirectoryStream(avrdude)) {
                    if (path.getFileName().toString().contains("-arduino")){
                        path = path.resolve("etc/avrdude.conf");
                        if(Files.exists(path)) {
                            avrdudeinstalldir = path;
                            break;
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(avrdudeinstalldir != null)
                return avrdudeinstalldir.toAbsolutePath().toString();
        }
        return "";
    }

    public AvrDude setConfigFile(String configFile) {
        this.configFile = configFile;
        return this;
    }

    public AvrDude setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public AvrDude setAvrDevice(String avrDevice) {
        this.avrDevice = avrDevice;
        return this;
    }

    public AvrDude setProgrammerType(String programmerType) {
        this.programmerType = programmerType;
        return this;
    }

    public AvrDude setPort(String port) {
        this.port = port;
        return this;
    }

    public AvrDude setBaudRate(String baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public AvrDude setDisableAutoErase(boolean disableAutoErase) {
        this.disableAutoErase = disableAutoErase;
        return this;
    }

    public AvrDude setMemFormat(String memFormat) {
        this.memFormat = memFormat;
        return this;
    }
}

package it.sijmen.cluino;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import purejavacomm.CommPortIdentifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Util {

    public static VirtualFile getOrCreateDir(Object requestor, VirtualFile base, String dirName) throws IOException {
        VirtualFile out = base.findChild(dirName);
        if(out == null || !out.exists())
            try {
                Application application = ApplicationManager.getApplication();
                out = application.runWriteAction((ThrowableComputable<VirtualFile, IOException>) ()->
                        base.createChildDirectory(requestor, dirName)
                );

            } catch (IOException e) {
                throw new IOException("Could not create "+dirName+" directory", e);
            }
        return out;
    }

    public static VirtualFile getOrFail(VirtualFile base, String dirName) throws IOException {
        VirtualFile out = base.findChild(dirName);
        if(out == null || !out.exists())
            throw new IOException("Could not find "+dirName+" directory");
        return out;
    }

    public static VirtualFile findHexFileFromCodeFile(Project project, VirtualFile codeFile) throws ExecutionException {
        VirtualFile out = project.getBaseDir().findChild("out");
        if(out == null)
            throw new ExecutionException("Could not find 'out' directory. Did you compile the selected source file?");
        out = out.findChild("arduino-build");
        if(out == null)
            throw new ExecutionException("Could not find 'out/arduino-build' directory. Did you compile the selected source file?");

        VirtualFile result = out.findChild(codeFile.getName() + ".hex");
        if(result == null)
            throw new ExecutionException("Could not find compiled ("+
                    out.getPath() + "/" + codeFile.getName() + ".hex"
                    +") hex file. Did you compile the selected source file?");
        return result;
    }

    public static VirtualFile getFileOrFail(String path) throws IOException {
        VirtualFile fileByPath = getFileOrNull(path);
        if(fileByPath == null)
            throw new IOException("Could not find file "+path+".");
        return fileByPath;
    }

    public static VirtualFile getFileOrNull(String path) {
        if(path == null)
            return null;
        VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(path);
        if(fileByPath == null || !fileByPath.exists())
            return null;
        return fileByPath;
    }


    public static String[] getSerailPorts(){
        List<String> out = new ArrayList<>();

        String prefix = "";
        if(!isWindows())
            prefix = "/dev/";

        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
            if(port.getPortType() == CommPortIdentifier.PORT_SERIAL)
                out.add(prefix+port.getName());
        }
        return out.toArray(new String[out.size()]);
    }

    public static void required(String description, String obj) throws RuntimeConfigurationException {
        if(obj == null || obj.isEmpty())
            throw new RuntimeConfigurationException("The "+description+" is not specified.");
    }

    public static void required(String description, boolean isOk) throws RuntimeConfigurationException {
        if(!isOk)
            throw new RuntimeConfigurationException("The "+description+" is not specified or invalid.");
    }

    public static boolean isWindows(){
        return System.getProperty("os.name").toLowerCase().startsWith("win");
    }

}

package com.transround.nativeradmin.util;

import com.intellij.openapi.ui.Messages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by szeibert on 2014.12.09..
 */
public class ProGuardUtils extends CommonUtils {

    private static List<File> proGuardFiles;

    public static List<File> getProGuardFiles() {
        if (proGuardFiles == null) {
            proGuardFiles = new ArrayList<File>();
            findProGuardFiles(new File(project.getBaseDir().getPath()));
        }
        return proGuardFiles;
    }

    private static void findProGuardFiles(File folder) {
        for (File f : folder.listFiles()) {
            if (f.getName().contains("proguard") && !f.getName().contains(".bak")) {
                proGuardFiles.add(f);
            }
            if (isNonBuildDirectory(f)) {
                findProGuardFiles(f);
            }
        }
    }

    public static void reset() {
        proGuardFiles = null;
    }

    public static void makeChangesToProGuardFiles() {
        for (File proGuardFile : getProGuardFiles()) {
            String proGuardFileContent = readContentFromFile(proGuardFile);
            StringBuilder modifiedProGuardFileContent = new StringBuilder(proGuardFileContent);
            String comment = "\n# Generated by Nativer Admin for Android Studio\n";
            if (!proGuardFileContent.contains("-dontwarn **")) {
                modifiedProGuardFileContent.append(comment);
                modifiedProGuardFileContent.append("-dontwarn **\n");
            }
            if (!proGuardFileContent.contains("-keep public class **.R")) {
                modifiedProGuardFileContent.append(comment);
                modifiedProGuardFileContent.append("-keep public class **.R\n");
            }
            if (!proGuardFileContent.contains("-keep public class **.R$**")) {
                modifiedProGuardFileContent.append(comment);
                modifiedProGuardFileContent.append("-keep public class **.R$**\n");
            }
            if (!proGuardFileContent.contains("-keepnames class **.R$**")) {
                modifiedProGuardFileContent.append(comment);
                modifiedProGuardFileContent.append("-keepnames class **.R$** {\n");
                modifiedProGuardFileContent.append("    *;\n");
                modifiedProGuardFileContent.append("}\n");
            }
            if (!proGuardFileContent.contains("-keepclassmembers class **.R$**")) {
                modifiedProGuardFileContent.append(comment);
                modifiedProGuardFileContent.append("-keepclassmembers class **.R$** {\n");
                modifiedProGuardFileContent.append("    *;\n");
                modifiedProGuardFileContent.append("}\n");
            }
            try {
                backupFile(proGuardFile);
                writeContentToFile(modifiedProGuardFileContent.toString(), proGuardFile);
            } catch (IOException e) {
                Messages.showErrorDialog(String.format("Error saving %1$s.", proGuardFile.getAbsolutePath()), "Error");
            }
        }
        project.getBaseDir().refresh(false, true);
    }
}
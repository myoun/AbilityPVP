package me.wincho.abilitypvp.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtils {
    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void deleteFolder(File folder) {
        try {
            if (folder.exists()) {
                File[] folder_list = folder.listFiles();

                for (int i = 0; i < folder_list.length; i++) {
                    if (folder_list[i].isFile()) {
                        folder_list[i].delete();
                    } else {
                        deleteFolder(folder_list[i]);
                    }
                    folder_list[i].delete();
                }
                folder.delete();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
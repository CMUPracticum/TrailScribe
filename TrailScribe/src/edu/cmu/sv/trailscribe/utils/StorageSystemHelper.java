package edu.cmu.sv.trailscribe.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.sv.trailscribe.view.TrailScribeApplication;

public class StorageSystemHelper {

    public static final String[] folders = {"maps", "tiles", "kmls"};
    
    public static void createFolder() {
        for (String folder : folders) {
            File directory = new File(TrailScribeApplication.STORAGE_PATH + folder);
            if (!directory.exists()) {
                directory.mkdir();
            }
        }
    }
    
    /**
     * @param directory For example, "/sdcard/trailscribe/maps/"
     * @return list of every file under that directory, recursively
     */
    public static List<String> getFiles(String directory) {
        ArrayList<String> files = new ArrayList<String>();
        
        File folder = new File(directory);
        if (!folder.exists()) {
            return files;
        }
        
        File[] listOfFiles = folder.listFiles();
        
        for (File file : listOfFiles) {
            if (file.isFile()) {
                files.add(file.getName());
            } else if (file.isDirectory()) {
                List<String> fs = getFiles(directory + file.getName() + "/");
                for (String f : fs) {
                    files.add(file.getName() + "/" + f);
                }
            }
        }
        
        return files;
    }
    
    /**
     * @param directory For example, "/sdcard/trailscribe/maps/"
     * @return list of every folder under that directory
     */
    public static List<String> getFolders(String directory) {
        ArrayList<String> files = new ArrayList<String>();
        
        File folder = new File(directory);
        if (!folder.exists()) {
            return files;
        }
        
        File[] listOfFolders = folder.listFiles();
        
        for (File f : listOfFolders) {
            if (f.isDirectory()) {
                files.add(f.getName());
            }
        }
        
        return files;
    }    
}

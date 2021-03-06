/* 
 * Copyright (c) 2014, TrailScribe Team.
 * This content is released under the MIT License. See the file named LICENSE for details.
 */
package edu.cmu.sv.trailscribe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import edu.cmu.sv.trailscribe.model.data.Map;
import edu.cmu.sv.trailscribe.view.TrailScribeApplication;

public class StorageSystemHelper {
//  SD card folders the application needs, will be created in createFolder()
    public static final String[] folders = {"maps", "tiles", "kmls", "samples"};
    
    private static final String MSG_TAG = "StorageSystemHelper";
    
    public static void createDefaultFolders() {
        createFolder(Environment.getExternalStorageDirectory() + "/trailscribe");
        for (String folder : folders) {
            createFolder(TrailScribeApplication.STORAGE_PATH + folder);
        }
    }
    
    public static boolean verifyDirectory(String directory){
    	File file = new File(directory); 
		return file.isDirectory();
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
    
    /**
     * Copy assets files to device storage, recursively.
     * 
     * @param context
     * @param sourceDirectory directory of the assets to be copied.
     *          The files should under ~/TrailScribe/TrailScribe/assets/<sourceDirectory>
     * @param targetDirectory target directory of the asset files.
     *          For example, sdcard/trailscribe/samples
     */
    public static boolean copyAssetToDevice(
            Context context, String sourceDirectory, String targetDirectory) {
//      Remove the tailing '/' in targetDirectory
        if (targetDirectory.charAt(targetDirectory.length() - 1) == '/') {
            targetDirectory = targetDirectory.substring(0, targetDirectory.length() - 1);
        }
        
        String[] list = null;
        Resources resources = context.getResources();
        AssetManager assetManager = resources.getAssets();
        
        try {
            list = assetManager.list(sourceDirectory);
            if (list == null) return true;
        } catch (IOException e) {
            Log.e(MSG_TAG, "Failed to get asset file list: " + e.getMessage());
        }
        
        for (String file : list) {
            if (file.indexOf(".") >= 0) {
                copyFile(assetManager, sourceDirectory + "/" + file, targetDirectory + "/" + file);
            } else {
                createFolder(targetDirectory + "/" + file);
                copyAssetToDevice(context, sourceDirectory + "/" + file, targetDirectory + "/" + file);
            }
        }
        
        return true;
    }
    
    /**
     * @return path of every base map under /sdcard/trailscribe/maps/
     */
    public static HashSet<String> getBaseMapsFromStorage() {
        final String overlayDirectory = TrailScribeApplication.STORAGE_PATH + "maps/";
        List<String> fileNames = StorageSystemHelper.getFolders(overlayDirectory);
        
        HashSet<String> baseMaps = new HashSet<String>();
        for (String fileName : fileNames) {
            baseMaps.add(fileName);
        }
        
        return baseMaps;
    }
    
    /**
     * @return path of every overlay under /sdcard/trailscribe/kmls/
     */
    public static ArrayList<String> getOverlaysFromStorage() {
        final String overlayDirectory = TrailScribeApplication.STORAGE_PATH + "kmls/";
        List<String> fileNames = StorageSystemHelper.getFiles(overlayDirectory);
        
        ArrayList<String> overlays = new ArrayList<String>();
        for (String fileName : fileNames) {
            overlays.add(fileName);
        }
        
        return overlays;
    }
    
    /**
     * Create a folder in the file system if it does not exist
     * 
     * @param path The absolute path of the folder to be created
     */
    public static void createFolder(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    /**
     * Remove a file from the file system
     * 
     * @param filePath The absolute path of the file to be removed
     */
	public static void removeFile(String filePath) {
		File zipFile = new File(filePath);
		if(zipFile !=null){
			zipFile.delete();
		}
	}
	
	/**
     * Remove the content of a directory in the file system, without removing the directory itself
     * 
     * @param directory The directory whose content need to be removed 
     */
	public static void removeDirectoryContent(String directory){
		File dir = new File(directory);
		if(dir.isDirectory()){
			for (File file: dir.listFiles()){
				deleteRecursive(file);
			}
		}
	}

	/**
     * TODO: Implement
     * Given a map, return the file extension 
     * @param map
     * @return file extension
     */
    public static String getMapTileType(Map map) {
        ArrayList<String> directories = new ArrayList<String>();
        directories.add(TrailScribeApplication.STORAGE_PATH + "maps/" + map.getName());
        
        return getMapTileTypeHelper(directories);
    }
    
    private static String getMapTileTypeHelper(ArrayList<String> directories) {
        String fileType = new String();
        for (String directory : directories) {
            File folder = new File(directory);
            
            if (!folder.exists()) return new String();
            
            if (folder.isFile()) {
                String filename = folder.getName();
                int index = filename.length() - 1;
                
                while (index > -1) {
                    if (filename.charAt(index--) == '.') {
                        fileType = filename.substring(index + 2, filename.length());
                        return fileType;
                    }
                }

                return fileType;
            }
            
            if (folder.isDirectory()) {
                ArrayList<String> d = new ArrayList<String>();
                for (File file : folder.listFiles()) {
                    d.add(directory + "/" + file.getName());
                }
                
                String tileType = getMapTileTypeHelper(d); 
                if (!tileType.equals(new String())) {
                    return tileType;
                }
            }
        }
        
        return new String();
    }
	
	private static void deleteRecursive(File dir) {
	    if (dir.isDirectory()) {
	    	for (File currentFile : dir.listFiles()) {
	            if (currentFile.isDirectory()) {
	            	deleteRecursive(currentFile);
	            } else {
	            	currentFile.delete();
	            }
	        }
	    }
	    dir.delete();
	}
    
    private static void copyFile(
            AssetManager assetManager, String sourceDirectory, String targetDirectory) {
        InputStream in = null;
        OutputStream out = null;
        
        try {
            in = assetManager.open(sourceDirectory);
            out = new FileOutputStream(targetDirectory);
             
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch(IOException e) {
            Log.e(MSG_TAG, "Failed to copy asset file: " + sourceDirectory);
            Log.e(MSG_TAG, e.getMessage());
        }   
    }
}

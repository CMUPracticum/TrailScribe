package edu.cmu.sv.trailscribe.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Decompressor {
	private String mCompressedFileFullPath; 
	private String mDecompressingDirectory; 
		 
	public Decompressor(String zipFile, String location) { 
		mCompressedFileFullPath = zipFile; 
		mDecompressingDirectory = location; 
		verifyDirectory(""); 
	} 
		 
	public void unzip() { 
		try{ 
			FileInputStream fileInputStream = new FileInputStream(mCompressedFileFullPath); 
			ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream)); 
			ZipEntry zipEntry = null; 
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {  
				if(zipEntry.isDirectory()) { 
					verifyDirectory(zipEntry.getName()); 
				} else { 
					FileOutputStream fout = new FileOutputStream(mDecompressingDirectory + zipEntry.getName());
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int count;		            
					while ((count = zipInputStream.read(buffer)) != -1) {
						baos.write(buffer, 0, count);
					}
					baos.writeTo(fout);
					zipInputStream.closeEntry(); 
					fout.close(); 
				}  
			} 
			zipInputStream.close(); 
		} catch(Exception e) {
			//TODO
		}	 
	} 
	 
	private void verifyDirectory(String dir) {
		File file = new File(mDecompressingDirectory + dir); 
		if(!file.isDirectory()) { 
			file.mkdirs(); 
		} 
	}		 
}

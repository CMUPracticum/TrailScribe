package edu.cmu.sv.trailscribe.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompressor {
	private String mCompressedFileFullPath; 
	private String mDecompressingDirectory; 
		 
	public Decompressor(String zipFile, String location) { 
		mCompressedFileFullPath = zipFile; 
		mDecompressingDirectory = location; 
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
			removeZipFile();
		} catch(Exception e) {
			//TODO
		}	 
	}
	
	private void verifyDirectory(String directory) {
		File file = new File(mDecompressingDirectory + directory); 
		if(!file.isDirectory()) { 
			file.mkdirs(); 
		} 
	}

	private void removeZipFile() {
		String extension = "";
		int i = this.mCompressedFileFullPath.lastIndexOf('.');
		if (i >= 0) {
		    extension = this.mCompressedFileFullPath.substring(i+1);
		}
		
		if(extension.equals("zip")){
			File zipFile = new File(this.mCompressedFileFullPath);
			if(zipFile !=null){
				zipFile.delete();
			}
		}
	} 		 
}

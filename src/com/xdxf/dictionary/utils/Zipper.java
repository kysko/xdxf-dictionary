package com.xdxf.dictionary.utils;

import android.util.Log;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/18/13
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Zipper {
    /**
     * Unzip it
     * @param ios input zip file
     * @param outputFolder zip file output folder
     */
    public static void unzip(InputStream ios, String outputFolder){

        byte[] buffer = new byte[1024];

        try{
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(ios);
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}


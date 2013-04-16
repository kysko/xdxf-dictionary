/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xdxf.dictionary.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author comspots
 */
public class CsvReader implements Enumeration<String[]> {

    private BufferedReader br;

    public CsvReader(BufferedReader br) {
        this.br = br;

    }

    public CsvReader(Reader r) {
        this(new BufferedReader(r));
    }

    @Override
    public boolean hasMoreElements() {
        try {
            return br.ready();
        } catch (IOException ex) {
            Logger.getLogger(CsvReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public String[] nextElement() {
        String[] values = null;
        try {
            String csr = br.readLine();
            values = csr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            for (int i = 0; i < values.length; i++) {
                values[i] = Utils.Csv.Unescape(values[i]);
            }
        } catch (IOException ex) {
            Logger.getLogger(CsvReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
    }

    public void close() throws IOException {
        br.close();
    }


//    public static void main(String[] args) {
//        try {
//            CsvReader reader = new CsvReader(new FileReader("D:\\out1"));
//            while (reader.hasMoreElements()) {
//                String[] values = reader.nextElement();
//                
//                //System.out.println(values.length);
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(CsvReader.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
}

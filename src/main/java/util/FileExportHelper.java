/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class FileExportHelper {

    private static final Logger log = Logger.getLogger(FileExportHelper.class.getName());

    public static final String COMMA   = ",";
    public static final String CSV   = ".csv";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public static void writeToFile(String fileName, String content) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f);
            fw.append(content);
            fw.append(LINE_SEPARATOR);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "error writing ", ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, "error writing ", ex);
            }
        }

    }

    public static void appendLineToFile(String fileName, String content) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, true);
            fw.append(content);
            fw.append(LINE_SEPARATOR);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "error writing ", ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, "error writing ", ex);
            }
        }
    }
    
    /**
     * Must include your own newlines
     * @param fileName
     * @param content 
     */
    public static void appendBatchToFile(String fileName, String content) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, true);
            fw.append(content);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "error writing ", ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, "error writing ", ex);
            }
        }
    }
}

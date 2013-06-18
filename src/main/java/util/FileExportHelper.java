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

    public static void writeToFile(String fileName, String content) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f);
            fw.append(content);
            fw.append(System.getProperty("line.separator"));
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

    public static void appendToFile(String fileName, String content) {
        FileWriter fw = null;
        try {
            File f = new File(fileName);
            fw = new FileWriter(f, true);
            fw.append(content);
            fw.append(System.getProperty("line.separator"));
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

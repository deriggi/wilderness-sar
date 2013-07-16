/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Johnny
 */
public class PropertiesReader {

    public static Properties getPropertiesFromClasspath(String propFileName) throws IOException {
        // loading xmlProfileGen.properties from the classpath
        Properties props = new Properties();
        InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(propFileName);

        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName
                    + "' not found in the classpath");
        }

        props.load(inputStream);

        return props;
    }
}

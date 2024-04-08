package org.example.engine.core.application;

import org.example.engine.core.graphics.Renderer2D;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    // TODO: set application properties from here.
    protected void loadProfile(final String profile) {
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            // Load the properties file
            inputStream = Renderer2D.class.getClassLoader().getResourceAsStream("application-" + profile + ".properties");

            properties.load(inputStream);

            // Get a property
            String physThreads = properties.getProperty("physics2d.threads");

            // Print the properties
            System.out.println("physics threads: " + physThreads);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
package org.example;

import org.example.engine.Application;
import org.example.engine.core.collections.ArrayLong;
import org.example.engine.core.files.UtilsFiles;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {

        //String x = UtilsFiles.getFileContent("shaders/shader.vert");
        //System.out.println(x);

        Application application = new Application("My Game", true);
        application.launch();
    }
}
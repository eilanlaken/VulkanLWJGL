package org.example;

import org.example.engine.Application;

public class Main {
    public static void main(String[] args) throws Exception {

        //String x = UtilsFiles.getFileContent("shaders/shader.vert");
        //System.out.println(x);



        Application application = new Application("My Game", false);
        application.launch();
    }
}
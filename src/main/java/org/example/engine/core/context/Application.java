package org.example.engine.core.context;

public class Application {

    public final String name;
    public final String author;
    public final ApplicationVersion version;

    public Application(String name, String author, ApplicationVersion version) {
        this.name = name;
        this.author = author;
        this.version = version;
    }

    public final void launch() {

    }

}

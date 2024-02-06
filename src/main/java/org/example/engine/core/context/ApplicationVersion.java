package org.example.engine.core.context;

public class ApplicationVersion {

    public final short major;
    public final short minor;
    public final short build;
    public final short patch;
    public final String additionalIdentifier;

    public ApplicationVersion(short major, short minor, short build, short patch, String additionalIdentifier) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.patch = patch;
        this.additionalIdentifier = additionalIdentifier;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build + "." + patch + "-" + additionalIdentifier;
    }

}

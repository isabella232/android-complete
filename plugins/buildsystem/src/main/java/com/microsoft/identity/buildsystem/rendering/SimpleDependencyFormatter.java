package com.microsoft.identity.buildsystem.rendering;

import org.gradle.api.artifacts.Dependency;

public class SimpleDependencyFormatter implements IDependencyFormatter {

    private static final String SEPARATOR = ":";

    @Override
    public String formatDependency(Dependency dependency) {
        return dependency.getGroup()
                + SEPARATOR + dependency.getName()
                + SEPARATOR + dependency.getVersion();
    }
}

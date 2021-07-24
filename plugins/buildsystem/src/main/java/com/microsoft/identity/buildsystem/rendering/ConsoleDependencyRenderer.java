package com.microsoft.identity.buildsystem.rendering;

import org.gradle.api.artifacts.Dependency;

public class ConsoleDependencyRenderer extends AbstractDependencyRenderer {

    private final IDependencyFormatter mDependencyFormatter;

    public ConsoleDependencyRenderer(IDependencyFormatter dependencyFormatter) {
        mDependencyFormatter = dependencyFormatter;
    }

    @Override
    public void render(Dependency dependency) {
        render(mDependencyFormatter.formatDependency(dependency));
    }

    private void render(String formattedDependency) {
        System.out.println(formattedDependency);
    }
}

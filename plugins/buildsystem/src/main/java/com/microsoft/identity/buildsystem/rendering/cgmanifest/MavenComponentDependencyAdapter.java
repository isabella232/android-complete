package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import org.gradle.api.artifacts.Dependency;

public class MavenComponentDependencyAdapter implements IDependencyComponentAdapter<MavenComponent> {
    @Override
    public MavenComponent adapt(Dependency dependency) {
        final MavenComponentInfo mavenComponentInfo = new MavenComponentInfo(
                dependency.getGroup(),
                dependency.getName(),
                dependency.getVersion()
        );

        final MavenComponent mavenComponent = new MavenComponent(mavenComponentInfo);
        return mavenComponent;
    }
}

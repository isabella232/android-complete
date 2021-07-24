package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import com.google.gson.Gson;
import com.microsoft.identity.buildsystem.rendering.IDependencyFormatter;

import org.gradle.api.artifacts.Dependency;

public class CGManifestDependencyJsonFormatter implements IDependencyFormatter {

    private static final Gson GSON = new Gson();

    private final IDependencyComponentAdapter<MavenComponent> mDependencyComponentAdapter =
            new MavenComponentDependencyAdapter();

    @Override
    public String formatDependency(Dependency dependency) {
        final MavenComponent mavenComponent = mDependencyComponentAdapter.adapt(dependency);
        return GSON.toJson(mavenComponent);
    }
}

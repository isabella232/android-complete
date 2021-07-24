package com.microsoft.identity.buildsystem.rendering;

import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.result.DependencyResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;

import lombok.NonNull;

public class MavenDependencyAdapter implements IMavenDependencyAdapter {
    @Override
    public IMavenDependency adapt(@NonNull final Dependency dependency) {
        final String group = dependency.getGroup();
        final String name = dependency.getName();
        final String version = dependency.getVersion();

        return new MavenDependency(group, name, version);
    }

    @Override
    public IMavenDependency adapt(DependencyResult dependencyResult) {
        if (dependencyResult instanceof ResolvedDependencyResult) {
            return adapt((ResolvedDependencyResult) dependencyResult);
        } else {
            return null;
        }
    }

    private IMavenDependency adapt(ResolvedDependencyResult resolvedDependencyResult) {
        final ModuleVersionIdentifier selectedModuleVersion = resolvedDependencyResult.getSelected().getModuleVersion();

        if (selectedModuleVersion == null) {
            return null;
        }

        final String group = selectedModuleVersion.getGroup();
        final String name = selectedModuleVersion.getName();
        final String version = selectedModuleVersion.getVersion();

        return new MavenDependency(group, name, version);
    }
}

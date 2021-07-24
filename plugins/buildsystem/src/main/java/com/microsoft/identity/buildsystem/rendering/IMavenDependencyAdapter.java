package com.microsoft.identity.buildsystem.rendering;

import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.result.DependencyResult;

public interface IMavenDependencyAdapter {

    IMavenDependency adapt(Dependency dependency);

    IMavenDependency adapt(DependencyResult dependencyResult);
}

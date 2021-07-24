package com.microsoft.identity.buildsystem.rendering;

import org.gradle.api.artifacts.Dependency;

public interface IDependencyFormatter {

    String formatDependency(Dependency dependency);
}

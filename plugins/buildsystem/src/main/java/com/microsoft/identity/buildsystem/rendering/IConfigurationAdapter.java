package com.microsoft.identity.buildsystem.rendering;

import org.gradle.api.artifacts.Configuration;

public interface IConfigurationAdapter {

    DependencyType adapt(Configuration configuration);
}

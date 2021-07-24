package com.microsoft.identity.buildsystem.rendering;

import org.gradle.api.artifacts.Configuration;

import lombok.NonNull;

public class ConfigurationAdapter implements IConfigurationAdapter {
    @Override
    public DependencyType adapt(Configuration configuration) {
        if (isRuntimeConfiguration(configuration.getName())) {
            return DependencyType.RUNTIME;
        } else {
            return DependencyType.DEVELOPMENT;
        }
    }

    private boolean isRuntimeConfiguration(@NonNull final String configurationName) {
        switch (configurationName) {
            case "runtimeClasspath":
            case "implementation":
                return true;
            default:
                return false;
        }
    }
}

package com.microsoft.identity.buildsystem.rendering.settings;

public interface IDependencyRendererSettingsAdapter {

    DependencyRendererSettings adapt(
            DependencyRendererSettingsExtension dependencyRendererSettingsExtension
    );

}

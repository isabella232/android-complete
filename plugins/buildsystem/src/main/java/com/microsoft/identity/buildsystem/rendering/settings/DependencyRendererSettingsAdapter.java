package com.microsoft.identity.buildsystem.rendering.settings;

import org.gradle.api.provider.Property;

public class DependencyRendererSettingsAdapter implements IDependencyRendererSettingsAdapter {
    @Override
    public DependencyRendererSettings adapt(DependencyRendererSettingsExtension extension) {
        final DependencyRendererSettings.DependencyRendererSettingsBuilder builder =
                DependencyRendererSettings.builder();

        if (extension == null) {
            return builder.build();
        }

        Property<Boolean> renderProjectDependency = extension.getRenderProjectDependency();
        if (renderProjectDependency != null && renderProjectDependency.isPresent()) {
            builder.renderProjectDependency(renderProjectDependency.get());
        }

        return builder.build();
    }
}

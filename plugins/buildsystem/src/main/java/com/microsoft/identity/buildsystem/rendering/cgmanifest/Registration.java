package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Registration {

    @SerializedName(SerializedNames.COMPONENT)
    private final Component mComponent;

    @SerializedName(SerializedNames.DEVELOPMENT_DEPENDENCY)
    private final boolean mDevelopmentDependency;

    private static class SerializedNames {
        private static final String COMPONENT = "Component";
        private static final String DEVELOPMENT_DEPENDENCY = "DevelopmentDependency";
    }
}

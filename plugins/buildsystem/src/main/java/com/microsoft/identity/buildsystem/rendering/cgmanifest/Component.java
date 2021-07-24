package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Component {

    @SerializedName(SerializedNames.TYPE)
    private final String mType;

    private static class SerializedNames {
        private static final String TYPE = "Type";
    }
}

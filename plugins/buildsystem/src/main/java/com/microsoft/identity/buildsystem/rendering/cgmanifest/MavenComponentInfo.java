package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(prefix = "m")
public class MavenComponentInfo implements IMavenComponentInfo {

    @SerializedName(SerializedNames.GROUP_ID)
    private final String mGroupId;

    @SerializedName(SerializedNames.ARTIFACT_ID)
    private final String mArtifactId;

    @SerializedName(SerializedNames.VERSION)
    private final String mVersion;

    private static class SerializedNames {
        private static final String GROUP_ID = "GroupId";
        private static final String ARTIFACT_ID = "ArtifactId";
        private static final String VERSION = "Version";
    }
}

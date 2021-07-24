package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class MavenComponent extends Component {

    private static final String MAVEN_COMPONENT_TYPE_NAME = "Maven";

    @SerializedName(MAVEN_COMPONENT_TYPE_NAME)
    private final MavenComponentInfo mMavenComponentInfo;

    public MavenComponent(@NonNull final MavenComponentInfo mavenComponentInfo) {
        super(MAVEN_COMPONENT_TYPE_NAME);
        this.mMavenComponentInfo = mavenComponentInfo;
    }
}

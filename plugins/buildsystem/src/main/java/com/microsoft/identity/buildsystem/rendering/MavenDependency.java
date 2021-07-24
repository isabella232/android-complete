package com.microsoft.identity.buildsystem.rendering;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(prefix = "m")
@EqualsAndHashCode
public class MavenDependency implements IMavenDependency {
    private final String mGroup;
    private final String mName;
    private final String mVersion;
}

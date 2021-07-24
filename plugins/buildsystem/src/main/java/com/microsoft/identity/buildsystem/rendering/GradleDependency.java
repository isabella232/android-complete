package com.microsoft.identity.buildsystem.rendering;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(prefix = "m")
@EqualsAndHashCode
public class GradleDependency {
    private final DependencyType mDependencyType;
    private final IMavenDependency mMavenDependency;
}

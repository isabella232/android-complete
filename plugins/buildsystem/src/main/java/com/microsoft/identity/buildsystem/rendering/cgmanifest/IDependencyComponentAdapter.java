package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import org.gradle.api.artifacts.Dependency;

public interface IDependencyComponentAdapter<T extends Component> {

    T adapt(Dependency dependency);
}

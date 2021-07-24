//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.identity.buildsystem.rendering;

import com.microsoft.identity.buildsystem.rendering.settings.DependencyRendererSettings;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.result.DependencyResult;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.tasks.diagnostics.internal.DependencyReportRenderer;
import org.gradle.api.tasks.diagnostics.internal.TextReportRenderer;
import org.gradle.internal.deprecation.DeprecatableConfiguration;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(prefix = "m")
public abstract class AbstractDependencyRenderer extends TextReportRenderer implements DependencyReportRenderer {

    private final DependencyRendererSettings mDependencyRendererSettings;

    private static final IMavenDependencyAdapter sMavenDependencyAdapter = new MavenDependencyAdapter();

    private static final IConfigurationAdapter sConfigurationAdapter = new ConfigurationAdapter();

    public abstract void render(@NonNull final GradleDependency gradleDependency);

    private final Set<GradleDependency> mRenderedDependencies = new HashSet<>();

    @Override
    public void startConfiguration(Configuration configuration) {
        System.out.println("Starting configuration: " + configuration.getName());
        // We don't need to do anything here by default
    }

    @Override
    public void render(Configuration configuration) {
        if (mDependencyRendererSettings.isRenderTransitiveDependencies() && canBeResolved(configuration)) {
            final ResolutionResult result = configuration.getIncoming().getResolutionResult();
            final Set<? extends DependencyResult> results = result.getAllDependencies();
            renderDependencyResultSet(configuration, results);
        } else {
            final DependencySet dependencies = configuration.getDependencies();
            renderDependencySet(configuration, dependencies);
        }
    }

    @Override
    public void completeConfiguration(Configuration configuration) {
        // We don't need to do anything here by default
    }

    private void renderDependencySet(@NonNull final Configuration configuration,
                                     @NonNull final DependencySet dependencies) {
        dependencies.iterator().forEachRemaining(
                dependency -> renderDependency(configuration, dependency)
        );
    }

    private void renderDependencyResultSet(@NonNull final Configuration configuration,
                                           @NonNull final Set<? extends DependencyResult> dependencyResults) {
        dependencyResults.iterator().forEachRemaining(
                dependencyResult -> renderDependencyResult(configuration, dependencyResult)
        );
    }

    private void renderDependencyResult(@NonNull final Configuration configuration,
                                        @NonNull final DependencyResult dependencyResult) {
        final IMavenDependency mavenDependency = sMavenDependencyAdapter.adapt(dependencyResult);
        if (mavenDependency != null) {
            renderInternal(configuration, mavenDependency);
        }
    }

    private void renderDependency(@NonNull final Configuration configuration,
                                  @NonNull final Dependency dependency) {
        if (shouldRender(dependency)) {
            renderInternal(configuration, sMavenDependencyAdapter.adapt(dependency));
        }
    }

    private boolean shouldRender(@NonNull final Dependency dependency) {
        return !(dependency instanceof ProjectDependency) ||
                mDependencyRendererSettings.isRenderProjectDependency();
    }

    private boolean canBeResolved(Configuration configuration) {
        boolean isDeprecatedForResolving = ((DeprecatableConfiguration) configuration).getResolutionAlternatives() != null;
        return configuration.isCanBeResolved() && !isDeprecatedForResolving;
    }

    private boolean alreadyRendered(@NonNull final GradleDependency gradleDependency) {
        return mRenderedDependencies.contains(gradleDependency);
    }

    private void renderInternal(@NonNull final Configuration configuration, @NonNull final IMavenDependency mavenDependency) {
        final DependencyType dependencyType = sConfigurationAdapter.adapt(configuration);

        final GradleDependency gradleDependency = new GradleDependency(
                dependencyType,
                mavenDependency
        );

        if (alreadyRendered(gradleDependency)) {
            return;
        }

        render(gradleDependency);
        mRenderedDependencies.add(gradleDependency);
    }
}

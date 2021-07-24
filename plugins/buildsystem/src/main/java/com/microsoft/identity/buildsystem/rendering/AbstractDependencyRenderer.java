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

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.tasks.diagnostics.internal.DependencyReportRenderer;
import org.gradle.api.tasks.diagnostics.internal.TextReportRenderer;

public abstract class AbstractDependencyRenderer extends TextReportRenderer implements DependencyReportRenderer {

    @Override
    public void startConfiguration(Configuration configuration) {
        // We don't need to do anything here by default
    }

    @Override
    public void render(Configuration configuration) {
        final DependencySet dependencies = configuration.getDependencies();
        dependencies.iterator().forEachRemaining(this::renderExternalDependency);
    }

    @Override
    public void completeConfiguration(Configuration configuration) {
        // We don't need to do anything here by default
    }

    private void renderExternalDependency(Dependency dependency) {
        if (!(dependency instanceof ProjectDependency)) {
            render(dependency);
        }
    }

    public abstract void render(Dependency dependency);
}

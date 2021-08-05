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
package com.microsoft.identity.buildsystem.rendering.settings;

import org.gradle.api.provider.Property;

import java.io.File;

/**
 * Gradle Build Extension to provide dependency rendering configuration to our
 * {@link com.microsoft.identity.buildsystem.BuildPlugin}.
 */
public abstract class DependencyRendererSettingsExtension {

    /**
     * Get the property that indicates if project dependencies should be rendered by the build
     * plugin.
     *
     * @return a {@link Property<Boolean>} that indicates if project dependencies should be rendered
     */
    abstract public Property<Boolean> getRenderProjectDependency();

    /**
     * Get the property that indicates if transitive dependencies should be rendered by the build
     * plugin.
     *
     * @return a {@link Property<Boolean>} that indicates if transitive dependencies should be
     * rendered
     */
    abstract public Property<Boolean> getRenderTransitiveDependencies();

    /**
     * Get the property that indicates the directory where the
     * {@link com.microsoft.identity.buildsystem.rendering.cgmanifest.CgManifest} file should be
     * created.
     *
     * @return a {@link Property<File>} that indicates the directory of the cg manifest file
     */
    abstract public Property<File> getCgManifestReportDirectory();
}

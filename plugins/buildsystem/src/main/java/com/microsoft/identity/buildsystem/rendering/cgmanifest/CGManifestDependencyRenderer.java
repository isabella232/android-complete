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
package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.microsoft.identity.buildsystem.rendering.AbstractDependencyRenderer;
import com.microsoft.identity.buildsystem.rendering.DependencyType;
import com.microsoft.identity.buildsystem.rendering.GradleDependency;
import com.microsoft.identity.buildsystem.rendering.IMavenDependency;
import com.microsoft.identity.buildsystem.rendering.settings.DependencyRendererSettings;

import org.gradle.api.Project;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.NonNull;

public class CGManifestDependencyRenderer extends AbstractDependencyRenderer {

    private static final String CG_MANIFEST_FILE_NAME = "cgmanifest.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final IDependencyComponentAdapter<MavenComponent> mDependencyComponentAdapter =
            new MavenComponentDependencyAdapter();

    private final Map<IMavenDependency, DependencyType> mRegisteredMavenDependencies = new HashMap<>();

    public CGManifestDependencyRenderer(DependencyRendererSettings dependencyRendererSettings) {
        super(dependencyRendererSettings);
    }

    @Override
    public void render(@NonNull GradleDependency gradleDependency) {
        // we could do something here...but let's just take the final result from the complete
        // method
    }

    @Override
    public void complete(@NonNull Collection<GradleDependency> gradleDependencies) {
        final CgManifest cgManifest = createCgManifest(gradleDependencies);

        final String cgManifestJson = GSON.toJson(cgManifest);
        final JsonElement cgManifestJsonElement = JSON_PARSER.parse(cgManifestJson);
        final String cgManifestPrettyJson = GSON.toJson(cgManifestJsonElement);
        System.out.println(cgManifestPrettyJson);
        dumpToCgManifestJsonFile(cgManifestPrettyJson);
    }

    @Override
    public void completeProject(Project project) {
        super.completeProject(project);
    }

    private CgManifest createCgManifest(@NonNull final Collection<GradleDependency> gradleDependencies) {
        final CgManifest cgManifest = new CgManifest();

        for (final GradleDependency gradleDependency : gradleDependencies) {
            final IMavenDependency mavenDependency = gradleDependency.getMavenDependency();
            final DependencyType dependencyType = gradleDependency.getDependencyType();
            final Set<IMavenDependency> rootDeps = gradleDependency.getDependencyRoots();

            final MavenComponent mavenComponent = mDependencyComponentAdapter.adapt(
                    mavenDependency
            );

            final Set<Component> rootComponents = new HashSet<>();

            rootDeps.iterator().forEachRemaining(mavenDep -> rootComponents.add(mDependencyComponentAdapter.adapt(mavenDep)));

            cgManifest.addRegistration(new Registration(
                    mavenComponent,
                    dependencyType == DependencyType.DEVELOPMENT,
                    rootComponents
            ));
        }

        return cgManifest;
    }

    private void dumpToCgManifestJsonFile(final String text) {
        try {
            final FileWriter fileWriter = new FileWriter(CG_MANIFEST_FILE_NAME);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(text);
            printWriter.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}

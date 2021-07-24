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
import com.microsoft.identity.buildsystem.rendering.AbstractDependencyRenderer;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CGManifestDependencyRenderer extends AbstractDependencyRenderer {

    private static final String CG_MANIFEST_FILE_NAME = "cgmanifest.json";

    final CgManifest mCgManifest = new CgManifest();

    private static final Gson GSON = new Gson();

    private final IDependencyComponentAdapter<MavenComponent> mDependencyComponentAdapter =
            new MavenComponentDependencyAdapter();

    @Override
    public void render(Dependency dependency) {
        final MavenComponent mavenComponent = mDependencyComponentAdapter.adapt(dependency);
        mCgManifest.addRegistration(new Registration(mavenComponent, false));
    }

    @Override
    public void completeProject(Project project) {
        super.completeProject(project);
        System.out.println(GSON.toJson(mCgManifest));
        dumpToFile();
    }

    private void dumpToFile() {
        try {
            final FileWriter fileWriter = new FileWriter(CG_MANIFEST_FILE_NAME);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(GSON.toJson(mCgManifest));
            printWriter.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}

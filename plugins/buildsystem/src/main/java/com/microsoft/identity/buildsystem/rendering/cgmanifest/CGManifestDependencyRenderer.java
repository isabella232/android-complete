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

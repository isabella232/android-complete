package com.microsoft.identity.buildsystem.rendering.cgmanifest;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.NonNull;

public class CgManifest {

    @SerializedName(SerializedNames.REGISTRATIONS)
    private final List<Registration> mRegistrations = new ArrayList<>();

    public void addRegistration(@NonNull final Registration registration) {
        mRegistrations.add(registration);
    }

    public List<Registration> getRegistrations() {
        return Collections.unmodifiableList(mRegistrations);
    }

    private static class SerializedNames {
        private static final String REGISTRATIONS = "Registrations";
    }
}

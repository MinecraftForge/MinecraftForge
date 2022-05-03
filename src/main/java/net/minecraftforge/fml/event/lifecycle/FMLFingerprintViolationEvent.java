/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import java.io.File;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.fml.common.Mod;


/**
 * DEPRECATED WITHOUT REPLACEMENT. REMOVE FROM YOUR CODE!!!
 * IT HAS NEVER BEEN FIRED IN 1.13+ AND WILL NEVER FIRE AGAIN!!!
 * FIRE.JPG FIRE.JPG FIRE.JPG
 * DELET THIS
 */
@Deprecated
public class FMLFingerprintViolationEvent extends ModLifecycleEvent
{

    private final boolean isDirectory;
    private final Set<String> fingerprints;
    private final File source;
    private final String expectedFingerprint;

    public FMLFingerprintViolationEvent(boolean isDirectory, File source, ImmutableSet<String> fingerprints, String expectedFingerprint)
    {
        super(null);
        this.isDirectory = isDirectory;
        this.source = source;
        this.fingerprints = fingerprints;
        this.expectedFingerprint = expectedFingerprint;
    }

    public boolean isDirectory() { return isDirectory; }
    public Set<String> getFingerprints() { return fingerprints; }
    public File getSource() { return source; }
    public String getExpectedFingerprint() { return expectedFingerprint; }
}

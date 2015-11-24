/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common.event;

import java.io.File;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.fml.common.Mod;


/**
 * A special event used when the {@link Mod#certificateFingerprint()} doesn't match the certificate loaded from the JAR
 * file. You could use this to log a warning that the code that is running might not be yours, for example.
 */
public class FMLFingerprintViolationEvent extends FMLEvent {

    public final boolean isDirectory;
    public final Set<String> fingerprints;
    public final File source;
    public final String expectedFingerprint;

    public FMLFingerprintViolationEvent(boolean isDirectory, File source, ImmutableSet<String> fingerprints, String expectedFingerprint)
    {
        super();
        this.isDirectory = isDirectory;
        this.source = source;
        this.fingerprints = fingerprints;
        this.expectedFingerprint = expectedFingerprint;
    }
}

package cpw.mods.fml.common.event;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;


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

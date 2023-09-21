package net.minecraftforge.network;

import net.minecraftforge.fml.IExtensionPoint;

@Deprecated
public final class NetworkConstants {
    /**
     * To enable binary compatibility with mods compiled against older versions of Forge. Will be removed in MC 1.21.
     * @deprecated Use {@link IExtensionPoint.DisplayTest#IGNORESERVERONLY} instead.
     */
    @Deprecated(forRemoval = true, since = "1.21")
    public static final String IGNORESERVERONLY = IExtensionPoint.DisplayTest.IGNORESERVERONLY;
}

/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * An extension point for a mod container.
 *
 * <p>An extension point can be registered for a mod container using {@link ModContainer#registerExtensionPoint(Class, Supplier)}
 * and retrieved (if present) using {@link ModContainer#getCustomExtension(Class)}. An extension point allows a mod to
 * supply an arbitrary value as a record class to another mod or framework through their mod container class, avoiding
 * the use of {@link InterModComms} or other external frameworks to pass around these values.</p>
 *
 * <p>The usual way to declare an extension point is to implement this interface on a record class, with the type
 * parameter being a reference to the class itself. For example, {@code record MyExtension(...) extends
 * IExtensionPoint<MyExtension>} would declare an extension point which supplies a {@code MyExtension} object. However,
 * there is no hard requirement that an extension point's type parameter must be in reference to itself; the type
 * parameter may reference another record class instead.</p>
 *
 * @param <T> the type of the record which is held by the extension point
 */
@SuppressWarnings("unused") // Type parameter T
public interface IExtensionPoint<T extends Record>
{
    /**
     * Extension point for the compatibility display test used on the server selection screen.
     *
     * <em>Note: "server" and "client" refers to the {@linkplain net.minecraftforge.api.distmarker.Dist#DEDICATED_SERVER dedicated server}
     * and {@linkplain net.minecraftforge.api.distmarker.Dist#CLIENT game client} physical distributions, rather than the
     * {@linkplain LogicalSide logical server and client}.</em>
     *
     * <p>The {@link Supplier} provides the local compatibility version, which is sent from the server to the client
     * for multiplayer connections or stored to disk for the world save. The {@link BiPredicate} accepts the remote
     * compatibility version and a boolean indicating whether the remote version is from the server or a world save,
     * where {@code true} means it is from the server and {@code false} means it is from the world save. The return
     * value of the predicate determines whether the remote version is "compatible" for the purposes of the display test.</p>
     *
     * <p>The local compatibility version may be of the value {@link net.minecraftforge.network.NetworkConstants#IGNORESERVERONLY},
     * in which case clients will ignore the mod's presence if it is present on the server but not on the client.
     * However, the remote version test predicate must still accept this value as a remote version in order to display
     * as compatible if the mod is present on the client.</p>
     *
     * <p><strong>The compatibility display test does not necessarily indicate the success or failure of an actual
     * connection attempt.</strong> Factors such as display test extension misconfiguration, truncation of ping data,
     * difference of registry data or network channels between server and client, and others may cause the result of the
     * compatibility test to not reflect the actual likelihood of a connection successfully being established between
     * the server and the client.</p>
     *
     * <p>An example declaration of a display test extension registration for a regular mod (requires to be present on
     * server and client) is as follows:</p>
     * <pre>{@code
     * String compatibilityVersion = "1"; // Could be linked with a network channel version or mod version
     * ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
     *         () -> new IExtensionPoint.DisplayTest(
     *                 () -> compatibilityVersion,
     *                 (remoteVersion, isFromServer) -> remoteVersion.equals(compatibilityVersion)
     *         )
     * );
     * }</pre>
     *
     * <p>An example declaration of a display test extension registration for a <em>server-side-only</em> mod (does not
     * require to be present on the client) is as follows:</p>
     * <pre>{@code
     * ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
     *         () -> new IExtensionPoint.DisplayTest(
     *                 // Ignore this mod if not present on the client
     *                 () -> NetworkConstants.IGNORESERVERONLY,
     *                 // If present on the client, accept any version if from a server
     *                 (remoteVersion, isFromServer) -> isFromServer
     *         )
     * );
     * }</pre>
     *
     * <p>An example declaration of a display test extension registration for a <em>client-side-only</em> mod (does not
     * require to be present on the server) is as follows:</p>
     * <pre>{@code
     * ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
     *         () -> new IExtensionPoint.DisplayTest(
     *                 // Send any version from server to client, since we will be accepting any version as well
     *                 () -> "dQw4w9WgXcQ",
     *                 // Accept any version on the client, from server or from save
     *                 (remoteVersion, isFromServer) -> true
     *         )
     * );
     * }</pre>
     *
     * @see net.minecraftforge.network.ServerStatusPing
     * @see net.minecraftforge.client.ForgeHooksClient#processForgeListPingData(net.minecraft.network.protocol.status.ServerStatus, net.minecraft.client.multiplayer.ServerData)
     */
    @SuppressWarnings("JavadocReference") // reference to NetworkConstants, ForgeHooksClient
    record DisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest) implements IExtensionPoint<DisplayTest> {
        public static final String IGNORESERVERONLY = "OHNOES\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31";
    }
}

/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.targets;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
sealed abstract class ForgeDevLaunchHandler extends CommonDevLaunchHandler {
    private ForgeDevLaunchHandler(LaunchType type) {
        super(type, "forge_dev_");
    }

    private ForgeDevLaunchHandler(String name) {
        super(name);
    }

    @Override
    public List<Path> getMinecraftPaths() {
        // The client extra jar is on the classpath we can try locating it using the .mcassetsroot which vanilla uses for loading data
        var extra = getPathFromResource("assets/.mcassetsroot");
        // Minecraft is an exploded directory, so find it.
        var minecraft = getPathFromResource("net/minecraft/client/Minecraft.class");
        var forge = getPathFromResource("net/minecraftforge/common/MinecraftForge.class");

        // If both Forge and MC are in the same folder, then we are in intellij or gradle
        // So we have to create a filtered jar
        if (!forge.equals(minecraft))
            return List.of(minecraft, extra);

        var filtered = CommonDevLaunchHandler.getMinecraftOnly(extra, minecraft);
        return List.of(filtered);
    }

    public static final class Client extends ForgeDevLaunchHandler {
        public Client() {
            super(CLIENT);
        }
    }

    public static final class ClientData extends ForgeDevLaunchHandler {
        public ClientData() {
            super(CLIENT_DATA);
        }
    }

    public static final class Data extends ForgeDevLaunchHandler {
        public Data() {
            super(DATA);
        }
    }

    public static final class Server extends ForgeDevLaunchHandler {
        public Server() {
            super(SERVER);
        }
    }

    public static final class ServerGameTest extends ForgeDevLaunchHandler {
        public ServerGameTest() {
            super(SERVER_GAMETEST);
        }
    }

    public static final class Custom extends ForgeDevLaunchHandler {
        public Custom() {
            super("forge_dev");
        }
    }
}

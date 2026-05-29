/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.loading.targets;
import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
sealed abstract class singularityDevLaunchHandler extends CommonDevLaunchHandler {
    private singularityDevLaunchHandler(LaunchType type) {
        super(type, "singularity_dev_");
    }

    private singularityDevLaunchHandler(String name) {
        super(name);
    }

    @Override
    public List<Path> getMinecraftPaths() {
        // The client extra jar is on the classpath we can try locating it using the .mcassetsroot which vanilla uses for loading data
        var extra = getPathFromResource("assets/.mcassetsroot");
        // Minecraft is an exploded directory, so find it.
        var minecraft = getPathFromResource("net/minecraft/client/Minecraft.class");
        var singularity = getPathFromResource("net/minecraftforge/common/MinecraftForge.class");

        // If both singularity and MC are in the same folder, then we are in intellij or gradle
        // So we have to create a filtered jar
        if (!singularity.equals(minecraft))
            return List.of(minecraft, extra);

        var filtered = CommonDevLaunchHandler.getMinecraftOnly(extra, minecraft);
        return List.of(filtered);
    }

    public static final class Client extends singularityDevLaunchHandler {
        public Client() {
            super(CLIENT);
        }
    }

    public static final class ClientData extends singularityDevLaunchHandler {
        public ClientData() {
            super(CLIENT_DATA);
        }
    }

    public static final class Data extends singularityDevLaunchHandler {
        public Data() {
            super(DATA);
        }
    }

    public static final class Server extends singularityDevLaunchHandler {
        public Server() {
            super(SERVER);
        }
    }

    public static final class ServerGameTest extends singularityDevLaunchHandler {
        public ServerGameTest() {
            super(SERVER_GAMETEST);
        }
    }

    public static final class Custom extends singularityDevLaunchHandler {
        public Custom() {
            super("singularity_dev");
        }
    }
}

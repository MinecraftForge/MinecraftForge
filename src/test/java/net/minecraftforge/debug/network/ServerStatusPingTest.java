package net.minecraftforge.debug.network;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.network.NetworkContext;
import net.minecraftforge.network.ServerStatusPing;
import net.minecraftforge.test.BaseTestMod;

@Mod(ServerStatusPingTest.MODID)
@GameTestNamespace("forge")
public class ServerStatusPingTest extends BaseTestMod {
    static final String MODID = "server_status_ping";

    public ServerStatusPingTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void fml_2(GameTestHelper helper) {
        test(helper, 2);
    }

    @GameTest
    public static void fml_3(GameTestHelper helper) {
        test(helper, 3);

    }

    @GameTest
    public static void fml_current(GameTestHelper helper) {
        test(helper, NetworkContext.NET_VERSION);
    }

    @GameTest
    private static void test(GameTestHelper helper, int fmlNetworkVer) {
        ServerStatusPing status = new ServerStatusPing(fmlNetworkVer);
        DataResult<JsonElement> encoded = ServerStatusPing.CODEC.encodeStart(JsonOps.INSTANCE, status);

        JsonElement json = encoded.getOrThrow();

        DataResult<Pair<ServerStatusPing, JsonElement>> decoded = ServerStatusPing.CODEC.decode(JsonOps.INSTANCE, json);

        ServerStatusPing deserializedStatus = decoded.getOrThrow().getFirst();

        helper.assertValueEqual(status, deserializedStatus, Component.literal("ServerStatusPing should be identical when serializing & deserializing."));
        helper.succeed();
    }
}

package net.minecraftforge.debug.network;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.network.Channel.VersionTest;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.ForgePayload;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + PacketTest.MODID)
@Mod(PacketTest.MODID)
public class PacketTest extends BaseTestMod {
    static final String MODID = "packet";

    protected static final Logger LOGGER = LogUtils.getLogger();

    public PacketTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        TestChannel.registerMessages();
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void send_to_player(GameTestHelper helper) {
        // Send a test packet directly to a player
        ServerPlayer target = mockPlayer(helper);
        ServerPlayer bystander = mockPlayer(helper);
        try {
            var distro = PacketDistributor.PLAYER.with(target);
            TestChannel.CHANNEL.send(new TestPacket("player target"), distro);
            helper.assertValueEqual(count(target), 1, "Target packet count");
            helper.assertValueEqual(count(bystander), 0, "Bystander packet count");

            helper.succeed();
        } finally {
            helper.getLevel().getServer().getPlayerList().remove(target);
            helper.getLevel().getServer().getPlayerList().remove(bystander);
        }
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void send_to_nearby(GameTestHelper helper) {
        // Send a test packet to all players near a target point

        ServerPlayer target = mockPlayer(helper);
        ServerPlayer bystander = mockPlayer(helper);
        try {
            var center = helper.absoluteVec(Vec3.ZERO);
            target.moveTo(center); // Move to center
            bystander.moveTo(center.add(0, 10, 0)); // Move 10 blocks away
            var point = new PacketDistributor.TargetPoint(center.x(), center.y(), center.z(), 1/* Only within 1 block */, helper.getLevel().dimension());
            var distro = PacketDistributor.NEAR.with(point);

            TestChannel.CHANNEL.send(new TestPacket("point target"), distro);
            helper.assertValueEqual(count(target), 1, "Target packet count");
            helper.assertValueEqual(count(bystander), 0, "Bystander packet count");

            helper.succeed();
        } finally {
            helper.getLevel().getServer().getPlayerList().remove(target);
            helper.getLevel().getServer().getPlayerList().remove(bystander);
        }
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void send_to_all(GameTestHelper helper) {
        // Send a test packet to all players
        ServerPlayer target = mockPlayer(helper);
        ServerPlayer bystander = mockPlayer(helper);
        try {
            var distro = PacketDistributor.ALL.noArg();

            TestChannel.CHANNEL.send(new TestPacket("everyone!"), distro);
            helper.assertValueEqual(count(target), 1, "Target packet count");
            helper.assertValueEqual(count(bystander), 1, "Bystander packet count");

            helper.succeed();
        } finally {
            helper.getLevel().getServer().getPlayerList().remove(target);
            helper.getLevel().getServer().getPlayerList().remove(bystander);
        }
    }

    private static ServerPlayer mockPlayer(GameTestHelper helper) {
        ServerPlayer ret = helper.makeMockServerPlayer();
        CommonListenerCookie cookie = new CommonListenerCookie(null, 0, null, false);

        helper.getLevel().getServer().getPlayerList().placeNewPlayer(ret.connection.getConnection(), ret, cookie);
        // Ignore all the packets sent during connection
        count(ret);
        return ret;
    }

    private static int count(ServerPlayer player) {
        EmbeddedChannel ec = (EmbeddedChannel)player.connection.getConnection().channel();

        int count = 0;
        Object msg;
        while ((msg = ec.readOutbound()) != null) {
            if (msg instanceof ClientboundCustomPayloadPacket pkt &&
                pkt.payload() instanceof ForgePayload pay &&
                pay.id().equals(TestChannel.CHANNEL_NAME)
            )
                count++;
        }
        return count;
    }

    private record TestPacket(String msg) {
        public static final StreamCodec<RegistryFriendlyByteBuf, TestPacket> STREAM_CODEC =
            StreamCodec.ofMember(TestPacket::encode, TestPacket::decode);

        public static void encode(TestPacket message, RegistryFriendlyByteBuf buf) {
            buf.writeUtf(message.msg);
        }

        public static TestPacket decode(RegistryFriendlyByteBuf buf) {
            return new TestPacket(buf.readUtf());
        }

        public static void onMessage(TestPacket message, CustomPayloadEvent.Context ctx) {
            LOGGER.info("Received packet with message: {}", message.msg);
        }
    }

    private static class TestChannel {
        private static final ResourceLocation CHANNEL_NAME = rl(PacketTest.MODID, "main_channel");
        private static final int PROTOCOL_VERSION = 1;

        private static final SimpleChannel CHANNEL = ChannelBuilder
            .named(CHANNEL_NAME)
            .clientAcceptedVersions(VersionTest.exact(PROTOCOL_VERSION))
            .serverAcceptedVersions(VersionTest.exact(PROTOCOL_VERSION))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .simpleChannel()
                .play()
                    .clientbound()
                        .addMain(TestPacket.class, TestPacket.STREAM_CODEC, TestPacket::onMessage)
            .build();

        public static void registerMessages() {
            LOGGER.debug("Registering network {} v{}", CHANNEL.getName(), CHANNEL.getProtocolVersion());
        }
    }
}

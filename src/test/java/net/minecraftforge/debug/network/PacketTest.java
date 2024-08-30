package net.minecraftforge.debug.network;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.network.Channel.VersionTest;
import net.minecraftforge.network.ChannelBuilder;
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
        PacketHandler.registerMessages();
    }
    
    @GameTest(template = "forge:empty3x3x3")
    public static void send_to_player(GameTestHelper helper) {
        // Send a test packet directly to a player
        ServerPlayer player = helper.makeMockServerPlayer();
        PacketHandler.sendToPlayer(new TestPacket("testing123"), player);
        helper.succeed();
    }
    
    @GameTest(template = "forge:empty3x3x3")
    public static void send_to_nearby(GameTestHelper helper) {
        // Send a test packet to all players near a target point
        helper.makeMockPlayer(GameType.SURVIVAL);
        PacketHandler.sendToAllAround(new TestPacket("testing123"), Level.OVERWORLD, BlockPos.ZERO, 5D);
        helper.succeed();
    }
    
    @GameTest(template = "forge:empty3x3x3")
    public static void send_to_all(GameTestHelper helper) {
        // Send a test packet to all players
        helper.makeMockPlayer(GameType.SURVIVAL);
        PacketHandler.sendToAll(new TestPacket("testing123"));
        helper.succeed();
    }
    
    public static class TestPacket {
        public static final StreamCodec<RegistryFriendlyByteBuf, TestPacket> STREAM_CODEC = StreamCodec.ofMember(TestPacket::encode, TestPacket::decode);
        protected final String msg;
        
        public TestPacket(String msg) {
            this.msg = msg;
        }
        
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
    
    public static class PacketHandler {
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
        
        public static void sendToPlayer(Object message, ServerPlayer player) {
            // Send a message to a specific player
            CHANNEL.send(message, PacketDistributor.PLAYER.with(player));
        }
        
        public static void sendToAllAround(Object message, ResourceKey<Level> dimension, BlockPos center, double radius) {
            // Send a message to the clients of all players within a given distance of the given world position
            CHANNEL.send(message, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D, radius, dimension)));
        }
        
        public static void sendToAll(Object message) {
            // Send a message to all connected clients
            CHANNEL.send(message, PacketDistributor.ALL.noArg());
        }
    }
}

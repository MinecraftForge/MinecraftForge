package net.minecraftforge.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.INetworkCapability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NetworkCapabilityTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NetworkCapabilityTest.MOD_ID)
public class NetworkCapabilityTest {

    public static final String MOD_ID = "network_capability_test";

    private static final Logger logger = LogManager.getLogger();

    @CapabilityInject(TestCapability.class)
    public static final Capability<TestCapability> TEST_CAP = null;

    public NetworkCapabilityTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NetworkCapabilityTest::handleCommonSetup);
    }

    private static void handleCommonSetup(FMLCommonSetupEvent event)
    {
        CapabilityManager.INSTANCE.register(TestCapability.class, new EmptyStorage<>(), TestCapability::new);
    }

    private static class TestCapability {

        private int number;
        private boolean dirty;

        public void update()
        {
            this.number++;
            this.dirty = true;
        }
    }

    private static class TestCapabilityProvider implements ICapabilityProvider, INetworkCapability {

        private final LazyOptional<TestCapability> cap = LazyOptional.of(() -> new TestCapability());

        @Override
        public void encode(PacketBuffer out, boolean writeAll)
        {
            this.cap.ifPresent(cap ->
            {
                cap.dirty = false;
                out.writeVarInt(cap.number);
            });
        }

        @Override
        public void decode(PacketBuffer in)
        {
            this.cap.ifPresent(cap ->
            {
                cap.number = in.readVarInt();
                logger.info("Received new number: " + cap.number);
            });
        }

        @Override
        public boolean requiresSync()
        {
            return this.cap.map(cap -> cap.dirty).orElse(false);
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
        {
            return cap == TEST_CAP ? this.cap.cast() : LazyOptional.empty();
        }

    }

    @SubscribeEvent
    public static void handleAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        event.addCapability(new ResourceLocation(MOD_ID, "test_cap"), new TestCapabilityProvider());
    }

    @SubscribeEvent
    public static void handleLivingEntityUseItem(LivingEntityUseItemEvent event)
    {
        event.getEntityLiving().getCapability(TEST_CAP).ifPresent(cap ->
        {
            if (!event.getEntity().getEntityWorld().isRemote())
            {
                cap.update();
            }
            logger.info("Current number: " + cap.number);
        });

    }

    private static class EmptyStorage<C> implements Capability.IStorage<C> {

        @Override
        public INBT writeNBT(Capability<C> capability, C instance, Direction side)
        {
            return null;
        }

        @Override
        public void readNBT(Capability<C> capability, C instance, Direction side, INBT nbt)
        {
        }
    }
}
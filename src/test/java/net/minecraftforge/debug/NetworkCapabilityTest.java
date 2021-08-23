package net.minecraftforge.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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

@Mod(NetworkCapabilityTest.ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NetworkCapabilityTest.ID)
public class NetworkCapabilityTest {

    public static final String ID = "network_capability_test";

    private static final Logger logger = LogManager.getLogger();

    @CapabilityInject(TestCapability.class)
    public static final Capability<TestCapability> TEST_CAP = null;

    public NetworkCapabilityTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NetworkCapabilityTest::handleCommonSetup);
    }

    private static void handleCommonSetup(FMLCommonSetupEvent event)
    {
        CapabilityManager.INSTANCE.register(TestCapability.class);
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
        public void write(FriendlyByteBuf out, boolean writeAll)
        {
            this.cap.ifPresent(cap ->
            {
                cap.dirty = false;
                out.writeVarInt(cap.number);
            });
        }

        @Override
        public void read(FriendlyByteBuf in)
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
    public static void handleAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        event.addCapability(new ResourceLocation(ID, "test_cap"), new TestCapabilityProvider());
    }

    @SubscribeEvent
    public static void handleLivingEntityUseItem(LivingEntityUseItemEvent event)
    {
        event.getItem().getCapability(TEST_CAP).ifPresent(cap ->
        {
            if (!event.getEntity().level.isClientSide())
            {
                cap.update();
            }
            logger.info("Current number: " + cap.number);
        });
    }
}
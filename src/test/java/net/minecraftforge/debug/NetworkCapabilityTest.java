/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.INetworkCapability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NetworkCapabilityTest.ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = NetworkCapabilityTest.ID)
public class NetworkCapabilityTest
{
    public static final String ID = "network_capability_test";

    private static final Logger logger = LogManager.getLogger();

    public static final Capability<TestCapability> TEST_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    public NetworkCapabilityTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(NetworkCapabilityTest::handleRegisterCapabilities);
    }

    private static void handleRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(TestCapability.class);
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
        public void writeCapabilities(FriendlyByteBuf out, boolean writeAll)
        {
            this.cap.ifPresent(cap ->
            {
                cap.dirty = false;
                out.writeVarInt(cap.number);
            });
        }

        @Override
        public void readCapabilities(FriendlyByteBuf in)
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
        event.addCapability(new ResourceLocation(ID, "test_cap"), new TestCapabilityProvider());
    }

    @SubscribeEvent
    public static void handleLivingEntityUseItem(LivingEntityUseItemEvent event)
    {
        event.getEntity().getCapability(TEST_CAP).ifPresent(cap ->
        {
            if (!event.getEntity().level.isClientSide())
            {
                cap.update();
                logger.info("Set new number: " + cap.number);
            }
        });
    }
}

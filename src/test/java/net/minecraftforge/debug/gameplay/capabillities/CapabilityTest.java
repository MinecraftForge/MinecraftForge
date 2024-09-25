package net.minecraftforge.debug.gameplay.capabillities;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityFactoryManager;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraftforge.debug.gameplay.capabillities.CapabilityTest.MOD_ID;

@Mod(MOD_ID)
public class CapabilityTest extends BaseTestMod {
    public static final String MOD_ID = "capability_test";

    public CapabilityTest() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(this::onInteract);

        CapabilityFactoryManager.getInstance().register(Pig.class, ResourceLocation.fromNamespaceAndPath("mc", "test"), e -> {
            return new MyProvider();
        });

        CapabilityFactoryManager.getInstance().register(Entity.class, ResourceLocation.fromNamespaceAndPath("mc", "test2"), e -> {
            return new ICapabilityProvider() {
                LazyOptional<IFluidHandler> HANDLER = LazyOptional.of(() -> new FluidTank(111));

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == ForgeCapabilities.FLUID_HANDLER)
                        return HANDLER.cast();
                    return LazyOptional.empty();
                }
            };
        });
    }

    public void onInteract(AttackEntityEvent event) {
        if (event.getTarget().level().isClientSide()) return; // check on server!
        var target = event.getTarget();
        var plr = event.getEntity();

        if (target.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
            plr.sendSystemMessage(Component.literal("HAS ENERGY!"));
        }
        if (target.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()) {
            plr.sendSystemMessage(Component.literal("HAS FLUID!"));
        }
    }


    public static class MyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final EnergyStorage STORAGE = new EnergyStorage(1000, 10, 10, 10);
        private final LazyOptional<EnergyStorage> STORAGE_LAZY = LazyOptional.of(() -> STORAGE);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ENERGY)
                return STORAGE_LAZY.cast();
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
            CompoundTag tag = new CompoundTag();
            tag.put("energy", STORAGE.serializeNBT(registryAccess));
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
            STORAGE.deserializeNBT(registryAccess, nbt.getCompound("energy"));
        }
    }

}

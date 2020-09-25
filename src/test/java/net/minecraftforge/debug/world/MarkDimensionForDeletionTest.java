/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

@Mod(MarkDimensionForDeletionTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MarkDimensionForDeletionTest.MODID)
public class MarkDimensionForDeletionTest
{

    public static final String MODID = "mark_dimension_for_deletion_test";

    private static final DeferredRegister<ModDimension> DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, MODID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

    private static final RegistryObject<ModDimension> DYNAMIC_DIMENSION_TYPE = DIMENSIONS.register(
            "dynamic_dimension",
            () -> ModDimension.withFactory(OverworldDimension::new)
    );
    private static final RegistryObject<Item> DIM_ITEM = ITEMS.register("dim_item", () -> new Item(new Item.Properties().group(ItemGroup.MISC))
    {
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn)
        {
            if (!worldIn.isRemote)
            {
                if (playerIn.isSneaking())
                {
                    playerIn.sendMessage(new StringTextComponent("You are in dimension " + worldIn.dimension.getType().getRegistryName()));
                }
                else
                {
                    DynamicDimensionCap cap = playerIn.getCapability(CAP).orElseThrow(IllegalStateException::new);
                    ITeleporter teleporter = new ITeleporter()
                    {
                        @Override
                        public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
                        {
                            return repositionEntity.apply(false);
                        }
                    };
                    if (cap.dimension == null)
                    {
                        ResourceLocation dimName = new ResourceLocation(MODID, "dynamic_" + playerIn.getUniqueID().toString() + "_" + UUID.randomUUID().toString());
                        cap.dimension = DimensionManager.registerDimension(dimName, DYNAMIC_DIMENSION_TYPE.get(), null, true);
                        DimensionManager.initWorld(worldIn.getServer(), cap.dimension);
                        playerIn.changeDimension(cap.dimension, teleporter);
                    }
                    else if (playerIn.dimension == cap.dimension)
                    {
                        playerIn.changeDimension(DimensionType.OVERWORLD, teleporter);
                        DimensionManager.markForDeletion(cap.dimension);
                    }
                    else
                    {
                        playerIn.changeDimension(cap.dimension, teleporter);
                    }
                }
            }
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        }
    });

    @CapabilityInject(DynamicDimensionCap.class)
    public static Capability<DynamicDimensionCap> CAP;

    public MarkDimensionForDeletionTest()
    {
        DIMENSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event)
    {
        CapabilityManager.INSTANCE.register(DynamicDimensionCap.class, new DynamicDimensionCap.Storage(), DynamicDimensionCap::new);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = MODID)
    public static class ForgeEventSubscriber
    {

        @SubscribeEvent
        public static void attachCaps(AttachCapabilitiesEvent<Entity> event)
        {
            if (event.getObject() instanceof ServerPlayerEntity)
            {
                event.addCapability(new ResourceLocation(MODID, "dynamic_dimension"), new DynamicDimensionCap());
            }
        }

    }

    public static class DynamicDimensionCap implements ICapabilitySerializable<CompoundNBT>
    {

        static class Storage implements Capability.IStorage<DynamicDimensionCap>
        {

            @Nullable
            @Override
            public INBT writeNBT(Capability<DynamicDimensionCap> capability, DynamicDimensionCap instance, Direction side)
            {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<DynamicDimensionCap> capability, DynamicDimensionCap instance, Direction side, INBT nbt)
            {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }

        public DimensionType dimension = null;

        private final LazyOptional<DynamicDimensionCap> instance = LazyOptional.of(() -> this);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            return CAP.orEmpty(cap, instance);
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            CompoundNBT result = new CompoundNBT();
            if (dimension != null && dimension.getRegistryName() != null)
            {
                result.putString("dimension", dimension.getRegistryName().toString());
            }
            return result;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt)
        {
            if (nbt.contains("dimension", Constants.NBT.TAG_STRING))
            {
                dimension = DimensionType.byName(new ResourceLocation(nbt.getString("dimension")));
            }
            else
            {
                dimension = null;
            }
        }
    }
}

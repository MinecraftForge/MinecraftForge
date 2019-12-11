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

package net.minecraftforge.debug.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;

@Mod(NewFluidTest.MODID)
public class NewFluidTest
{
    public static final String MODID = "new_fluid_test";

    public static final Material FLUID_STILL = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("minecraft:block/brown_mushroom_block"));
    public static final Material FLUID_FLOWING = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("minecraft:block/mushroom_stem"));

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, MODID);

    public static RegistryObject<FlowingFluid> test_fluid = FLUIDS.register("test_fluid", () ->
            new ForgeFlowingFluid.Source(NewFluidTest.test_fluid_properties)
    );
    public static RegistryObject<FlowingFluid> test_fluid_flowing = FLUIDS.register("test_fluid_flowing", () ->
            new ForgeFlowingFluid.Flowing(NewFluidTest.test_fluid_properties)
    );

    public static RegistryObject<FlowingFluidBlock> test_fluid_block = BLOCKS.register("test_fluid_block", () ->
            new FlowingFluidBlock(test_fluid, Block.Properties.create(net.minecraft.block.material.Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())
    );
    public static RegistryObject<Item> test_fluid_bucket = ITEMS.register("test_fluid_bucket", () ->
            new BucketItem(test_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC))
    );

    public static final ForgeFlowingFluid.Properties test_fluid_properties =
            new ForgeFlowingFluid.Properties(test_fluid, test_fluid_flowing, FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING).color(0x3F1080FF))
                    .bucket(test_fluid_bucket).block(test_fluid_block);

    public NewFluidTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::loadComplete);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        // some sanity checks
        BlockState state = Fluids.WATER.getDefaultState().getBlockState();
        BlockState state2 = Fluids.WATER.getAttributes().getBlock(null,null,Fluids.WATER.getDefaultState());
        Validate.isTrue(state.getBlock() == Blocks.WATER && state2 == state);
        ItemStack stack = Fluids.WATER.getAttributes().getBucket(new FluidStack(Fluids.WATER, 1));
        Validate.isTrue(stack.getItem() == Fluids.WATER.getFilledBucket());
    }
}

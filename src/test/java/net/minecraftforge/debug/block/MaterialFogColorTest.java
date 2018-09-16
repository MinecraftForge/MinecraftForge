/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@EventBusSubscriber
@Mod(modid = MaterialFogColorTest.MODID, name = "FogColor inside material debug.", version = "1.0", acceptableRemoteVersions = "*")
public class MaterialFogColorTest
{
    static final boolean ENABLED = false; // <-- enable mod
    static int color = 0xFFd742f4; // <-- change value for testing

    public static final String MODID = "fogcolorinsidematerialtest";

    static
    {
        if (ENABLED)
        {
            FluidRegistry.enableUniversalBucket();
        }
    }

    public static final Fluid SLIME = new Fluid("slime", new ResourceLocation(MODID, "slime_still"), new ResourceLocation(MODID, "slime_flow"), new ResourceLocation(MODID, "slime_overlay")) {
        @Override
        public int getColor()
        {
            return color;
        }
    };

    @ObjectHolder("slime")
    public static final BlockFluidBase SLIME_BLOCK = null;
    @ObjectHolder("test_fluid")
    public static final Block FLUID_BLOCK = null;
    @ObjectHolder("test_fluid")
    public static final Item FLUID_ITEM = null;

    private static final ResourceLocation RES_LOC = new ResourceLocation(MODID, "slime");
    private static final ResourceLocation testFluidRegistryName = new ResourceLocation(MODID, "test_fluid");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            FluidRegistry.registerFluid(SLIME);
            FluidRegistry.addBucketForFluid(SLIME);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register((new BlockFluidClassic(SLIME, Material.WATER)).setRegistryName(RES_LOC).setUnlocalizedName(RES_LOC.toString()));
            Fluid fluid = new Fluid("fog_test", new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow"), new ResourceLocation("blocks/water_overlay"));
            FluidRegistry.registerFluid(fluid);
            Block fluidBlock = new BlockFluidClassic(fluid, Material.WATER)
            {
                @Override
                public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
                {
                    return new Vec3d(0.6F, 0.1F, 0.0F);
                }
            };
            event.getRegistry().register(fluidBlock.setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setUnlocalizedName(MODID + ".test_fluid").setRegistryName(testFluidRegistryName));
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if (ENABLED)
        {
            event.getRegistry().register(new ItemBlock(FLUID_BLOCK).setRegistryName(testFluidRegistryName));
        }
    }

    @EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLED)
            {
                ModelResourceLocation fluidLocation = new ModelResourceLocation(testFluidRegistryName, "fluid");
                ModelLoader.registerItemVariants(FLUID_ITEM);
                ModelLoader.setCustomMeshDefinition(FLUID_ITEM, stack -> fluidLocation);
                ModelLoader.setCustomStateMapper(FLUID_BLOCK, new StateMapperBase() {
                    @Override
                    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                    {
                        return fluidLocation;
                    }
                });
            }
        }
    }
}

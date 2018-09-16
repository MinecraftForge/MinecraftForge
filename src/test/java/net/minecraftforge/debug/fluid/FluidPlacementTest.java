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

package net.minecraftforge.debug.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.debug.client.model.ModelFluidTest;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

@Mod(modid = FluidPlacementTest.MODID, name = "ForgeDebugFluidPlacement", version = FluidPlacementTest.VERSION, acceptableRemoteVersions = "*")
public class FluidPlacementTest
{
    public static final String MODID = "forgedebugfluidplacement";
    public static final String VERSION = "1.0";

    public static final boolean ENABLE = true;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            if (!ENABLE || !ModelFluidTest.ENABLE)
                return;
            event.getRegistry().registerAll(
                FiniteFluidBlock.instance
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLE || !ModelFluidTest.ENABLE)
                return;
            FluidRegistry.registerFluid(FiniteFluid.instance);
            FluidRegistry.addBucketForFluid(FiniteFluid.instance);
            event.getRegistry().registerAll(
                EmptyFluidContainer.instance,
                FluidContainer.instance,
                new FluidItemBlock(FiniteFluidBlock.instance).setRegistryName(FiniteFluidBlock.instance.getRegistryName())
            );
            MinecraftForge.EVENT_BUS.register(FluidContainer.instance);
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (!ENABLE || !ModelFluidTest.ENABLE)
                return;
            ModelResourceLocation fluidLocation = new ModelResourceLocation(MODID.toLowerCase() + ":" + FiniteFluidBlock.name, "normal");

            Item fluid = Item.getItemFromBlock(FiniteFluidBlock.instance);
            ModelLoader.setCustomModelResourceLocation(EmptyFluidContainer.instance, 0, new ModelResourceLocation("forge:bucket", "inventory"));
            ModelLoader.setBucketModelDefinition(FluidContainer.instance);
            // no need to pass the locations here, since they'll be loaded by the block model logic.
            ModelBakery.registerItemVariants(fluid);
            ModelLoader.setCustomMeshDefinition(fluid, stack -> fluidLocation);
            ModelLoader.setCustomStateMapper(FiniteFluidBlock.instance, new StateMapperBase()
            {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return fluidLocation;
                }
            });
        }
    }

    public static final class FiniteFluid extends Fluid
    {
        public static final String name = "finitefluid";
        public static final FiniteFluid instance = new FiniteFluid();

        private FiniteFluid()
        {
            super(name, new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow"), new ResourceLocation("blocks/water_overlay"));
        }

        @Override
        public int getColor()
        {
            return 0xFFFFFF00;
        }

        @Override
        public String getLocalizedName(FluidStack stack)
        {
            return "Finite Fluid";
        }
    }

    public static final class FiniteFluidBlock extends BlockFluidFinite
    {
        public static final FiniteFluidBlock instance = new FiniteFluidBlock();
        public static final String name = "finite_fluid_block";

        private FiniteFluidBlock()
        {
            super(FiniteFluid.instance, Material.WATER);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(MODID, name);
        }
    }

    public static final class FluidItemBlock extends ItemBlock
    {
        FluidItemBlock(BlockFluidBase block)
        {
            super(block);
        }

        @Override
        public BlockFluidBase getBlock()
        {
            return (BlockFluidBase) super.getBlock();
        }

        @Override
        public int getMetadata(int damage)
        {
            return getBlock().getMaxRenderHeightMeta();
        }
    }

    public static final class EmptyFluidContainer extends ItemBucket
    {
        public static final EmptyFluidContainer instance = new EmptyFluidContainer();
        public static final String name = "empty_fluid_container";

        private EmptyFluidContainer()
        {
            super(Blocks.AIR);
            setRegistryName(MODID, name);
            setUnlocalizedName(MODID + ":" + name);
            setMaxStackSize(16);
        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
        {
            return new EmptyContainerHandler(stack);
        }

        private static final class EmptyContainerHandler extends FluidBucketWrapper
        {
            public EmptyContainerHandler(@Nonnull ItemStack container)
            {
                super(container);
            }

            @Override
            public int fill(FluidStack resource, boolean doFill)
            {
                if (container.getCount() != 1 || resource == null || resource.amount > Fluid.BUCKET_VOLUME || container
                        .getItem() instanceof ItemBucketMilk || getFluid() != null || !canFillFluidType(resource))
                {
                    return 0;
                }

                if (doFill)
                {
                    container = new ItemStack(FluidContainer.instance);
                    NBTTagCompound tag = new NBTTagCompound();
                    NBTTagCompound fluidTag = new NBTTagCompound();
                    resource.writeToNBT(fluidTag);
                    tag.setTag(FLUID_NBT_KEY, fluidTag);
                    container.setTagCompound(tag);
                }

                return resource.amount;
            }
        }
    }

    public static final class FluidContainer extends UniversalBucket
    {
        public static final FluidContainer instance = new FluidContainer();
        public static final String name = "fluid_container";

        private FluidContainer()
        {
            super(1000, new ItemStack(EmptyFluidContainer.instance), false);
            setCreativeTab(CreativeTabs.MISC);
            setRegistryName(MODID, name);
            setUnlocalizedName(MODID + ":" + name);
        }

        @Nonnull
        @Override
        public String getItemStackDisplayName(@Nonnull ItemStack stack)
        {
            FluidStack fluid = getFluid(stack);
            if (fluid == null || fluid.getFluid() == null)
            {
                return "Empty Variable Container";
            }
            return "Variable Container (" + getFluid(stack).getLocalizedName() + ")";
        }

        @Override
        @Nullable
        public FluidStack getFluid(ItemStack container)
        {
            container = container.copy();
            if (container.getTagCompound() != null)
            {
                container.setTagCompound(container.getTagCompound().getCompoundTag(FLUID_NBT_KEY));
            }
            return super.getFluid(container);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced)
        {
            FluidStack fluid = getFluid(stack);
            if (fluid != null)
            {
                tooltip.add(fluid.amount + "/1000");
            }
        }

        @Override
        public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems)
        {
            if (!this.isInCreativeTab(tab))
                return;
            Fluid[] fluids = new Fluid[]{FluidRegistry.WATER, FluidRegistry.LAVA, FiniteFluid.instance, ModelFluidTest.FLUID};
            // add 16 variable fillings
            for (Fluid fluid : fluids)
            {
                for (int amount = 125; amount <= 1000; amount += 125)
                {
                    for (int offset = 63; offset >= 0; offset -= 63)
                    {
                        FluidStack fs = new FluidStack(fluid, amount - offset);
                        ItemStack stack = new ItemStack(this);
                        NBTTagCompound tag = stack.getTagCompound();
                        if (tag == null)
                        {
                            tag = new NBTTagCompound();
                        }
                        NBTTagCompound fluidTag = new NBTTagCompound();
                        fs.writeToNBT(fluidTag);
                        tag.setTag(FLUID_NBT_KEY, fluidTag);
                        stack.setTagCompound(tag);
                        subItems.add(stack);
                    }
                }
            }
        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
        {
            return new FluidHandlerItemStack.SwapEmpty(stack, new ItemStack(EmptyFluidContainer.instance), 1000);
        }
    }
}
